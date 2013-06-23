package com.whiuk.philip.games;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Game launcher.
 * @author Philip
 */
@SuppressWarnings("serial")
public class Launcher extends JFrame {
    /**
     * Entry point.
     * @param args Command-line arguments
     */
    public static void main(final String[] args) {
        new Launcher();
    }
    /**
     * {@link Pacman} button.
     */
    private JButton pacman;
    /**
     * {@link Snake} button.
     */
    private JButton snake;
    /**
     * {@link MissileAttack} button.
     */
    private JButton missileattack;
    /**
     * {@link Apeiron} button.
     */
    private JButton apeiron;
    /**
     * {@link RPG} button.
     */
    private JButton rpg;
    /**
     * Constructor
     */
    public Launcher() {
        super();
        setLayout(new FlowLayout());
        buildComponents();
        pack();
        setVisible(true);
    }
    /**
     * Build components.
     */
    private void buildComponents() {
        pacman = new JButton("Pacman");
        pacman.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new Pacman();
                dispose();
            }
        });
        snake = new JButton("Snake");
        snake.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new Snake();
                dispose();
            }
        });
        missileattack = new JButton("Missile Attack");
        missileattack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new MissileAttack();
                dispose();
            }
        });
        apeiron = new JButton("Apeiron");
        apeiron.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new Apeiron();
                dispose();
            }
        });
        rpg = new JButton("RPG");
        rpg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new RPG();
                dispose();
            }
        });
        add(pacman);
        add(snake);
        add(missileattack);
        add(apeiron);
        add(rpg);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
