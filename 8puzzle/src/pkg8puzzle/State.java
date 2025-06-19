package com.example.npuzzleai;

import java.util.Random;
import java.util.Vector;

public class State {
    public int[] value;
    public int size;
    public static int heuristic = 1; // 1: Misplaced Tiles, 2: Manhattan Distance, 3: Wrong Rows/Columns, 4: Euclidean Distance

    public State(int size) {
        this.size = size;
        value = new int[size * size];
    }

    public void createRandomArray() {
        createGoalArray();
        Random rand = new Random();
        int moves = 20 * size;
        for (int i = 0; i < moves; i++) {
            int[] possibleMoves = {0, 1, 2, 3}; // 0: UP, 1: DOWN, 2: LEFT, 3: RIGHT
            int move = possibleMoves[rand.nextInt(4)];
            switch (move) {
                case 0: UP(); break;
                case 1: DOWN(); break;
                case 2: LEFT(); break;
                case 3: RIGHT(); break;
            }
        }
    }

    public void createGoalArray() {
        for (int i = 0; i < size * size - 1; i++) {
            value[i] = i + 1;
        }
        value[size * size - 1] = 0;
    }

    public int posBlank(int[] state) {
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) return i;
        }
        return -1;
    }

    public void UP() {
        int pos = posBlank(value);
        int row = pos / size;
        if (row < size - 1) {
            value[pos] = value[pos + size];
            value[pos + size] = 0;
        }
    }

    public void DOWN() {
        int pos = posBlank(value);
        int row = pos / size;
        if (row > 0) {
            value[pos] = value[pos - size];
            value[pos - size] = 0;
        }
    }

    public void LEFT() {
        int pos = posBlank(value);
        int col = pos % size;
        if (col < size - 1) {
            value[pos] = value[pos + 1];
            value[pos + 1] = 0;
        }
    }

    public void RIGHT() {
        int pos = posBlank(value);
        int col = pos % size;
        if (col > 0) {
            value[pos] = value[pos - 1];
            value[pos - 1] = 0;
        }
    }

    public boolean isGoal(State goalState) {
        for (int i = 0; i < value.length; i++) {
            if (value[i] != goalState.value[i]) return false;
        }
        return true;
    }

    public int estimate(State goal) {
        if (heuristic == 1) {
            return heuristic1(goal);
        } else if (heuristic == 2) {
            return heuristic2(goal);
        } else if (heuristic == 3) {
            return heuristic3(goal);
        } else if (heuristic == 4) {
            return heuristic4(goal);
        }
        return 0;
    }

    public Vector<State> successors() {
        Vector<State> successors = new Vector<>();
        int pos = posBlank(value);
        int row = pos / size;
        int col = pos % size;

        if (row > 0) {
            State newState = new State(size);
            System.arraycopy(value, 0, newState.value, 0, value.length);
            newState.value[pos] = newState.value[pos - size];
            newState.value[pos - size] = 0;
            successors.add(newState);
        }

        if (row < size - 1) {
            State newState = new State(size);
            System.arraycopy(value, 0, newState.value, 0, value.length);
            newState.value[pos] = newState.value[pos + size];
            newState.value[pos + size] = 0;
            successors.add(newState);
        }

        if (col > 0) {
            State newState = new State(size);
            System.arraycopy(value, 0, newState.value, 0, value.length);
            newState.value[pos] = newState.value[pos - 1];
            newState.value[pos - 1] = 0;
            successors.add(newState);
        }

        if (col < size - 1) {
            State newState = new State(size);
            System.arraycopy(value, 0, newState.value, 0, value.length);
            newState.value[pos] = newState.value[pos + 1];
            newState.value[pos + 1] = 0;
            successors.add(newState);
        }

        return successors;
    }

    private int heuristic1(State goal) {
        int count = 0;
        for (int i = 0; i < value.length; i++) {
            if (value[i] != goal.value[i] && value[i] != 0) count++;
        }
        return count;
    }

    private int heuristic2(State goal) {
        int distance = 0;
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0) {
                int goalPos = -1;
                for (int j = 0; j < goal.value.length; j++) {
                    if (goal.value[j] == value[i]) {
                        goalPos = j;
                        break;
                    }
                }
                int row = i / size, col = i % size;
                int goalRow = goalPos / size, goalCol = goalPos % size;
                distance += Math.abs(row - goalRow) + Math.abs(col - goalCol);
            }
        }
        return distance;
    }

    private int heuristic3(State goal) {
        int wrongRows = 0, wrongCols = 0;
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0) {
                int goalPos = -1;
                for (int j = 0; j < goal.value.length; j++) {
                    if (goal.value[j] == value[i]) {
                        goalPos = j;
                        break;
                    }
                }
                int row = i / size, col = i % size;
                int goalRow = goalPos / size, goalCol = goalPos % size;
                if (row != goalRow) wrongRows++;
                if (col != goalCol) wrongCols++;
            }
        }
        return wrongRows + wrongCols;
    }

    private int heuristic4(State goal) {
        double distance = 0;
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0) {
                int goalPos = -1;
                for (int j = 0; j < goal.value.length; j++) {
                    if (goal.value[j] == value[i]) {
                        goalPos = j;
                        break;
                    }
                }
                int row = i / size, col = i % size;
                int goalRow = goalPos / size, goalCol = goalPos % size;
                distance += Math.sqrt(Math.pow(row - goalRow, 2) + Math.pow(col - goalCol, 2));
            }
        }
        return (int) Math.ceil(distance); // Làm tròn lên để đảm bảo giá trị nguyên
    }

    public boolean isSolvable() {
        int inversions = 0;
        for (int i = 0; i < value.length; i++) {
            if (value[i] == 0) continue;
            for (int j = i + 1; j < value.length; j++) {
                if (value[j] != 0 && value[i] > value[j]) {
                    inversions++;
                }
            }
        }
        return inversions % 2 == 0;
    }
}