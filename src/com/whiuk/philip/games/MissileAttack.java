package com.whiuk.philip.games;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.util.Iterator;

@SuppressWarnings("serial")
public class MissileAttack extends JFrame {
    public interface Moveable {
		void move();
    }
    public abstract class Sprite {
        protected int x;
        protected int y;
        public Sprite(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public abstract Image getImage();
        public abstract int getX();
        public abstract int getY();
    }
	public class Board extends GameBoard {

	    private Timer timer;
	    private Craft craft;
	    private ArrayList<Alien> aliens;
	    private boolean ingame;
	    private int B_WIDTH;
	    private int B_HEIGHT;

	    private int[][] initialAlienPositions = {
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

	    final void initAliens() {
	        aliens = new ArrayList<>();
			for (int[] initialAlienPosition : initialAlienPositions) {
				aliens.add(new Alien(initialAlienPosition[0], initialAlienPosition[1]));
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

				for (Missile missile : craft.getMissiles()) {
					g2d.drawImage(missile.getImage(), missile.getX(), missile.getY(), this);
				}

				for (Alien alien : aliens) {
					if (alien.isVisible()) {
						g2d.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
					}
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

	        Iterator<Missile> missileIterator = craft.getMissiles().iterator();
	        while(missileIterator.hasNext()) {
                Missile m = missileIterator.next();
                if (m.isAlive())
                    m.move();
                else missileIterator.remove();
            }

            Iterator<Alien> alienIterator = aliens.iterator();
	        while (alienIterator.hasNext()) {
                Alien a = alienIterator.next();
                if (a.isVisible()) {
                    a.move();
                } else {
                    alienIterator.remove();
                }
            }

	        craft.move();
	        checkCollisions();
	        repaint();  
	    }

        void checkCollisions() {

	        Rectangle craftBounds = craft.getBounds();

            for (Alien alien : aliens) {
                Rectangle alienBounds = alien.getBounds();

                if (craftBounds.intersects(alienBounds)) {
                    craft.setVisible(false);
                    alien.setVisible(false);
                    ingame = false;
                }
            }

	        ArrayList<Missile> ms = craft.getMissiles();

            for (Missile missile : ms) {

                Rectangle missileBounds = missile.getBounds();

                for (Alien alien : aliens) {
                    Rectangle r2 = alien.getBounds();

                    if (missileBounds.intersects(r2)) {
                        missile.setAlive(false);
                        alien.setVisible(false);
                    }
                }
            }
	    }

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

	public class Craft extends Sprite implements Moveable {

	    private String craft = "craft.png";

	    private int dx;
	    private int dy;
	    private int width;
	    private int height;
	    private boolean visible;
	    private Image image;
	    private ArrayList<Missile> missiles;

	    public Craft() {
	        super(40, 60);
	        ImageIcon ii = new ImageIcon(this.getClass().getResource(craft));
	        image = ii.getImage();
	        width = image.getWidth(null);
	        height = image.getHeight(null);
	        missiles = new ArrayList<>();
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
	        char keyChar = e.getKeyChar();

	        if (key == KeyEvent.VK_SPACE) {
	            fire();
	        }

	        if (key == KeyEvent.VK_LEFT || keyChar == 'a' || keyChar == 'd') {
	            dx = -1;
	        }

	        if (key == KeyEvent.VK_RIGHT || keyChar == 'd' || keyChar == 'D') {
	            dx = 1;
	        }

	        if (key == KeyEvent.VK_UP || keyChar == 'w' || keyChar == 'W') {
	            dy = -1;
	        }

	        if (key == KeyEvent.VK_DOWN || keyChar == 's' || keyChar == 'S') {
	            dy = 1;
	        }
	    }

        void fire() {
	        missiles.add(new Missile(x + width, y + height / 2));
	    }

	    public void keyReleased(KeyEvent e) {
	        int key = e.getKeyCode();
	        char keyChar = e.getKeyChar();

	        if (key == KeyEvent.VK_LEFT || keyChar == 'a' || keyChar == 'A') {
	            dx = 0;
	        }

	        if (key == KeyEvent.VK_RIGHT || keyChar == 'd' || keyChar == 'D') {
	            dx = 0;
	        }

	        if (key == KeyEvent.VK_UP || keyChar == 'w' || keyChar == 'W') {
	            dy = 0;
	        }

	        if (key == KeyEvent.VK_DOWN || keyChar == 's' || keyChar == 'S') {
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
	    boolean alive;
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
	        alive = true;
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

	    public final boolean isAlive() {
	        return alive;
	    }

	    public final void setAlive(Boolean alive) {
	        this.alive = alive;
	    }

	    public final Rectangle getBounds() {
	        return new Rectangle(x, y, width, height);
	    }

	    @Override
	    public void move() {
	        x += MISSILE_SPEED;
	        if (x > BOARD_WIDTH) {
	            alive = false;
	        }
	    }
	}

	public MissileAttack() {
        add(new Board());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("Missile Attack");
        setResizable(false);
        setVisible(true);
    }
}
