package com.whiuk.philip.games;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Apeiron clone.
 * @author Philip
 */
@SuppressWarnings("serial")
public class Apeiron extends JFrame {
    /**
     * Game board.
     * @author Philip
     */
    private class Board extends GameBoard {
        /**
         * T-Adapter.
         * @author Philip
         */
        private class TAdapter extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {
                craft.keyPressed(e);
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                craft.keyReleased(e);
            }
        }

        /**
         * The current mushroom field.
         */
        ArrayList<Mushroom> mushrooms;
        /**
         * All the centipedes on the current level.
         */
        ArrayList<Centipede> centipedes;
        /**
         * The player's craft.
         */
        Craft craft;
        /**
         *
         */
        boolean ingame;
        /**
         *
         */
        Timer timer;
        /**
         *
         */
        private int level;
        /**
         *
         */
        private int boardWidth;
        /**
         *
         */
        private int boardHeight;

        /**
         * 
         */
        public Board() {
            addKeyListener(new TAdapter());
            setFocusable(true);
            setBackground(Color.BLACK);
            setDoubleBuffered(true);
            ingame = true;
            setSize(400, 300);
            craft = new Craft();
            initMushrooms();
            initLevel();
            timer = new Timer(5, this);
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (centipedes.size() == 0) {
                levelUp();
            }
            for (int i = 0; i < craft.getBullets().size(); i++) {
                Bullet b = craft.getBullets().get(i);
                if (b.isVisible()) {
                    b.move();
                } else {
                    craft.getBullets().remove(i);
                }
            }
            for (int i = 0; i < centipedes.size(); i++) {
                final Centipede c = centipedes.get(i);
                if (c.getLength() > 0) {
                    c.move();
                } else {
                    centipedes.remove(i);
                }
            }
            craft.move();
            checkCollisions();
            repaint();
        }

        private void checkCollisions() {
            // Craft and Centipedes
            final Rectangle r3 = craft.getBounds();

            for (int j = 0; j < centipedes.size(); j++) {
                final Centipede a = centipedes.get(j);
                for (int k = 0; k < a.getParts().size(); k++) {
                    final CentipedePart b = a.getParts().get(k);
                    final Rectangle r2 = b.getBounds();
                    if (r3.intersects(r2)) {
                        doLifeLost();
                    }
                }
            }

            // Bullet and Centipedes
            final ArrayList<Bullet> ms = craft.getBullets();

            for (int i = 0; i < ms.size(); i++) {
                final Bullet m = ms.get(i);

                final Rectangle r1 = m.getBounds();
                for (int j = 0; j < centipedes.size(); j++) {
                    final Centipede a = centipedes.get(j);
                    for (int k = 0; k < a.getParts().size(); k++) {
                        final CentipedePart b = a.getParts().get(k);
                        final Rectangle r2 = b.getBounds();
                        if (r1.intersects(r2)) {
                            // TODO: Hit centipede part.
                            if (k == 0) {
                                a.getParts().remove(k);
                            }
                        }
                    }
                }
            }
        }

        private void doLifeLost() {
            // TODO Die
        }

        private void initLevel() {
            // TODO Be more dynamic about how we spawn them
            centipedes = new ArrayList<Centipede>();
            centipedes.add(new Centipede(level, 0, 0));
        }

        private void initMushrooms() {
            mushrooms = new ArrayList<Mushroom>();
            // TODO Randomly generate some shroomz
        }

        private void levelUp() {
            level++;
            System.out.println("Starting level: " + level);
            initLevel();
        }

        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            if (ingame) {
                final Graphics2D g2d = (Graphics2D) g;

                if (craft.isVisible()) {
                    g2d.setColor(Color.lightGray);
                    g2d.fill(craft.getBounds());
                    // g2d.drawImage(craft.getImage(), craft.getX(), craft.getY(),this);
                }

                final ArrayList<Bullet> bs = craft.getBullets();

                for (int i = 0; i < bs.size(); i++) {
                    final Bullet b = bs.get(i);
                    g2d.drawImage(b.getImage(), b.getX(), b.getY(), this);
                }

                for (int i = 0; i < centipedes.size(); i++) {
                    final Centipede c = centipedes.get(i);
                    // TODO Centipede drawing
                }

                g2d.setColor(Color.WHITE);
                // Centipedes left
                g2d.drawString("Centipedes left: " + centipedes.size(), 5, 15);

            } else {
                final String msg = "Game Over";
                final Font small = new Font("Helvetica", Font.BOLD, 14);
                final FontMetrics metr = this.getFontMetrics(small);

                g.setColor(Color.white);
                g.setFont(small);
                g.drawString(msg, (boardWidth - metr.stringWidth(msg)) / 2,
                        boardHeight / 2);
            }

            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        }
    }
    /**
     * Bullet.
     * @author Philip
     */
    private class Bullet {
        private final static int WIDTH = 0, HEIGHT = 0;
        private final String imgpath = "bullet.png";
        Image image;
        boolean visible;
        int x, y, width, height;
        Bullet(int i, int j) {
            ImageIcon ii =
                    new ImageIcon(this.getClass().getResource(imgpath));
            image = ii.getImage();
            visible = true;
            width = image.getWidth(null);
            height = image.getHeight(null);
            x = i;
            y = j;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, WIDTH, HEIGHT);
        }

        public BufferedImage getImage() {
            // TODO Get image
            return null;
        }

        int getX() {
            return 0;
        }

        int getY() {
            return y;
        }

        public boolean isVisible() {
            return visible;
        }

        public void move() {
            // TODO Move the bullet
        }
    }

    class Centipede {
        String head = "centipedeHead.png";
        String body = "centipedeHead.png";
        ArrayList<CentipedePart> parts;

        public Centipede(final int l, final int x, final int y) {
            parts = new ArrayList<CentipedePart>(l);
            for (int i = 0; i < l; i++) {
                //Model the chain as a vertical line to the point
                parts.add(new CentipedePart(this,
                        x,
                        y - (i * CentipedePart.HEIGHT)
                ));
            }
        }

        public int getLength() {
            return parts.size();
        }

        public ArrayList<CentipedePart> getParts() {
            return parts;
        }

        public void move() {
            for (CentipedePart part: parts) {
                part.move();
            }
        }
    }

    class CentipedePart {
        static final int WIDTH = 10, HEIGHT = 10;

        Centipede parent;
        int x;
        int y;
        private boolean visible;

        CentipedePart(Centipede p, int a, int b) {
            parent = p;
            x = a;
            y = b;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, WIDTH, HEIGHT);
        }

        public boolean isVisible() {
            return visible;
        }

        public void move() {
            // TODO: Centipede Part Shuffle
        }
    }

    class Craft {
        String imgpath = "craft.png";
        ArrayList<Bullet> bullets;
        int x, y, width = 10, height = 10;
        int dx, dy;
        Image image;
        private boolean visible;

        Craft() {
            bullets = new ArrayList<Bullet>();
            visible = true;
            x = 40;
            y = 60;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        public ArrayList<Bullet> getBullets() {
            return bullets;
        }

        public Image getImage() {
            return image;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isVisible() {
            return visible;
        }

        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                fire();
            } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                dx = -1;
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                dx = 1;
            } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                dy = -1;
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                dy = 1;
            }
        }
        public void fire() {
            bullets.add(new Bullet(x + width, y + height / 2));
        }

        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                dx = 0;
            }

            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                dx = 0;
            }

            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                dy = 0;
            }

            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                dy = 0;
            }
        }

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
    }

    class Mushroom {
        String img = "mushroom.png";
    }

    Apeiron() {
        add(new Board());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("Apeiron");
        setResizable(false);
        setVisible(true);
    }
}