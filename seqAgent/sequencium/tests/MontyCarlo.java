package tests;

import java.util.List;
import java.util.Random;
import sequencium.Player;
import sequencium.Utilities;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * To compile the file:
 *     javac -Xlint -cp sequencium.jar seqtests/*.java
 * 
 * To run the file:
 *     java seqtests.Sequecium
 * 
 * @author Max Collier,
 */
public class MontyCarlo implements Player {

    // flags
    private boolean hasSetup = false;
    private boolean createdMove = false;

    // board info
    private int boardWidth;
    private int boardHeight;
    private int[][] previousBoard;
    
    // player variables
    private String name;
    private int moveCount = 0;
    private int[][] openingMoves;

    // other
    private Random numberGen = new Random();
    
    /**
     * Default Constructor
     */
    public MontyCarlo() {
    }

    /**
     * DEBUGGING
     * 
     * Used in the process of monty carlo to see which open moves are better to
     * start the player off with. 
     * 
     * @param openingMoves array of grid coords to place starting moves along with value
     *  eg. [[1, 1, 2], [2, 2, 3], ...] 
     */
    public MontyCarlo(String name, int[][] openingMoves) {
        this.name = name;
        this.openingMoves = openingMoves;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Performs setup operations that involves finding out information of the 
     * game from the first move.
     * 
     * @param board initial board state
     */
    private void setup(int[][] board) {
        boardWidth = board.length;
        boardHeight = board[0].length;
        hasSetup = true;
    }

    /**
     * ------------------------------------------------------------------------------------
     *                                   Local Utilites
     * ------------------------------------------------------------------------------------
     */

     /**
     * Hashes coordinate value pair.
     * 
     * @param coords grid coordinates
     */
    private String hashCoords(int[] coords) {
        return String.valueOf(coords[0]) + " " + String.valueOf(coords[1]);
    }

    /**
     * Prints board to stdout
     * 
     * TODO figure out string formatting
     * 
     * @param board
     */
    private void printBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int tile : row) {
                String strValue = (tile >= 0) ? " " + String.valueOf(tile) : String.valueOf(tile);
                sb.append(strValue+ " ");
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
    } 

    /**
     * Prints out array of possible moves to stdout
     * 
     * @param possibleMoves - grid coordinates
     */
    private void printPossibleMoves(int[][] possibleMoves) {
        StringBuilder sb = new StringBuilder();
        sb.append("Possible Moves for turn " + String.valueOf(this.moveCount) + "\n");
        for (int[] coords : possibleMoves) {
            sb.append("Grid Position: " + String.valueOf(coords[0]) + "," + String.valueOf(coords[1]) + "\t Maximum Score: " + String.valueOf(coords[2]) + '\n');
        }
        sb.append('\n');
        System.out.println(sb.toString());
    } 

    /**
     * Gets the possible moves for the positive player given the board state. Uses Hashtable
     * initially to ensure that all grid positions are unqiue before converting to int[][]. 
     * 
     * @param board current board state
     * @param isPlayer is our player, otherwise evaluates other players moves
     * @return Array of int[] which represent a possible move eg. {row, column, maxValue}
     */
    private int[][] getPossibleMoves(int[][] board, boolean isPlayer) {
        
        int cell;
        Hashtable<String, int[]> movesTable = new Hashtable<String, int[]>();

        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {

                // if value on board exists for player, then find neighbours
                cell = board[i][j];
                if (isPlayer && cell <= 0) continue;
                if (!isPlayer && cell >= 0) continue;

                // loops through each neighbour
                ArrayList<int[]> cellMoves = Utilities.neighbours(i, j, boardWidth, boardHeight);
                for (int[] coords : cellMoves) {

                    // checks value does not already exist at grid position
                    if (board[coords[0]][coords[1]] == 0) {
                        String hash = hashCoords(coords);
                        int[] values = {coords[0], coords[1], cell+1};

                        // checks new hash is better than existing hash maxValue
                        if (!movesTable.containsKey(hash)) {
                            movesTable.put(hash, values);
                        } else {
                            int[] currentValues = movesTable.get(hash);
                            if (cell+1 > currentValues[2]) movesTable.put(hash, values);
                        }
                    }
                }
            }
        }
        int[][] possibleMoves = movesTable.values().toArray(new int[movesTable.size()][3]);
        return possibleMoves;
    }

    /**
     * Agents makes opening moves for the player before making random moves
     * for the rest of the game. 
     * 
     * @param board current board state
     * @return 3 value array [x, y, value]
     */
    public int[] makeMove(int[][] board) {
        int[] turn = new int[3];

        // setup meta information on board
        if (!hasSetup) {
            setup(board);
        }
        
        // does opening moves for first turns, then completes random moves
        // using the possible moves array.
        if (openingMoves != null && moveCount < openingMoves.length) {
            turn = openingMoves[moveCount];
        } else {
            int[][] possibleMoves = getPossibleMoves(board, true);
            
            int moveIdx = numberGen.nextInt(possibleMoves.length);
            turn = possibleMoves[moveIdx]; 
        }

        // if (this.moveCount > 3) {
        //     System.out.println("Current Board State after 3 turns");
        //     printBoard(board);
        //     System.exit(0); 
        // }

        this.moveCount += 1;
        return turn;
    }
}
