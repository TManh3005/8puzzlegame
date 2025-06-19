package com.example.npuzzleai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Vector;
import java.nio.charset.StandardCharsets;

public class PuzzleGUI extends JFrame {
    private Node currentNode;
    private Node goalNode;
    private JButton[][] tiles;
    private JTextArea resultArea;
    private JComboBox<String> algorithmCombo;
    private JComboBox<String> heuristicCombo;
    private JButton solveButton, resetButton, shuffleButton, customButton;
    private JLabel statusLabel;
    private static final Font VIETNAMESE_FONT = new Font("Arial", Font.PLAIN, 14);

    public PuzzleGUI() {
        setTitle("8-Puzzle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 600);

        State goalState = new State(3);
        goalState.createGoalArray();
        goalNode = new Node(goalState, 0);

        State initialState = new State(3);
        initialState.createGoalArray();
        currentNode = new Node(initialState, 0);

        JPanel puzzlePanel = new JPanel(new GridLayout(3, 3, 5, 5));
        puzzlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tiles = new JButton[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j] = new JButton();
                tiles[i][j].setFont(new Font("Arial", Font.BOLD, 24));
                updateTile(i, j);
                final int row = i, col = j;
                tiles[i][j].addActionListener(e -> handleTileClick(row, col));
                puzzlePanel.add(tiles[i][j]);
            }
        }

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        algorithmCombo = new JComboBox<>(new String[]{"BFS", "A*"});
        algorithmCombo.setFont(VIETNAMESE_FONT);
        heuristicCombo = new JComboBox<>(new String[]{"Số ô sai vị trí", "Khoảng cách Manhattan", "Số ô sai hàng/cột", "Khoảng cách Euclid"});
        heuristicCombo.setFont(VIETNAMESE_FONT);
        heuristicCombo.setEnabled(false);
        algorithmCombo.addActionListener(e -> heuristicCombo.setEnabled(algorithmCombo.getSelectedIndex() == 1));

        solveButton = new JButton("Giải");
        solveButton.setFont(VIETNAMESE_FONT);
        resetButton = new JButton("Đặt lại");
        resetButton.setFont(VIETNAMESE_FONT);
        shuffleButton = new JButton("Xáo trộn");
        shuffleButton.setFont(VIETNAMESE_FONT);
        customButton = new JButton("Tùy chỉnh");
        customButton.setFont(VIETNAMESE_FONT);

        JLabel algorithmLabel = new JLabel("Thuật toán:");
        algorithmLabel.setFont(VIETNAMESE_FONT);
        JLabel heuristicLabel = new JLabel("Heuristic:");
        heuristicLabel.setFont(VIETNAMESE_FONT);

        solveButton.addActionListener(e -> solvePuzzle());
        resetButton.addActionListener(e -> resetPuzzle());
        shuffleButton.addActionListener(e -> shufflePuzzle());
        customButton.addActionListener(e -> customPuzzle());

        controlPanel.add(algorithmLabel);
        controlPanel.add(algorithmCombo);
        controlPanel.add(heuristicLabel);
        controlPanel.add(heuristicCombo);
        controlPanel.add(solveButton);
        controlPanel.add(resetButton);
        controlPanel.add(shuffleButton);
        controlPanel.add(customButton);

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(VIETNAMESE_FONT);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        statusLabel = new JLabel("Nhấn vào ô để di chuyển, chọn 'Tùy chỉnh' để nhập ma trận, hoặc chọn 'Giải' để tìm lời giải.");
        statusLabel.setFont(VIETNAMESE_FONT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(puzzlePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void updateTile(int i, int j) {
        int value = currentNode.state.value[i * 3 + j];
        tiles[i][j].setText(value == 0 ? "" : String.valueOf(value));
        tiles[i][j].setBackground(value == 0 ? Color.LIGHT_GRAY : Color.WHITE);
    }

    private void handleTileClick(int row, int col) {
        int pos = currentNode.state.posBlank(currentNode.state.value);
        int blankRow = pos / 3;
        int blankCol = pos % 3;

        if ((Math.abs(row - blankRow) == 1 && col == blankCol) || (Math.abs(col - blankCol) == 1 && row == blankRow)) {
            State newState = new State(3);
            System.arraycopy(currentNode.state.value, 0, newState.value, 0, 9);
            newState.value[blankRow * 3 + blankCol] = newState.value[row * 3 + col];
            newState.value[row * 3 + col] = 0;
            currentNode = new Node(newState, 0);
            updateBoard();
            statusLabel.setText("Ô đã được di chuyển.");
            checkWin();
        } else {
            statusLabel.setText("Ô không thể di chuyển!");
        }
    }

    private void updateBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                updateTile(i, j);
            }
        }
    }

    private void solvePuzzle() {
        if (!currentNode.state.isSolvable()) {
            resultArea.setText("Trạng thái này không có lời giải!\nVui lòng nhập lại hoặc xáo trộn.");
            statusLabel.setText("Không thể giải!");
            return;
        }

        solveButton.setEnabled(false);
        statusLabel.setText("Đang giải...");
        resultArea.setText("");

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() {
                try {
                    Solver solver = new Solver();
                    String algorithm = (String) algorithmCombo.getSelectedItem();
                    int heuristicType = heuristicCombo.getSelectedIndex() + 1;

                    TextAreaOutputStream taos = new TextAreaOutputStream(resultArea);
                    System.setOut(new java.io.PrintStream(taos, true, StandardCharsets.UTF_8.name()));

                    Result result;
                    if (algorithm.equals("BFS")) {
                        result = solver.solveBFS(currentNode, goalNode, nodes -> publish(nodes));
                    } else {
                        result = solver.solveAStar(currentNode, goalNode, heuristicType);
                    }

                    if (result == null) {
                        resultArea.append("Lỗi: Kết quả trả về null!\n");
                        return null;
                    }

                    resultArea.append(result.showResult(true));
                    if (result.error == null) {
                        Vector<int[]> solution = solver.getResult();
                        if (solution == null || solution.isEmpty()) {
                            resultArea.append("Lỗi: Không có đường giải hợp lệ!\n");
                        } else {
                            resultArea.append("\nCác bước giải:\n");
                            for (int[] state : solution) {
                                for (int i = 0; i < state.length; i++) {
                                    resultArea.append(state[i] + " ");
                                    if ((i + 1) % 3 == 0) resultArea.append("\n");
                                }
                                resultArea.append("\n");
                            }

                            int option = JOptionPane.showConfirmDialog(
                                PuzzleGUI.this,
                                "Bạn có muốn xem ma trận tự chạy từng bước?",
                                "Tự chạy lời giải",
                                JOptionPane.YES_NO_OPTION
                            );

                            if (option == JOptionPane.YES_OPTION) {
                                runSolutionSteps(solution);
                            }
                        }
                    }
                } catch (Exception e) {
                    resultArea.append("Lỗi khi giải: " + e.getMessage() + "\n");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int steps = chunks.get(chunks.size() - 1);
                statusLabel.setText("Đang giải... Số bước tối đa: " + steps);
            }

            @Override
            protected void done() {
                solveButton.setEnabled(true);
                statusLabel.setText("Giải xong!");
            }
        };

        worker.execute();
    }

    private void runSolutionSteps(Vector<int[]> solution) {
        solveButton.setEnabled(false);
        resetButton.setEnabled(false);
        shuffleButton.setEnabled(false);
        customButton.setEnabled(false);
        statusLabel.setText("Đang chạy lời giải...");

        new Thread(() -> {
            try {
                int stepCount = 0;
                for (int i = 1; i < solution.size(); i++) {
                    int[] state = solution.get(i);
                    final int currentStep = stepCount + 1;
                    SwingUtilities.invokeAndWait(() -> {
                        State newState = new State(3);
                        System.arraycopy(state, 0, newState.value, 0, 9);
                        currentNode = new Node(newState, 0);
                        updateBoard();
                        statusLabel.setText("Đang chạy lời giải... Bước: " + currentStep);
                    });
                    stepCount++;
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultArea.append("Lỗi khi chạy lời giải: " + e.getMessage() + "\n");
                    e.printStackTrace();
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    solveButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    shuffleButton.setEnabled(true);
                    customButton.setEnabled(true);
                    statusLabel.setText("Đã chạy xong lời giải!");
                });
            }
        }).start();
    }

    private void resetPuzzle() {
        State initialState = new State(3);
        initialState.createGoalArray();
        currentNode = new Node(initialState, 0);
        updateBoard();
        resultArea.setText("");
        statusLabel.setText("Puzzle đã được đặt lại.");
    }

    private void shufflePuzzle() {
        State newState = new State(3);
        newState.createRandomArray();
        currentNode = new Node(newState, 0);
        if (!currentNode.state.isSolvable()) {
            shufflePuzzle();
        }
        updateBoard();
        resultArea.setText("");
        statusLabel.setText("Puzzle đã được xáo trộn.");
    }

    private void customPuzzle() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        JTextField[][] inputFields = new JTextField[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                inputFields[i][j] = new JTextField(2);
                inputFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                inputFields[i][j].setFont(VIETNAMESE_FONT);
                inputPanel.add(inputFields[i][j]);
            }
        }

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Nhập ma trận 3x3 (0-8, 0 là ô trống)", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int[] newValues = new int[9];
            boolean validInput = true;
            boolean[] used = new boolean[9];

            try {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        String text = inputFields[i][j].getText().trim();
                        if (text.isEmpty()) {
                            validInput = false;
                            break;
                        }
                        int value = Integer.parseInt(text);
                        if (value < 0 || value > 8 || used[value]) {
                            validInput = false;
                            break;
                        }
                        used[value] = true;
                        newValues[i * 3 + j] = value;
                    }
                }
            } catch (NumberFormatException e) {
                validInput = false;
            }

            if (!validInput) {
                statusLabel.setText("Ma trận không hợp lệ! Vui lòng nhập số từ 0-8, không trùng lặp.");
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ma trận 3x3 với số từ 0-8, không trùng lặp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            State newState = new State(3);
            newState.value = newValues;
            currentNode = new Node(newState, 0);
            if (!currentNode.state.isSolvable()) {
                statusLabel.setText("Ma trận không có lời giải!");
                resultArea.setText("Trạng thái này không có lời giải!\nVui lòng nhập lại hoặc xáo trộn.");
                return;
            }

            updateBoard();
            resultArea.setText("");
            statusLabel.setText("Ma trận tùy chỉnh đã được áp dụng.");
        }
    }

    private void checkWin() {
        if (currentNode.state.isGoal(goalNode.state)) {
            statusLabel.setText("Chúc mừng! Bạn đã giải được puzzle!");
            JOptionPane.showMessageDialog(this, "Bạn đã thắng!", "Chúc mừng", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static class TextAreaOutputStream extends java.io.OutputStream {
        private JTextArea textArea;

        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            SwingUtilities.invokeLater(() -> {
                textArea.append(String.valueOf((char) b));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }
}