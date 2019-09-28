package com.whiuk.philip.games;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class Launcher extends JFrame {
    public static void main(final String[] args) {
        new Launcher();
    }

    public Launcher() {
        super();
        setLayout(new FlowLayout());
        buildComponents();
        pack();
        setVisible(true);
    }
    private void buildComponents() {
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
        JButton missileattack = new JButton("Missile Attack");
        missileattack.addActionListener(e -> {
            new MissileAttack();
            dispose();
        });
        JButton apeiron = new JButton("Apeiron");
        apeiron.addActionListener(e -> {
            new Apeiron();
            dispose();
        });
        JButton rpg = new JButton("RPG");
        rpg.addActionListener(e -> {
            new RPG();
            dispose();
        });
        add(pacman);
        add(snake);
        add(missileattack);
        add(apeiron);
        add(rpg);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
