package seqplayer;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static void main(String[] args) {
        int[][] testBoard = new int[][] {{1, 0, 0, 0},
                                         {0, 0, 0, 0},
                                         {0, 0, 0, -1}};
                           
        System.out.println("Utils:");

        ArrayList<int[]> neighs = neighbours(0, 0, 4, 3);
        for (int[] neigh : neighs) {
            System.out.println(Arrays.toString(neigh));
        }

        System.out.println(hasMove(testBoard));
    }

    public static boolean checkMove(int[] move, int[][] board) {
        int numRows = board.length;
        int numCols = board[0].length;

        int moveRow = move[0];
        int moveCol = move[1];
        int moveVal = move[2];

        // If space is taken, move is invalid
        if (board[moveRow][moveCol] != 0) return false;

        ArrayList<int[]> neighbours = neighbours(moveRow, moveCol, numRows, numCols);

        int maxNeighVal = Integer.MIN_VALUE;
        for (int[] neigh : neighbours) {
            int neighRow = neigh[0];
            int neighCol = neigh[1];
            int neighVal = board[neighRow][neighCol];

            if (maxNeighVal < neighVal) maxNeighVal = neighVal;
        }
        if (moveVal <= maxNeighVal) {
            return true;
        }
        return false;
    }


    public static ArrayList<int[]> neighbours(int r, int c, int rows, int cols) {
        ArrayList<Integer> possRows = new ArrayList<>();
        ArrayList<Integer> possCols = new ArrayList<>();
        ArrayList<int[]> returnList = new ArrayList<>();
        
        // If Row or value is invalid
        if (r < 0 || rows-1 < r || c < 0 || cols-1 < c) {
            return returnList;
        }
        possRows.add(r);
        possCols.add(c);

        if (r != 0) possRows.add(r-1);
        if (c != 0) possCols.add(c-1);

        if (r != rows-1) possRows.add(r+1);
        if (c != cols-1) possCols.add(c+1);

        for (int row : possRows) {
            for (int col : possCols) {
                if (row == 0 && col == 0) continue;
                returnList.add(new int[] {row, col});
            }
        }
        return returnList;
    }

    public static boolean hasMove(int[][] board) {
        for (int[] row : board) {
            for (int val : row) {
                if (val == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}