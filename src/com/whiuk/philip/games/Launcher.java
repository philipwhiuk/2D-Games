package com.whiuk.philip.games;

import com.whiuk.philip.games.apeiron.Apeiron;
import com.whiuk.philip.games.breakout.Breakout;
import com.whiuk.philip.games.missileAttack.MissileAttack;
import com.whiuk.philip.games.pacman.Pacman;
import com.whiuk.philip.games.snake.Snake;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;

import javax.swing.*;

@SuppressWarnings("serial")
public class Launcher extends JFrame {
    public static void main(final String[] args) {
        new Launcher();
    }

    Launcher() {
        super();
        setLayout(new FlowLayout());
        buildComponents();
        pack();
        setVisible(true);
    }
    private void buildComponents() {
        JButton apeiron = new JButton("Apeiron");
        apeiron.addActionListener(e -> {
            new Apeiron();
            dispose();
        });
        JButton breakout = new JButton("Breakout");
        breakout.addActionListener(e -> {
            new Breakout();
            dispose();
        });
        JButton missileattack = new JButton("Missile Attack");
        missileattack.addActionListener(e -> {
            new MissileAttack();
            dispose();
        });
        JButton pacman = new JButton("Pacman");
        pacman.addActionListener(e -> {
            new Pacman();
            dispose();
        });
        JButton snake = new JButton("Snake");
        snake.addActionListener(e -> {
            new Snake();
            dispose();
        });
        add(apeiron);
        add(breakout);
        add(missileattack);
        add(pacman);
        add(snake);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
