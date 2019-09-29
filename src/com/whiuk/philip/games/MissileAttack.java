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
import java.util.Random;

@SuppressWarnings("serial")
class MissileAttack extends JFrame {
    private Random random = new Random();

    abstract class Sprite {
        int x;
        int y;
        ImageIcon icon;
        Image image;
        Sprite(int x, int y, String spriteFile) {
            this.x = x;
            this.y = y;
            this.icon = new ImageIcon(this.getClass().getResource(spriteFile));
            image = icon.getImage();
        }
        public Image getImage() {
            return image;
        }
    }

	class Board extends GameBoard {

	    private Timer timer;
	    private Craft craft;
	    private ArrayList<Alien> aliens;
	    private boolean inGame;
	    private int level;
	    private int score;
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

        Board() {
	        addKeyListener(new TAdapter());
	        setFocusable(true);
	        setBackground(Color.BLACK);
	        setDoubleBuffered(true);
	        setSize(400, 300);
            startGame();
	        timer = new Timer(5, this);
	        timer.start();
	    }

	    private void startGame() {
            inGame = true;
            level = 1;
            score = 0;
            craft = new Craft();
            initAliens(level);
        }

        private void nextLevel() {
            level++;
            initAliens(level);
        }

	    @Override
        public final void addNotify() {
	        super.addNotify();
	        B_WIDTH = getWidth();
	        B_HEIGHT = getHeight();
	    }

	    final void initAliens(int level) {
	        aliens = new ArrayList<>();
			for (int[] initialAlienPosition : initialAlienPositions) {
				aliens.add(new Alien(initialAlienPosition[0], initialAlienPosition[1]));
			}
	    }
	    @Override
	    public void paint(Graphics g) {
	        super.paint(g);

	        if (inGame) {

	            Graphics2D g2d = (Graphics2D)g;

	            if (craft.isVisible()) {
                    g2d.setColor(Color.PINK);
                    g2d.draw(craft.getBounds());
                    g2d.drawImage(craft.getImage(), craft.getX(), craft.getY(),
                            this);
                }

				for (Missile missile : craft.getMissiles()) {
					g2d.drawImage(missile.getImage(), missile.getX(), missile.getY(), this);
				}

				for (Alien alien : aliens) {
					if (alien.isVisible()) {
                        g2d.draw(alien.getBounds());
						g2d.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
					}
				}

	            g2d.setColor(Color.WHITE);
	            g2d.drawString("Level: " + level, 5, 15);
                g2d.drawString("Score: " + score, 5, 30);


	        } else {
				Font font = new Font("Helvetica", Font.BOLD, 14);
				FontMetrics metrics = this.getFontMetrics(font);
				g.setColor(Color.white);
				g.setFont(font);

	            String msg = "Game Over";
				String playAgain = "Press ENTER to play again";

	            g.drawString(msg, (B_WIDTH - metrics.stringWidth(msg)) / 2,
	                         B_HEIGHT / 2);
				g.drawString(playAgain, (B_WIDTH - metrics.stringWidth(playAgain)) / 2,
						(B_HEIGHT / 2) + 20);
	        }

	        Toolkit.getDefaultToolkit().sync();
	        g.dispose();
	    }

        @Override
	    public final void actionPerformed(final ActionEvent e) {

	        if (aliens.size() == 0) {
	            nextLevel();
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
                    score += 50;
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
                    inGame = false;
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
	            if (inGame) {
                    craft.keyPressed(e);
                } else {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                    startGame();
                    }
                }
	        }
	    }
	}

	class Craft extends Sprite {
	    private int dx;
	    private int dy;
	    private int width;
	    private int height;
	    private boolean visible;
	    private ArrayList<Missile> missiles;

	    Craft() {
	        super(40, 60, "craft.png");
	        width = image.getWidth(null)-2;
	        height = image.getHeight(null)-2;
	        missiles = new ArrayList<>();
	        visible = true;
	    }

	    void move() {

	        x += dx;
	        y += dy;

	        if (x < 1) {
	            x = 1;
	        }

	        if (y < 1) {
	            y = 1;
	        }
	    }

	    int getX() {
	        return x;
	    }

        int getY() {
	        return y;
	    }

	    ArrayList<Missile> getMissiles() {
	        return missiles;
	    }

	    void setVisible(boolean visible) {
	        this.visible = visible;
	    }

	    boolean isVisible() {
	        return visible;
	    }

	    Rectangle getBounds() {
	        return new Rectangle(x, y+1, width, height);
	    }

	    void keyPressed(KeyEvent e) {

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

	    void keyReleased(KeyEvent e) {
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

	class Alien extends Sprite {
	    private int width;
	    private int height;
	    private boolean visible;

	    Alien(int x, int y) {
	        super(x, y, "alien.png");
	        width = image.getWidth(null)-1;
	        height = image.getHeight(null)-1;
	        visible = true;
	    }

	    public void move() {
	        if (x < 0) {
                x = 400;
                y = random.nextInt(250);
            }
	        x -= 1;
	    }

        public int getX() {
	        return x;
	    }

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

	class Missile extends Sprite {
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
	        super(x, y, "missile.png");
	        alive = true;
	        width = image.getWidth(null);
	        height = image.getHeight(null);
	    }

	    public int getX() {
	        return x;
	    }

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

	    public void move() {
	        x += MISSILE_SPEED;
	        if (x > BOARD_WIDTH) {
	            alive = false;
	        }
	    }
	}

	MissileAttack() {
        add(new Board());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("Missile Attack");
        setResizable(false);
        setVisible(true);
    }
}
