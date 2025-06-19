import java.util.Arrays;

public class PuzzleState {
    private int[][] board;
    private int g; // Chi phí từ trạng thái đầu đến trạng thái hiện tại
    private PuzzleState parent;
    private int blankRow, blankCol; // Vị trí ô trống

    public PuzzleState(int[][] board, int g, PuzzleState parent) {
        this.board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.board[i][j] = board[i][j];
                if (board[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
        this.g = g;
        this.parent = parent;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getG() {
        return g;
    }

    public PuzzleState getParent() {
        return parent;
    }

    public int getBlankRow() {
        return blankRow;
    }

    public int getBlankCol() {
        return blankCol;
    }

    // Hàm heuristic 1: Tổng số ô sai vị trí
    public int heuristic1() {
        int[][] goal = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != goal[i][j] && board[i][j] != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    // Hàm heuristic 2: Tổng khoảng cách Manhattan
    public int heuristic2() {
        int distance = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != 0) {
                    int value = board[i][j];
                    int goalRow = (value - 1) / 3;
                    int goalCol = (value - 1) % 3;
                    distance += Math.abs(i - goalRow) + Math.abs(j - goalCol);
                }
            }
        }
        return distance;
    }

    // Hàm heuristic 3: Tổng số ô sai hàng và sai cột
    public int heuristic3() {
        int wrongRows = 0, wrongCols = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != 0) {
                    int value = board[i][j];
                    int goalRow = (value - 1) / 3;
                    int goalCol = (value - 1) % 3;
                    if (i != goalRow) wrongRows++;
                    if (j != goalCol) wrongCols++;
                }
            }
        }
        return wrongRows + wrongCols;
    }

    public int getHeuristic(int heuristicType) {
        switch (heuristicType) {
            case 1: return heuristic1();
            case 2: return heuristic2();
            case 3: return heuristic3();
            default: return 0;
        }
    }

    // Kiểm tra tính khả thi của trạng thái puzzle
    public boolean isSolvable() {
        int[] flat = new int[9];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                flat[k++] = board[i][j];
            }
        }

        int inversions = 0;
        for (int i = 0; i < 9; i++) {
            if (flat[i] == 0) continue;
            for (int j = i + 1; j < 9; j++) {
                if (flat[j] != 0 && flat[i] > flat[j]) {
                    inversions++;
                }
            }
        }

        // 8-puzzle có thể giải được nếu số đảo ngược là chẵn
        return inversions % 2 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleState that = (PuzzleState) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public void printBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}