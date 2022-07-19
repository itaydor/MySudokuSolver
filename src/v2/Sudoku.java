package v2;

import java.time.Duration;
import java.time.Instant;

public class Sudoku {

    private static final int BOARD_SIZE = 25;

    public static void main(String[] args) {
        Instant start = Instant.now();
        SudokuGridGenerator sudokuGridGenerator = new SudokuGridGenerator();
        int[][] intsBoard;
        Board board;
        intsBoard = gridToBoard(sudokuGridGenerator.generateGrid(BOARD_SIZE));
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                System.out.println("Working on cell: [" + (row + 1) + "," + (column + 1) + "], out of [" + BOARD_SIZE + "," + BOARD_SIZE + "].");
                if(Math.random() < 0.99) {
                    int value = intsBoard[row][column];
                    intsBoard[row][column] = 0;
                    board = new Board(intsBoard);
                    if(!board.solve()){
                        intsBoard[row][column] = value;
                    }
                }
            }
        }
        /*do{

            board = new Board(intsBoard);
        }while (!board.solve());*/
        board = new Board(intsBoard);
        System.out.println("Before:");
        System.out.println(board);
        System.out.println("After:");
        board.solve();
        System.out.println(board);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.getSeconds() +" seconds");
    }

    private static int[][] gridToBoard(int[] grid) {
        int size = (int) Math.sqrt(grid.length);
        int[][] board = new int[size][size];
        int gridIndex = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                board[row][column] = grid[gridIndex++];
            }
        }
        return board;
    }

}
