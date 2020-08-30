package seqplayer;

import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class CliffBooth {
    /**
     * -------------------------------------------------------------------------
     *                          Public Methods
     * -------------------------------------------------------------------------
     */

    /**
     * @return name of agent
     */
    public String getName() {
        return "Cliff Booth";
    }

    /**
     * Returns the presumed best move, and updates the tree + evaluation values
     * 
     * At the start of the game, there are oppening moves setup by the player...
     * 
     * @return proposed move {i, j, value}
     */
    public int[] makeMove(int[][] board, boolean shadowingEnabled, String strategy) {
        if (shadowingEnabled && canShadow(board)) return shadow(board);

        if (strategy.equals("minimax")) {
            BoardTree boardTree = new BoardTree(board);
            return boardTree.getMove();
        }

        else if (strategy.equals("random")) {
            int[][] possibleMoves = BoardTree.getPossibleMoves(board, true);

            // Return invalid move if there is no possible move
            if (possibleMoves.length < 1) return new int[] {-1, -1, -1};

            int randIndex = new Random().nextInt(possibleMoves.length);

            return possibleMoves[randIndex];
        }

        System.out.println("ERROR: Invalid strategy name: "+strategy);
        return new int[1];
    }

    /**
     * -------------------------------------------------------------------------
     *                     Shadowing Opponent Methods
     * -------------------------------------------------------------------------
     */

    /**
     * This method returns true if it we are in the situation where we are playing second
     * and can shadow moves, which will at least guarantee a draw. 
     *
     * This works by checking that all values we have place have been equal too or greater
     * than the values that the opponent has player, plus the opponent making one move we
     * have not.
     *
     * @param board current state
     * @return whether player can shadow other person
     */
    private boolean canShadow(int[][] board) {

        int invI, invJ;
        int nRows = board.length;
        int nCols = board[0].length;

        // handles case where board rows and columns are uneven.
        if (nRows % 2 == 1 && nCols % 2 == 1){
            if (board[(nRows/2)+1][(nCols/2)+1] != 0) return false;
        }

        int difference = 0;
        int cell, invCell, temp;
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                cell = board[i][j];
                invI = (nRows -i) - 1;
                invJ = (nCols -j) - 1;
                invCell = board[invI][invJ];

                if (invCell > 0) {
                    temp = cell;
                    cell = invCell;
                    invCell = temp;
                }

                if (cell > 0 && invCell > 0) {
                    difference++;
                    continue;
                } 
                if (cell < 0 && invCell < 0) {
                    difference++;
                    continue;
                }

                if (cell < -invCell) {
                    difference++;
                    continue;
                }
            }
        }

        return difference == 2;
    }

     /**
      * Shadows opponents move by scoring equal to or greater than their playerd move to 
      * at least draw. 
      *
      * The method looks for the positions where one sqaure has been filled where the  
      * inverse position has not. There is graunteed to be only two matching positions
      * That meet this specification due to this method only being called if 'canShadow'
      * returns truthy.
      *
      * Using the empty position, it then searches the neighbours for the highest possible
      * value it can place into the square.
      *
      * The null statement should never be reached as a shadowing move is graunteed by
      * the 'canShadow' method.
      * 
      * @param board state
      * @return shadowing move {i, j, value}
      */
     private int[] shadow(int[][] board) {

        int invI, invJ;
        int cell, invCell, temp;
        int nRows = board.length;
        int nCols = board[0].length;

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {

                // collecting values
                cell = board[i][j];
                invI = (nRows -i) - 1;
                invJ = (nCols -j) - 1;
                invCell = board[invI][invJ];

                // no match
                if (cell == 0 && invCell == 0) {
                    continue;
                }

                // no match
                if (cell != 0 && invCell != 0) {
                    continue;
                }

                // finds the position we can place into 
                if (cell < 0) {
                    i = invI;
                    j = invJ;
                }

                // looks for the highest possible score we can put down
                int high = 0;
                ArrayList<int[]> positions = Utils.neighbours(i, j, nRows, nCols);
                for (int[] pos : positions) {
                    if ((cell = board[pos[0]][pos[1]]+1) > high) {
                        high = cell;
                    }
                }

                // creates move
                int[] turn = {i, j, high};
                return turn;
            }
        }
        return null;
     }

    /**
     * -------------------------------------------------------------------------
     *                          Debugging Methods
     * -------------------------------------------------------------------------
     */
    private static String stringifyBoard(int[][] board) { 
        StringBuilder s = new StringBuilder();
        for (int[] row : board) {
            for (int val : row) {
                s.append(val + "\t");
            }
            s.append('\n');
        }
        return s.toString();
    }
}