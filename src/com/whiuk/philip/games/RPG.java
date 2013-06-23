package com.whiuk.philip.games;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Role Playing Game.
 * @author Philip
 *
 */
@SuppressWarnings("serial")
public class RPG extends JFrame {
    /**
     * Board.
     * @author Philip
     *
     */
    class Board extends GameBoard {
        /**
         * 
         * @author Philip
         *
         */
        class TAdapter extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {
                final int key = e.getKeyCode();
                if (ingame) {
                    if (key == KeyEvent.VK_LEFT) {
                        reqdx = -1;
                        reqdy = 0;
                    } else if (key == KeyEvent.VK_RIGHT) {
                        reqdx = 1;
                        reqdy = 0;
                    } else if (key == KeyEvent.VK_UP) {
                        reqdx = 0;
                        reqdy = -1;
                    } else if (key == KeyEvent.VK_DOWN) {
                        reqdx = 0;
                        reqdy = 1;
                    } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                        ingame = false;
                    } else if (key == KeyEvent.VK_PAUSE) {
                        if (timer.isRunning()) {
                            timer.stop();
                        } else {
                            timer.start();
                        }
                    }
                } else if (key == 's' || key == 'S') {
                    ingame = true;
                    GameInit();
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                final int key = e.getKeyCode();
                if (key == Event.LEFT || key == Event.RIGHT || key == Event.UP
                        || key == Event.DOWN) {
                    reqdx = 0;
                    reqdy = 0;
                }
            }
        }

        Dimension d;
        Font smallfont = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fmsmall, fmlarge;
        Image ii;
        boolean ingame = false, dying = false;
        final int blocksize = 24;
        final int nrofblocks = 15;
        final int scrsize = nrofblocks * blocksize;
        final int char_animdelay = 2;
        final int char_animcount = 4;
        final int char_speed = 6;
        final short mapdata[][] = {{1}, {2}};

        int char_cur_animcount = 2;
        int char_cur_animdir = 1;
        int char_cur_animpos = 0;
        int livesleft, score;
        int deathcounter;
        int[] dx, dy;

        Image NPC;
        Image char1;
        Image char2up, char2left, char2right, char2down;
        Image char3up, char3down, char3left, char3right;
        Image char4up, char4down, char4left, char4right;

        int char_x, char_y, char_dx, char_dy;
        int reqdx, reqdy, viewdx, viewdy;

        final int validspeeds[] = {1, 2, 3, 4, 6, 8};
        final int maxspeed = 6;
        int currentspeed = 3;
        short[] screendata;

        Timer timer;

        public Board() {
            GetImages();
            addKeyListener(new TAdapter());
            setFocusable(true);
            d = new Dimension(400, 400);
            setBackground(Color.black);
            setDoubleBuffered(true);
            timer = new Timer(40, this);
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }

        @Override
        public void addNotify() {
            super.addNotify();
            GameInit();
        }

        public void CheckMaze() {
            short i = 0;
            boolean finished = true;

            while (i < nrofblocks * nrofblocks && finished) {
                if ((screendata[i] & 48) != 0) {
                    finished = false;
                }
                i++;
            }

            if (finished) {
                score += 50;

                if (currentspeed < maxspeed) {
                    currentspeed++;
                }
                LevelInit();
            }
        }

        public void Death() {
            livesleft--;
            if (livesleft == 0) {
                ingame = false;
            }
            LevelContinue();
        }

        public void DrawCharacter(Graphics2D g) {

        }

        public void GameInit() {

        }

        public void GetImages() {

        }

        public void LevelContinue() {

        }

        private void LevelInit() {
            // TODO Level initialization

        }

        public void MoveCharacter() {
            int pos;
            short ch;

            if (reqdx == -char_dx && reqdy == -char_dy) {
                char_dx = reqdx;
                char_dy = reqdy;
                viewdx = char_dx;
                viewdy = char_dy;
            }
            if (char_x % blocksize == 0 && char_y % blocksize == 0) {
                pos = char_x / blocksize + nrofblocks * (char_y / blocksize);
                ch = screendata[pos];

                if ((ch & 16) != 0) {
                    screendata[pos] = (short) (ch & 15);
                    score++;
                }

                if (reqdx != 0 || reqdy != 0) {
                    if (!(reqdx == -1 && reqdy == 0 && (ch & 1) != 0
                            || reqdx == 1 && reqdy == 0 && (ch & 4) != 0
                            || reqdx == 0 && reqdy == -1 && (ch & 2) != 0 || reqdx == 0
                            && reqdy == 1 && (ch & 8) != 0)) {
                        char_dx = reqdx;
                        char_dy = reqdy;
                        viewdx = char_dx;
                        viewdy = char_dy;
                    }
                }

                // Check for standstill
                if (char_dx == -1 && char_dy == 0 && (ch & 1) != 0
                        || char_dx == 1 && char_dy == 0 && (ch & 4) != 0
                        || char_dx == 0 && char_dy == -1 && (ch & 2) != 0
                        || char_dx == 0 && char_dy == 1 && (ch & 8) != 0) {
                    char_dx = 0;
                    char_dy = 0;
                }
            }
            char_x = char_x + char_speed * char_dx;
            char_y = char_y + char_speed * char_dy;
        }

        /**
         * 
         * @param g 2D graphics context
         */
        public void MoveNPCs(Graphics2D g) {

        }

        /**
         * 
         * @param g2d 2D graphics context
         */
        public void PlayGame(Graphics2D g2d) {
            if (dying) {
                Death();
            } else {
                MoveCharacter();
                DrawCharacter(g2d);
                MoveNPCs(g2d);
                CheckMaze();
            }
        }

    }

    /**
     *
     */
    public RPG() {
        add(new Board());
        setTitle("RPG");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 420);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
