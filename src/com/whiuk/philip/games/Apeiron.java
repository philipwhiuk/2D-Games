package com.whiuk.philip.games;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

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
            if (ingame) {
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
                        c.move(centipedes, mushrooms);
                    } else {
                        centipedes.remove(i);
                    }
                }
                craft.move(mushrooms);
                checkCollisions();
                for (Centipede c : centipedes) {
                    if (c.getParts().size() > 0 && c.getParts().get(0).y >= BOARD_HEIGHT) {
                        doLifeLost();
                        break;
                    }
                }
            }
            repaint();
        }

        private void checkCollisions() {
            // Craft and Centipedes

            final Rectangle craftBounds = craft.getBounds();
            if(isThereACraftCentipedeCollision(craftBounds)) {
                doLifeLost();
                return;
            }
            handleBulletCentipedeCollisions();
        }

        private boolean isThereACraftCentipedeCollision(Rectangle craftBounds) {
            for (final Centipede centipede : centipedes) {
                for (int k = 0; k < centipede.getParts().size(); k++) {
                    final CentipedePart centipedePart = centipede.getParts().get(k);
                    final Rectangle centipedePartBounds = centipedePart.getBounds();
                    if (craftBounds.intersects(centipedePartBounds)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void handleBulletCentipedeCollisions() {
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
                        final CentipedePart centipedePart = centipede.getParts().get(k);
                        final Rectangle partBounds = centipedePart.getBounds();
                        if (bulletBounds.intersects(partBounds)) {
                            hasHit = true;
                            score += 25;
                            if (k == 0 || k == centipede.parts.size()-1) {
                                centipede.getParts().remove(k);
                                hasHit = true;
                            } else {
                                livingCentipedes.remove();
                                newCentipedes.addAll(centipede.split(k));
                            }
                            mushrooms.add(new Mushroom(centipedePart));
                            break;
                        }
                    }
                }
                if (!hasHit) {
                    Iterator<Mushroom> mushroomIterator = mushrooms.iterator();
                    while (mushroomIterator.hasNext()) {
                        Mushroom mushroom = mushroomIterator.next();
                        if (mushroom.bounds.intersects(bulletBounds)) {
                            hasHit = true;
                            mushroom.health--;
                            if (mushroom.health == 0) {
                                mushroomIterator.remove();
                                score += 1;
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

                for (final Mushroom m : mushrooms) {
                    m.paint(g2d);
                }

                g2d.setColor(Color.WHITE);
                g2d.drawString("Score: " + score, 5, 15);
                g2d.drawString("Level: " + level, 5, 30);

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

        Rectangle getBounds() {
            return new Rectangle(x, y, WIDTH, HEIGHT);
        }

        boolean isVisible() {
            return visible;
        }

        void move() {
            y--;
        }

        void paint(Graphics2D g2d) {
            g2d.setColor(Color.YELLOW);
            g2d.fill(getBounds());
        }
    }

    private class Centipede {
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

        int getLength() {
            return parts.size();
        }

        ArrayList<CentipedePart> getParts() {
            return parts;
        }

        void move(List<Centipede> allCentipedes, List<Mushroom> mushrooms) {
            for (CentipedePart part : parts) {
                part.move(allCentipedes, this, mushrooms);
            }
        }

        void paint(Graphics2D g2d) {
            for (int i = 0; i < parts.size(); i++) {
                parts.get(i).paint(g2d, i == 0);
            }
        }

        List<Centipede> split(int k) {
            Centipede head = new Centipede(parts.subList(0, k));
            Centipede tail = new Centipede(parts.subList(k+1, parts.size()));
            Collections.reverse(tail.parts);
            for (CentipedePart part: tail.parts) {
                part.switchDirection();
            }
            return Arrays.asList(head, tail);
        }
    }

    private enum Direction { LEFT, DOWN, RIGHT }

    private class CentipedePart {
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

        Rectangle getBounds() {
            return new Rectangle(x, y, WIDTH, HEIGHT);
        }

        private int CENTIPEDE_SPEED = 1;

        void move(List<Centipede> allCentipedes, Centipede self, List<Mushroom> mushrooms) {
            if (y-HEIGHT < 0) {
                y += CENTIPEDE_SPEED;
            } else if (direction == Direction.LEFT) {
                if (x-CENTIPEDE_SPEED < 0) {
                    x = 0;
                    lastDirection = Direction.LEFT;
                    direction = Direction.DOWN;
                } else if (hasCollision(allCentipedes, self, mushrooms, new Rectangle(x-CENTIPEDE_SPEED, y, WIDTH, HEIGHT))) {
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
                } else if (hasCollision(allCentipedes, self, mushrooms, new Rectangle(x+CENTIPEDE_SPEED, y, WIDTH, HEIGHT))) {
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

        private boolean hasCollision(List<Centipede> allCentipedes, Centipede self, List<Mushroom> mushrooms, Rectangle newBounds) {
            for (Centipede collisionCentipede : allCentipedes) {
                if (self == collisionCentipede) {
                    continue;
                }
                for (CentipedePart collisionPart: collisionCentipede.getParts()) {
                    if (collisionPart.getBounds().intersects(newBounds)) {
                        return true;
                    }
                }
            }
            for (Mushroom mushroom : mushrooms) {
                if (mushroom.getBounds().intersects(newBounds)) {
                    return true;
                }
            }
            return false;
        }

        void paint(Graphics2D g2d, boolean isHead) {
            g2d.setColor(isHead ? Color.GREEN : Color.CYAN);
            g2d.fillOval(x, y, WIDTH, HEIGHT);
        }

        void switchDirection() {
            if (direction == Direction.LEFT) {
                direction = Direction.RIGHT;
            } else if (direction == Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (direction == Direction.DOWN) {
                lastDirection = lastDirection == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
            }
        }

        void switchAndDown() {
            if (direction == Direction.LEFT) {
                lastDirection = Direction.LEFT;
                direction = Direction.DOWN;
                movedDown = 0;
            } else if (direction == Direction.RIGHT) {
                lastDirection = Direction.RIGHT;
                direction = Direction.DOWN;
                movedDown = 0;
            }
        }
    }

    private class Mushroom {
        static final int WIDTH = 10, HEIGHT = 10;
        int x;
        int y;
        int health = 3;
        Rectangle bounds;

        Mushroom(CentipedePart centipedePart) {
            this.x = centipedePart.x;
            this.y = centipedePart.y;
            //Mushrooms don't move so cache the bounds
            bounds = new Rectangle(x,y, WIDTH, HEIGHT);
        }


        void paint(Graphics2D g2d) {
            Color color = Color.WHITE;
            if (health == 3) {
                color = Color.WHITE;
            } else if (health == 2) {
                color = Color.LIGHT_GRAY;
            }if (health == 1) {
                color = Color.DARK_GRAY;
            }
            g2d.setColor(color);
            g2d.fillOval(x, y, WIDTH, HEIGHT);
        }

        public Rectangle getBounds() {
            return bounds;
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

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        ArrayList<Bullet> getBullets() {
            return bullets;
        }

        boolean isAlive() {
            return alive;
        }

        void keyPressed(KeyEvent e) {
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

        void keyReleased(KeyEvent e) {
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

        void move(List<Mushroom> mushrooms) {
            if (x + dx < 1) {
                x = 1;
            } else if (x + dx > BOARD_WIDTH - width) {
                x = BOARD_WIDTH - width;
            } else if (!hitsMushroom(mushrooms, x + dx, y)) {
                x += dx;
            }

            if (y + dy < BOARD_HEIGHT - 100) {
                y = BOARD_HEIGHT - 100;
            } else if (y + dy > BOARD_HEIGHT - height) {
                y = BOARD_HEIGHT - height;
            } else if (!hitsMushroom(mushrooms, x, y + dy)) {
                y += dy;
            }
        }

        private boolean hitsMushroom(List<Mushroom> mushrooms, int x, int y) {
            Rectangle newBounds = new Rectangle(x, y, WIDTH, HEIGHT);
            for (Mushroom mushroom:  mushrooms) {
                if (newBounds.intersects(mushroom.getBounds())) {
                    return true;
                }
            }
            return false;
        }

        void paint(Graphics2D g2d) {
            g2d.setColor(Color.lightGray);
            g2d.fill(getBounds());
        }
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
