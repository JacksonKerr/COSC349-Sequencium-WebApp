package tests;

import sequencium.Game;
import sequencium.Player;
import java.util.ArrayList;

public class RunMontyCarlo {

    public static void main(String[] args) {
        coords();
        System.out.println("\n\n");
        coords1();
    }

    /**
     * Method for find reflecting coordinate values for shar and megan.
     */
    private static void coords() {
        for(int i=0; i < 6/2; i++){
            for (int j=0; j<6; j++){
                int mirrorI = (-1* (i -3)) + 2;
                int mirrorJ = (-1* (j -3)) + 2;
                System.out.format("%d %d -> %d %d\n", i, j, mirrorI, mirrorJ);
            }
        }
    }

    private static void coords1() {
        int nRows = 3;
        int nCols = 5;
        boolean rowsEven = nRows % 2 == 0;
        boolean colsEven = nRows % 2 == 0;
        int split = (nRows/2) + (rowsEven ? 0 : 1);
        for (int i = 0; i < split; i++) {
            for (int j = 0; j < nCols; j++) {
                int mirrorI = (nRows -i) - 1;
                int mirrorJ = (nCols -j) - 1;
                int[] position = {mirrorI, mirrorJ};
                System.out.format("%d %d -> %d %d\n", i, j, mirrorI, mirrorJ);
            }
        }
    }


    /**
     * ==================================================================
     *                              RESULTS 
     * ==================================================================
     * 
     * This is after 1,000,000 games for all statistics
     * 
     * rate stands for wins/losses without draws
     * percent stands for wins/losses with draws
     * 
     * no set moves vs no set moves:
     *      Win rate: 53.04
     *      Loss rate: 46.96
     *      Win percent: 42.01
     *      Loss percent: 37.19
     * 
     * {{1, 1, 2}, {2, 2, 3}} vs no set moves:
     *      Win rate: 53.14
     *      Loss rate: 46.86
     *      Win percent: 42.08
     *      Loss percent: 37.11
     * 
     * {{1, 1, 2}, {2, 2, 3}} vs {{4, 4, 2}, {3, 3, 3}}:
     * 
     *      Win rate: 53.05
     *      Loss rate: 46.95
     *      Win percent: 41.99
     *      Loss percent: 37.17
     */

    /**
     * Performs setup operations that involves finding out information of the 
     * game from the first move.
     * 
     * @param board initial board state
     */
    // public static void montyCarlo() {
        
    //     int winsPlayer1 = 0;
    //     int winsPlayer2 = 0;
    //     int numberOfGames = 1000000;
        
    //     int[][] player1FirstMoves = {{1, 1, 2}, {2, 2, 3}}; // new int[0][0]; 
    //     int[][] player2FirstMoves = {{4, 4, 1}, {3, 5, 1}, {4, 5, 1}, {5, 4, 1}}; //new int[0][0];
        
    //     Player player1 = new MontyCarlo("Player 1", player1FirstMoves);
    //     Player player2 = new MontyCarlo("Player 2", player2FirstMoves);

    //     for (int i = 0; i < numberOfGames; i++) {
    //         Game game = new Game(player1, player2);
    //         game.run();

    //         int[][] board = game.boardCopy();
    //         int outcome = whoWon(board);

    //         // System.out.println(String.valueOf(outcome));
    //         // printBoard(board);

    //         if (outcome > 0) winsPlayer1++;
    //         if (outcome < 0) winsPlayer2++;
    //     } 
    //     System.out.println("Games won");
    //     System.out.println(player1.getName() + " won " + String.valueOf(winsPlayer1) + " out of " + String.valueOf(numberOfGames));
    //     System.out.println(player2.getName() + " won " + String.valueOf(winsPlayer2) + " out of " + String.valueOf(numberOfGames) + "\n");

    //     double winPercent = ((double) winsPlayer1 * 100) / ((double) numberOfGames);
    //     double lossPercent = ((double) winsPlayer2 * 100) / ((double) numberOfGames);
    //     double winRate = ((double) winsPlayer1 * 100) / (((double) winsPlayer1) + ((double) winsPlayer2));
    //     double lossRate = ((double) winsPlayer2 * 100) / (((double) winsPlayer1) + ((double) winsPlayer2));
        
    //     System.out.println(player1.getName() + " Statistics");
    //     System.out.printf("Win rate: %.2f\n", winRate);
    //     System.out.printf("Loss rate: %.2f\n", lossRate);
    //     System.out.printf("Win percent: %.2f\n", winPercent);
    //     System.out.printf("Loss percent: %.2f\n\n", lossPercent);
    // }

    // private static int whoWon(int[][] board) {
    //     int p1High = 0;
    //     int p2High = 0;

    //     int cell;
    //     for (int i = 0; i < 6; i++) {
    //         for (int j = 0; j < 6; j++) {
    //             cell = board[i][j]; 
    //             if (cell > 0 && cell > p1High) {
    //                 p1High = cell;
    //                 continue;
    //             } 
    //             if (cell < 0 && cell < p2High) {
    //                 p2High = cell;
    //             }
    //         }
    //     }

    //     if (p1High == -p2High) {
    //         return 0;
    //     }

    //     int score = (p1High > -p2High) ? p1High : p2High;
    //     return score;
    // }

    // private static void printBoard(int[][] board) {
    //     StringBuilder sb = new StringBuilder();
    //     for (int[] row : board) {
    //         for (int tile : row) {
    //             String strValue = (tile >= 0) ? " " + String.valueOf(tile) : String.valueOf(tile);
    //             sb.append(strValue+ " ");
    //         }
    //         sb.append('\n');
    //     }
    //     System.out.println(sb.toString());
    // } 
}