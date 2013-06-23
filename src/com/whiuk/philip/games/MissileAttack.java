package com.whiuk.philip.games;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Missile attack game.
 * @author Philip
 *
 */
@SuppressWarnings("serial")
public class MissileAttack extends JFrame {
    /**
     * Indicates the object can move.
     * @author Philip
     *
     */
    public interface Moveable {
        /**
         * Move the object based on it's direction and speed.
         */
        public void move();
    }

    /**
     * Represents a game sprite.
     * @author Philip
     *
     */
    public abstract class Sprite {
        /**
         * X Position.
         */
        protected int x;
        /**
         * Y Position.
         */
        protected int y;
        /**
         * Constructor.
         * @param x X Position
         * @param y Y Position
         */
        public Sprite(int x, int y) {
            this.x = x;
            this.y = y;
        }
        /**
         * @return the sprite's image
         */
        public abstract Image getImage();
        /**
         * @return the sprite's x position
         */
        public abstract int getX();
        /**
         * @return the sprite's y position
         */
        public abstract int getY();
    }

    /**
     * Game board.
     * @author Philip
     *
     */
	public class Board extends GameBoard {

	    private Timer timer;
	    private Craft craft;
	    private ArrayList<Alien> aliens;
	    private boolean ingame;
	    private int B_WIDTH;
	    private int B_HEIGHT;

	    private int[][] pos = { 
	        {2380, 29}, {2500, 59}, {1380, 89},
	        {780, 109}, {580, 139}, {680, 239}, 
	        {790, 259}, {760, 50}, {790, 150},
	        {980, 209}, {560, 45}, {510, 70},
	        {930, 159}, {590, 80}, {530, 60},
	        {940, 59}, {990, 30}, {920, 200},
	        {900, 259}, {660, 50}, {540, 90},
	        {810, 220}, {860, 20}, {740, 180},
	        {820, 128}, {490, 170}, {700, 30}
	     };
	    /**
	     * Constructor.
	     */
	    public Board() {

	        addKeyListener(new TAdapter());
	        setFocusable(true);
	        setBackground(Color.BLACK);
	        setDoubleBuffered(true);
	        ingame = true;

	        setSize(400, 300);

	        craft = new Craft();

	        initAliens();

	        timer = new Timer(5, this);
	        timer.start();
	    }
	    @Override
        public final void addNotify() {
	        super.addNotify();
	        B_WIDTH = getWidth();
	        B_HEIGHT = getHeight();
	    }
	    /**
	     * Initialise aliens.
	     */
	    public final void initAliens() {
	        aliens = new ArrayList<Alien>();

	        for (int i = 0; i < pos.length; i++) {
	            aliens.add(new Alien(pos[i][0], pos[i][1]));
	        }
	    }
	    @Override
	    public void paint(Graphics g) {
	        super.paint(g);

	        if (ingame) {

	            Graphics2D g2d = (Graphics2D)g;

	            if (craft.isVisible())
	                g2d.drawImage(craft.getImage(), craft.getX(), craft.getY(),
	                              this);

	            ArrayList<Missile> ms = craft.getMissiles();

	            for (int i = 0; i < ms.size(); i++) {
	                Missile m = (Missile)ms.get(i);
	                g2d.drawImage(m.getImage(), m.getX(), m.getY(), this);
	            }

	            for (int i = 0; i < aliens.size(); i++) {
	                Alien a = (Alien)aliens.get(i);
	                if (a.isVisible())
	                    g2d.drawImage(a.getImage(), a.getX(), a.getY(), this);
	            }

	            g2d.setColor(Color.WHITE);
	            g2d.drawString("Aliens left: " + aliens.size(), 5, 15);


	        } else {
	            String msg = "Game Over";
	            Font small = new Font("Helvetica", Font.BOLD, 14);
	            FontMetrics metr = this.getFontMetrics(small);

	            g.setColor(Color.white);
	            g.setFont(small);
	            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2,
	                         B_HEIGHT / 2);
	        }

	        Toolkit.getDefaultToolkit().sync();
	        g.dispose();
	    }
        @Override
	    public final void actionPerformed(final ActionEvent e) {

	        if (aliens.size() == 0) {
	            ingame = false;
	        }

	        ArrayList<Missile> ms = craft.getMissiles();

	        for (int i = 0; i < ms.size(); i++) {
	            Missile m = (Missile) ms.get(i);
	            if (m.isVisible()) 
	                m.move();
	            else ms.remove(i);
	        }

	        for (int i = 0; i < aliens.size(); i++) {
	            Alien a = (Alien) aliens.get(i);
	            if (a.isVisible()) 
	                a.move();
	            else aliens.remove(i);
	        }

	        craft.move();
	        checkCollisions();
	        repaint();  
	    }
        /**
         * Check for collisions.
         */
	    public void checkCollisions() {

	        Rectangle r3 = craft.getBounds();

	        for (int j = 0; j < aliens.size(); j++) {
	            Alien a = (Alien) aliens.get(j);
	            Rectangle r2 = a.getBounds();

	            if (r3.intersects(r2)) {
	                craft.setVisible(false);
	                a.setVisible(false);
	                ingame = false;
	            }
	        }

	        ArrayList<Missile> ms = craft.getMissiles();

	        for (int i = 0; i < ms.size(); i++) {
	            Missile m = (Missile) ms.get(i);

	            Rectangle r1 = m.getBounds();

	            for (int j = 0; j < aliens.size(); j++) {
	                Alien a = (Alien) aliens.get(j);
	                Rectangle r2 = a.getBounds();

	                if (r1.intersects(r2)) {
	                    m.setVisible(false);
	                    a.setVisible(false);
	                }
	            }
	        }
	    }
	    /**
	     * Implementation of adapter.
	     * @author Philip
	     *
	     */
	    private class TAdapter extends KeyAdapter {
	        @Override
	        public void keyReleased(final KeyEvent e) {
	            craft.keyReleased(e);
	        }
            @Override
	        public void keyPressed(final KeyEvent e) {
	            craft.keyPressed(e);
	        }
	    }
	}
	/**
	 * Player craft.
	 * @author Philip
	 *
	 */
	public class Craft extends Sprite implements Moveable {

	    private String craft = "craft.png";

	    private int dx;
	    private int dy;
	    private int width;
	    private int height;
	    private boolean visible;
	    private Image image;
	    private ArrayList<Missile> missiles;

	    /**
	     * Default constructor.
	     */
	    public Craft() {
	        super(40, 60);
	        ImageIcon ii = new ImageIcon(this.getClass().getResource(craft));
	        image = ii.getImage();
	        width = image.getWidth(null);
	        height = image.getHeight(null);
	        missiles = new ArrayList<Missile>();
	        visible = true;
	    }
	    @Override
	    public void move() {

	        x += dx;
	        y += dy;

	        if (x < 1) {
	            x = 1;
	        }

	        if (y < 1) {
	            y = 1;
	        }
	    }
	    @Override
	    public int getX() {
	        return x;
	    }

        @Override
	    public int getY() {
	        return y;
	    }

        @Override
	    public Image getImage() {
	        return image;
	    }

        /**
         * @return list of missiles.
         */
	    public ArrayList<Missile> getMissiles() {
	        return missiles;
	    }

	    /**
	     * @param visible Visibility of craft
	     */
	    public void setVisible(boolean visible) {
	        this.visible = visible;
	    }

	    /**
	     * @return Visibility of craft
	     */
	    public boolean isVisible() {
	        return visible;
	    }

	    /**
	     * 
	     * @return Collision bounds
	     */
	    public Rectangle getBounds() {
	        return new Rectangle(x, y, width, height);
	    }

	    /**
	     * Responds to key presses
	     * @param e Key press event
	     */
	    public void keyPressed(KeyEvent e) {

	        int key = e.getKeyCode();

	        if (key == KeyEvent.VK_SPACE) {
	            fire();
	        }

	        if (key == KeyEvent.VK_LEFT) {
	            dx = -1;
	        }

	        if (key == KeyEvent.VK_RIGHT) {
	            dx = 1;
	        }

	        if (key == KeyEvent.VK_UP) {
	            dy = -1;
	        }

	        if (key == KeyEvent.VK_DOWN) {
	            dy = 1;
	        }
	    }

	    /**
	     * Fires a missile.
	     */
	    public void fire() {
	        missiles.add(new Missile(x + width, y + height / 2));
	    }


        /**
         * Responds to key releases
         * @param e Key release event
         */
	    public void keyReleased(KeyEvent e) {
	        int key = e.getKeyCode();

	        if (key == KeyEvent.VK_LEFT) {
	            dx = 0;
	        }

	        if (key == KeyEvent.VK_RIGHT) {
	            dx = 0;
	        }

	        if (key == KeyEvent.VK_UP) {
	            dy = 0;
	        }

	        if (key == KeyEvent.VK_DOWN) {
	            dy = 0;
	        }
	    }
	}
	/**
	 * Alien craft
	 * @author Philip
	 *
	 */
	public class Alien extends Sprite implements Moveable {
	    private String craft = "alien.png";
	    private int width;
	    private int height;
	    private boolean visible;
	    private Image image;

	    /**
	     * Alien constructor.
	     * @param x X Position
	     * @param y Y Position
	     */
	    public Alien(int x, int y) {
	        super(x, y);
	        ImageIcon ii = new ImageIcon(this.getClass().getResource(craft));
	        image = ii.getImage();
	        width = image.getWidth(null);
	        height = image.getHeight(null);
	        visible = true;
	    }

        @Override
	    public void move() {
	        if (x < 0) 
	            x = 400;
	        x -= 1;
	    }

        @Override
	    public int getX() {
	        return x;
	    }

        @Override
	    public int getY() {
	        return y;
	    }

        /**
         * @return Visibility of alien craft
         */
	    public boolean isVisible() {
	        return visible;
	    }

	    /**
	     * 
	     * @param visible Visibility of alien craft
	     */
	    public void setVisible(Boolean visible) {
	        this.visible = visible;
	    }
	    @Override
	    public Image getImage() {
	        return image;
	    }
	    /**
	     * @return Bounds of alien craft.
	     */
	    public Rectangle getBounds() {
	        return new Rectangle(x, y, width, height);
	    }
	}
	/**
	 * Missile
	 * @author Philip
	 *
	 */
	public class Missile extends Sprite implements Moveable {
	    private Image image;
	    boolean visible;
	    private int width, height;

	    private final int BOARD_WIDTH = 390;
	    private final int MISSILE_SPEED = 2;

	    /**
	     * Constructor.
	     * @param x X Position
	     * @param y Y Position
	     */
	    public Missile(int x, int y) {
	        super(x, y);
	        ImageIcon ii =
	            new ImageIcon(this.getClass().getResource("missile.png"));
	        image = ii.getImage();
	        visible = true;
	        width = image.getWidth(null);
	        height = image.getHeight(null);
	    }

	    @Override
	    public Image getImage() {
	        return image;
	    }

	    @Override
        public int getX() {
	        return x;
	    }

        @Override
	    public int getY() {
	        return y;
	    }

        /**
         * 
         * @return Visibility of missile
         */
	    public final boolean isVisible() {
	        return visible;
	    }
	    /**
	     * 
	     * @param visible Visibility of missile
	     */
	    public final void setVisible(Boolean visible) {
	        this.visible = visible;
	    }
	    /**
	     * 
	     * @return Collision bounds
	     */
	    public final Rectangle getBounds() {
	        return new Rectangle(x, y, width, height);
	    }
	    @Override
	    public void move() {
	        x += MISSILE_SPEED;
	        if (x > BOARD_WIDTH) {
	            visible = false;
	        }
	    }
	}
	/**
	 * Constructor.
	 */
	public MissileAttack() {
        add(new Board());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("Missile Attack");
        setResizable(false);
        setVisible(true);
    }
}
