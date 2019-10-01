package com.whiuk.philip.games.breakout;

import com.whiuk.philip.games.GameBoard;
import com.whiuk.philip.games.apeiron.Apeiron;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

public class Breakout extends JFrame {
    private static final int BOARD_HEIGHT = 300;
    private static final int BOARD_WIDTH = 400;
    private static final int BRICK_WIDTH = 20;
    private static final int BRICK_HEIGHT = 10;

    private class Board extends GameBoard {
        private class TAdapter extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {
                Board.this.keyPressed(e);
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                Board.this.keyReleased(e);
            }
        }
        ArrayList<Brick> bricks;
        Ball ball;
        Paddle paddle;
        boolean ingame;
        Timer timer;
        private int level;
        private int score = 0;
        private int lives = 3;

        Board() {
            addKeyListener(new Board.TAdapter());
            setFocusable(true);
            setBackground(Color.BLACK);
            setDoubleBuffered(true);
            setSize(BOARD_WIDTH, BOARD_HEIGHT);
            startGame();
            timer = new Timer(5, this);
            timer.start();
        }

        private void startGame() {
            lives = 3;
            ingame = true;
            paddle = new Paddle();
            initLevel();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            gameUpdate();
            repaint();
        }

        private void gameUpdate() {
            if (ingame) {
                if (bricks.size() == 0) {
                    levelUp();
                }
                if (ball.isAlive()) {
                    ball.move();
                }
                paddle.move();
                checkCollisions();
                if (ball.isAlive() && ball.y > BOARD_HEIGHT-10) {
                    loseLife();
                }
            }
        }

        private void loseLife() {
            lives--;
            if (lives > 0) {
                ball.alive = false;
            } else {
                ingame = false;
            }
        }

        private void checkCollisions() {
            // Paddle and Ball
            final Rectangle paddleBounds = paddle.getBounds();
            final Rectangle ballBounds = ball.getBounds().getBounds();
            if (paddleBounds.intersects(ballBounds)) {
                ball.dx = -ball.dx;
                ball.dy = -ball.dy;
            }
            Iterator<Brick> brickIterator = bricks.iterator();
            while (brickIterator.hasNext()) {
                Brick brick = brickIterator.next();
                if (ballBounds.intersects(brick.bounds)) {
                    if (brick.health == 1) {
                        score += 20;
                        brickIterator.remove();
                    } else {
                        score += 5;
                        brick.health --;
                    }
                    ball.dx = -ball.dx;
                    ball.dy = -ball.dy;
                }
            }
        }

        private void initLevel() {
            bricks = new ArrayList<>();
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 10; x++) {
                    bricks.add(new Brick(10 + (x * (BRICK_WIDTH+1)), 10 + (y * (BRICK_HEIGHT+1))));
                }
            }
            ball = new Ball(paddle);
        }

        private void levelUp() {
            level++;
            initLevel();
        }

        void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                if (!ball.isAlive()) {
                    ball = new Ball(paddle);
                }
            } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                paddle.dx = -1;
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                paddle.dx = 1;
            }
        }

        void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                paddle.dx = 0;
            }

            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                paddle.dx = 0;
            }
        }

        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            final Graphics2D g2d = (Graphics2D) g;
            g.setColor(Color.WHITE);
            g.drawRect(0,0,BOARD_WIDTH, BOARD_HEIGHT);
            if (ingame) {

                paddle.paint(g2d);

                if (ball.isAlive()) {
                    ball.paint(g2d);
                }

                for (final Brick b : bricks) {
                    b.paint(g2d);
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

    class Paddle {
        int x, y, width = 50, height = 10;
        int dx;

        Paddle() {
            x = BOARD_WIDTH/2;
            y = BOARD_HEIGHT-30;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        void move() {
            if (x + dx < 1) {
                x = 1;
            } else if (x + dx > BOARD_WIDTH - width) {
                x = BOARD_WIDTH - width;
            } else {
                x += dx;
            }
        }

        void paint(Graphics2D g2d) {
            g2d.setColor(Color.lightGray);
            g2d.fill(getBounds());
        }
    }

    class Ball {
        boolean alive;
        int x;
        int y;
        int dx;
        int dy;
        static final int BALL_DIAMETER = 5;

        Ball(Paddle paddle) {
            this.x = paddle.x+(paddle.width/2);
            this.y = paddle.y-BALL_DIAMETER;
            this.dx = -1;
            this.dy = -1;
            alive = true;
        }

        void paint(Graphics2D g2d) {
            Color color = Color.YELLOW;
            g2d.setColor(color);
            g2d.fillRect(x, y, BALL_DIAMETER, BALL_DIAMETER);
        }

        public Shape getBounds() {
            return new Ellipse2D.Float(x, y, BALL_DIAMETER, BALL_DIAMETER);
        }

        public void move() {
            if (x + dx < 0 || x + dx > BOARD_WIDTH) {
                dx = -dx;
            }
            if (y + dy < 0 || y + dy > BOARD_HEIGHT) {
                dy = -dy;
            }
            x += dx;
            y += dy;
        }

        public boolean isAlive() {
            return alive;
        }
    }

    private class Brick {
        int x;
        int y;
        int health = 3;
        Rectangle bounds;

        Brick(int x, int y) {
            this.x = x;
            this.y = y;
            //Bricks don't move so cache the bounds
            bounds = new Rectangle(x,y, BRICK_WIDTH, BRICK_HEIGHT);
        }

        void paint(Graphics2D g2d) {
            Color color = Color.RED;
            if (health == 3) {
                color = Color.RED;
            } else if (health == 2) {
                color = Color.BLUE;
            }if (health == 1) {
                color = Color.CYAN;
            }
            g2d.setColor(color);
            g2d.drawRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        }

        public Rectangle getBounds() {
            return bounds;
        }
    }

    public Breakout() {
        add(new Board());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setTitle("Breakout");
        setResizable(false);
        setVisible(true);
    }
}
