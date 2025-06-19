package com.example.npuzzleai;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            PuzzleGUI gui = new PuzzleGUI();
            gui.setVisible(true);
        });
    }
}