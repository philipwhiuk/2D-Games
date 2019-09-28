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
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

@SuppressWarnings("serial")
class Apeiron extends JFrame {
    private int BOARD_HEIGHT = 300;
    private int BOARD_WIDTH = 400;

    private class Board extends GameBoard {
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
        ArrayList<Mushroom> mushrooms;
        ArrayList<Centipede> centipedes;
        Craft craft;
        boolean ingame;
        Timer timer;
        private int level;
        private int score = 0;

        Board() {
            addKeyListener(new TAdapter());
            setFocusable(true);
            setBackground(Color.BLACK);
            setDoubleBuffered(true);
            ingame = true;
            setSize(BOARD_WIDTH, BOARD_HEIGHT);
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
            final Rectangle craftBounds = craft.getBounds();

            for (final Centipede centipede : centipedes) {
                for (int k = 0; k < centipede.getParts().size(); k++) {
                    final CentipedePart centipedePart = centipede.getParts().get(k);
                    final Rectangle centipedePartBounds = centipedePart.getBounds();
                    if (craftBounds.intersects(centipedePartBounds)) {
                        doLifeLost();
                        return;
                    }
                }
            }

            // Bullet and Centipedes
            final ArrayList<Bullet> bullets = craft.getBullets();

            Iterator<Bullet> bulletIterator = bullets.iterator();
            while(bulletIterator.hasNext()) {
                final ArrayList<Centipede> newCentipedes = new ArrayList<>();
                final Bullet bullet = bulletIterator.next();

                final Rectangle bulletBounds = bullet.getBounds();
                Iterator<Centipede> livingCentipedes = centipedes.iterator();
                boolean hasHit = false;
                while(livingCentipedes.hasNext()) {
                    final Centipede centipede = livingCentipedes.next();
                    for (int k = 0; k < centipede.getParts().size(); k++) {
                        final CentipedePart b = centipede.getParts().get(k);
                        final Rectangle r2 = b.getBounds();
                        if (bulletBounds.intersects(r2)) {
                            hasHit = true;
                            score += 25;
                            if (k == 0 || k == centipede.parts.size()-1) {
                                centipede.getParts().remove(k);
                                hasHit = true;
                                break;
                            } else {
                                livingCentipedes.remove();
                                newCentipedes.addAll(centipede.split(k));
                                break;
                            }
                        }
                    }
                }
                if (hasHit) {
                    bulletIterator.remove();
                }
                centipedes.addAll(newCentipedes);
            }
        }

        private void doLifeLost() {
            ingame = false;
        }

        private void initLevel() {
            centipedes = new ArrayList<>();
            centipedes.add(new Centipede(level+15, 0, 0));
        }

        private void initMushrooms() {
            mushrooms = new ArrayList<>();
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
            final Graphics2D g2d = (Graphics2D) g;
            g.setColor(Color.YELLOW);
            g.drawRect(0,0,BOARD_WIDTH, BOARD_HEIGHT);
            if (ingame) {

                if (craft.isAlive()) {
                    craft.paint(g2d);
                }

                final ArrayList<Bullet> bs = craft.getBullets();

                for (final Bullet b : bs) {
                    b.paint(g2d);
                }

                for (final Centipede c : centipedes) {
                    c.paint(g2d);
                }

                g2d.setColor(Color.WHITE);
                g2d.drawString("Score: " + score, 5, 15);

            } else {
                final String msg = "Game Over - Score: "+ score;
                final Font small = new Font("Helvetica", Font.BOLD, 14);
                final FontMetrics metr = this.getFontMetrics(small);

                g2d.setColor(Color.white);
                g2d.setFont(small);
                g2d.drawString(msg, (BOARD_WIDTH - metr.stringWidth(msg)) / 2,
                        BOARD_HEIGHT / 2);
            }

            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        }
    }

    private class Bullet {
        private final static int WIDTH = 5, HEIGHT = 5;
        private final String imgpath = "missile.png";
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

        public boolean isVisible() {
            return visible;
        }

        public void move() {
            y--;
        }

        public void paint(Graphics2D g2d) {
            g2d.setColor(Color.YELLOW);
            g2d.fill(getBounds());
        }
    }

    class Centipede {
        ArrayList<CentipedePart> parts;

        Centipede(final int length, final int x, final int y) {
            parts = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                parts.add(new CentipedePart(this,
                        x,
                        y - (i * CentipedePart.HEIGHT)
                ));
            }
        }

        private Centipede(List<CentipedePart> parts) {
            this.parts = new ArrayList<>(parts);
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

        public void paint(Graphics2D g2d) {
            for (int i = 0; i < parts.size(); i++) {
                parts.get(i).paint(g2d, i == 0);
            }
        }

        public List<Centipede> split(int k) {
            Centipede head = new Centipede(parts.subList(0, k));
            Centipede tail = new Centipede(parts.subList(k+1, parts.size()));
            Collections.reverse(tail.parts);
            for (CentipedePart part: tail.parts) {
                part.switchDirection();
            }
            return Arrays.asList(head, tail);
        }
    }

    enum Direction { LEFT, DOWN, RIGHT }

    class CentipedePart {
        static final int WIDTH = 10, HEIGHT = 10;

        Centipede parent;
        int x;
        int y;
        private Direction lastDirection = Direction.LEFT;
        private Direction direction = Direction.LEFT;
        private int movedDown;

        CentipedePart(Centipede p, int a, int b) {
            parent = p;
            x = a;
            y = b;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, WIDTH, HEIGHT);
        }

        private int CENTIPEDE_SPEED = 1;

        public void move() {
            if (y-HEIGHT < 0) {
                y += CENTIPEDE_SPEED;
            } else if (direction == Direction.LEFT) {
                if (x-CENTIPEDE_SPEED < 0) {
                    x = 0;
                    lastDirection = Direction.LEFT;
                    direction = Direction.DOWN;
                } else {
                    x-= CENTIPEDE_SPEED;
                }
            } else if (direction == Direction.RIGHT) {
                if (x+CENTIPEDE_SPEED+WIDTH > BOARD_WIDTH) {
                    x = BOARD_WIDTH-WIDTH;
                    lastDirection = Direction.RIGHT;
                    direction = Direction.DOWN;
                } else {
                    x += CENTIPEDE_SPEED;
                }
            } else if (direction == Direction.DOWN) {
                if (movedDown >= HEIGHT) {
                    if (lastDirection == Direction.LEFT) {
                        direction = Direction.RIGHT;
                        movedDown = 0;
                    } else {
                        direction = Direction.LEFT;
                        movedDown = 0;
                    }
                } else {
                    movedDown += CENTIPEDE_SPEED;
                    y += CENTIPEDE_SPEED;
                }
            }
        }

        public void paint(Graphics2D g2d, boolean isHead) {
            g2d.setColor(isHead ? Color.GREEN : Color.CYAN);
            g2d.fillOval(x, y, WIDTH, HEIGHT);
        }

        public void switchDirection() {
            if (direction == Direction.LEFT) {
                direction = Direction.RIGHT;
            } else if (direction == Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (direction == Direction.DOWN) {
                lastDirection = lastDirection == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
            }
        }

        public void switchAndDown() {
            if (direction == Direction.LEFT) {
                lastDirection = Direction.LEFT;
                direction = Direction.DOWN;
            } else if (direction == Direction.RIGHT) {
                lastDirection = Direction.RIGHT;
                direction = Direction.DOWN;
            } else if (direction == Direction.DOWN) {
                lastDirection = lastDirection == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
            }
        }
    }

    class Craft {
        ArrayList<Bullet> bullets;
        int x, y, width = 10, height = 10;
        int dx, dy;
        private boolean alive;

        Craft() {
            bullets = new ArrayList<>();
            alive = true;
            x = BOARD_WIDTH/2;
            y = BOARD_HEIGHT-30;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        public ArrayList<Bullet> getBullets() {
            return bullets;
        }

        public boolean isAlive() {
            return alive;
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
        void fire() {
            bullets.add(new Bullet(x + width/2 - Bullet.WIDTH/2, y - Bullet.HEIGHT));
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
            if (x > BOARD_WIDTH-width) {
                x = BOARD_WIDTH-width;
            }

            if (y < BOARD_HEIGHT-100 ) {
                y = BOARD_HEIGHT-100;
            }
            if (y > BOARD_HEIGHT-height) {
                y = BOARD_HEIGHT-height;
            }
        }

        public void paint(Graphics2D g2d) {
            g2d.setColor(Color.lightGray);
            g2d.fill(getBounds());
        }
    }

    private class Mushroom {
    }

    Apeiron() {
        add(new Board());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setTitle("Apeiron");
        setResizable(false);
        setVisible(true);
    }
}
