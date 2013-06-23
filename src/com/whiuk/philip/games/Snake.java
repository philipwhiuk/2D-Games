package com.whiuk.philip.games;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Snake implementation.
 * @author Philip
 *
 */
@SuppressWarnings("serial")
public class Snake extends JFrame {
    /**
     *
     * @author Philip
     *
     */
    public class Board extends GameBoard {
        private final int WIDTH = 300;
        private final int HEIGHT = 300;
        private final int DOT_SIZE = 10;
        private final int ALL_DOTS = 900;
        private final int RAND_POS = 29;
        private final int DELAY = 140;
        private int x[] = new int[ALL_DOTS];
        private int y[] = new int[ALL_DOTS];
        /**
         * 
         */
        private int dots;
        /**
         * 
         */
        private int appleX;
        /**
         * 
         */
        private int appleY;
        /**
         * 
         */
        private boolean left = false;
        /**
         *
         */
        private boolean right = true;
        /**
         *
         */
        private boolean up = false;
        /**
         *
         */
        private boolean down = false;
        /**
         *
         */
        private boolean inGame = true;
        /**
         *
         */
        private Timer timer;
        /**
         *
         */
        private Image ball;
        /**
         *
         */
        private Image apple;
        /**
         *
         */
        private Image head;

	    /**
	     * 
	     */
	    public Board() {
	    	addKeyListener(new TAdapter());
	    	setBackground(Color.black);
	    	 ImageIcon iid = new ImageIcon(this.getClass().getResource("dot.png"));
	         ball = iid.getImage();

	         ImageIcon iia = new ImageIcon(this.getClass().getResource("apple.png"));
	         apple = iia.getImage();

	         ImageIcon iih = new ImageIcon(this.getClass().getResource("head.png"));
	         head = iih.getImage();	    	
	    	setFocusable(true);
	        initGame();
	    }
		private void initGame() {
			dots = 3;
	        for (int z = 0; z < dots; z++) {
	            x[z] = 50 - z * 10;
	            y[z] = 50;
	        }
	        locateApple();
	        timer = new Timer(DELAY, this);
	        timer.start();
		}
		@Override
	    public void paint(Graphics g) {
	        super.paint(g);
	        Graphics2D g2D = (Graphics2D) g;
	        if (inGame) {
	            g2D.drawImage(apple, appleX, appleY, this);
	            for (int z = 0; z < dots; z++) {
	                if (z == 0)
	                    g2D.drawImage(head, x[z], y[z], this);
	                else g2D.drawImage(ball, x[z], y[z], this);
	            }
	            Toolkit.getDefaultToolkit().sync();
	            g2D.dispose();
	        } else {
	            gameOver(g2D);
	        }
	    }
		/**
		 * 
		 * @param g2D 2D graphics context
		 */
	    public void gameOver(Graphics2D g2D) {
	        String msg = "Game Over";
	        Font small = new Font("Helvetica", Font.BOLD, 14);
	        FontMetrics metr = this.getFontMetrics(small);

	        g2D.setColor(Color.white);
	        g2D.setFont(small);
	        g2D.drawString(msg, (WIDTH - metr.stringWidth(msg)) / 2,
	                     HEIGHT / 2);
	    }
	    /**
	     * 
	     */
	    public void checkApple() {
	        if ((x[0] == appleX) && (y[0] == appleY)) {
	            dots++;
	            locateApple();
	        }
	    }
	    /**
	     * 
	     */
	    public void move() {
	        for (int z = dots; z > 0; z--) {
	            x[z] = x[(z - 1)];
	            y[z] = y[(z - 1)];
	        }
	        if (left) {
	            x[0] -= DOT_SIZE;
	        }
	        if (right) {
	            x[0] += DOT_SIZE;
	        }
	        if (up) {
	            y[0] -= DOT_SIZE;
	        }
	        if (down) {
	            y[0] += DOT_SIZE;
	        }
	    }
	    /**
	     * 
	     */
	    public void checkCollision() {
			  for (int z = dots; z > 0; z--) {
			
			      if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
			          inGame = false;
			      }
			  }
	        if (y[0] > HEIGHT) {
	            inGame = false;
	        }

	        if (y[0] < 0) {
	            inGame = false;
	        }

	        if (x[0] > WIDTH) {
	            inGame = false;
	        }

	        if (x[0] < 0) {
	            inGame = false;
	        }
	    }
	    /**
	     * 
	     */
	    public void locateApple() {
	        int r = (int) (Math.random() * RAND_POS);
	        appleX = ((r * DOT_SIZE));
	        r = (int) (Math.random() * RAND_POS);
	        appleY = ((r * DOT_SIZE));
	    }
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        if (inGame) {
	            checkApple();
	            checkCollision();
	            move();
	        }
	        repaint();
	    }	    

	    private class TAdapter extends KeyAdapter {
	    	@Override
	    	public void keyPressed(KeyEvent e) {
	            int key = e.getKeyCode();
	            if ((key == KeyEvent.VK_LEFT) && (!right)) {
	                left = true;
	                up = false;
	                down = false;
	            }

	            if ((key == KeyEvent.VK_RIGHT) && (!left)) {
	                right = true;
	                up = false;
	                down = false;
	            }

	            if ((key == KeyEvent.VK_UP) && (!down)) {
	                up = true;
	                right = false;
	                left = false;
	            }

	            if ((key == KeyEvent.VK_DOWN) && (!up)) {
	                down = true;
	                right = false;
	                left = false;
	            }
	        }
	    }
	}
	/**
	 * 
	 */
	public Snake() {
		add(new Board());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 340);
        setLocationRelativeTo(null);
        setTitle("Snake");

        setResizable(false);
        setVisible(true);
    }
}
