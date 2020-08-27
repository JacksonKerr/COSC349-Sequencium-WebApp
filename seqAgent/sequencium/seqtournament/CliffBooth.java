package seqtournament;

import java.util.List;
import java.util.Random;
import sequencium.Player;
import sequencium.Utilities;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

/**
 * To compile the file:
 *     javac -Xlint -cp sequencium.jar seqtournament/*.java
 * 
 * To run the file:
 *     java seqtournament.Sequecium
 * 
 * @author Max Collier,
 */
public class CliffBooth implements Player {

    // flags
    private boolean hasSetup = false;
    private boolean createdMove = false;

    // board info
    private int boardRows;
    private int boardColumns;
    private int[][] previousBoard;
    
    // player variables
    private int moveCount = 0;
    private int[][] startingMoves;

    private BoardTree moveTree;

    // other
    private int depth = 0;
    private  final int maxDepth = 30;
    private Random numberGen = new Random();

    /**
     * Default Constructor
     */
    public CliffBooth() {
    }

    public String getName() {
        return "Clifford Booth";
        
    }

    /**
     * Performs setup operations that involves finding out information of the 
     * game from the first move.
     * 
     * @param board initial board state
     */
    private void setup(int[][] board) {
        boardRows = board.length;
        boardColumns = board[0].length;
        hasSetup = true;
    }

    /**
     * ------------------------------------------------------------------------------------
     *                                   Print Utilites
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
     * @param board
     */
    private void printBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int tile : row) {
                sb.append(String.valueOf(tile) + " ");
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

        for (int[] move : possibleMoves) {
            sb.append(Arrays.toString(move)+" ");
        }
        sb.append('\n');
        System.out.println("Possible Moves: "+sb.toString());
    } 

    /**
     * ------------------------------------------------------------------------------------
     *                                   Local Utilites
     * ------------------------------------------------------------------------------------
     */

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

        for (int i = 0; i < boardRows; i++) {
            for (int j = 0; j < boardColumns; j++) {

                // if value on board exists for player, then find neighbours

                cell = board[i][j];
                if (isPlayer && cell <= 0) continue;
                if (!isPlayer && cell >= 0) continue;

                // loops through each neighbour (boardRows and BoardColumns in the right order?
                ArrayList<int[]> cellMoves = Utilities.neighbours(i, j, boardRows, boardColumns);
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

    private int scoreBoardState(int[][] board) {
        return 0;
    }

    /***
     * TODO NEEDS COMMENT
     * @param board
     * @return
     */
    private boolean isBoardMovesEven(int[][] board) {
        //Figures out if we hvae gone second or first.
        int moves = 0;
        for (int i =0; i<board.length; i++) {
            for (int j=0; j<board[i].length; j++) {
                if(board[i][j]!=0) {
                moves++;
                }
            }
        }
        if(moves % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * Assume the other player will move first, and mirror their move
     * only do if canShadow();
     * returns place to move I assume?
     * https://mathwithbaddrawings.com/sequencium/ <-- Read here
     * 
     * @param board
     * @return
     */
    private int[] copyOtherPlayer(int[][] board) {
        // Assume the other player will move first, and mirror their move
        // only do if canShadow();
        // returns place to move I assume?
        // https://mathwithbaddrawings.com/sequencium/ <-- Read here
        int halfRows = boardRows/2; //Split board width down middle
        int halfColumns = boardColumns/2; //Split lines down middle, halfway point. 
        int[] whereToMove = new int[2];
        boolean rowsEven = true;
        boolean columnsEven =true;
        
        if (!(boardRows%2 ==0)){
            rowsEven=false;
        }
        if (!(boardColumns %2 ==0)){
            columnsEven=false;
        }

        for(int i=0; i<halfRows; i++){
            for (int j=0; j<halfColumns; j++){
                if (!((0-(board[i][j])) == (board[boardRows-(i+1)][boardColumns-(j+1)]))){ // should this be boardRowsand boardHeight, not half?
                    whereToMove[0] = i;
                    whereToMove[1] = j;
                }
            }
        }
       
        if (!rowsEven){ 
            //If the width is not even, iterate down the middle column on width.
            System.out.println("Rows uneven ");
            for (int heightCount =0; heightCount<halfRows; heightCount++){
                if((0-board[halfRows][heightCount]) != board[halfRows][boardColumns-(heightCount+1)]){
                    whereToMove[0] = halfRows;
                    whereToMove[1] = heightCount;
                }
            }
        }
        if (!columnsEven){
            //If the height is uneven, iterate through the middle row of height.
           
            for (int widthCount =0; widthCount<halfColumns; widthCount++){
                System.out.println("Culumns uneven.");
                if ((0-board[widthCount][halfColumns]) != board[boardRows-(widthCount+1)][halfColumns]){
                    System.out.println(widthCount+ " " +(halfColumns+1)+ ", " + widthCount + " " + (boardColumns-halfColumns+1));
                    whereToMove[0] = widthCount;
                    whereToMove[1] = halfColumns;
                    System.out.println("1: " + whereToMove[0] + " 2: " + whereToMove[1]);
                }
            }
        }
  
        if (board[whereToMove[0]][whereToMove[1]] ==0){ //aka if the board at the non matching spot isn't empty
            return whereToMove;
        }else {
            int[] moveHere = new int[2];
            moveHere[0] = boardRows-(whereToMove[0]+1);
            moveHere[1] = boardColumns-(whereToMove[1]+1);
            return moveHere;
        }
    }

    private boolean can(int[][] board) {
        
        int mismatchCount = 0;
        for(int i=0; i < boardRows/2; i++){
            for (int j=0; j<boardRows; i++){
                int mirrorI = (-1* (i -3)) + 2;
                int mirrorJ = (-1* (j -3)) + 2;
                if (board[i][j] != board[mirrorI][mirrorJ]) {
                    if (mismatchCount > 1) {
                        return false;
                    } else {
                        mismatchCount++;
                    }
                }
            }
        }
        return true;
    }

    private boolean canShadow(int[][] board){
        //This method returns true if it we are in the situation where we are playing second
        //and can shadow moves, which will guarantee a draw. 
        int halfWidth = boardRows/2; //Split board width down middle
        int halfHeight = boardColumns/2; //Split lines down middle, halfway point. 
        int not = 0;
        boolean widthEven = boardRows % 2 == 0; // check width and height are rows and columns
        boolean heightEven = boardColumns % 2 == 0;


        if(isBoardMovesEven(board)){ //If it is an even number of board moves, we are second and connot shadow.
            return false;
        }
        for(int i=0; i<halfWidth; i++){
            for (int j=0; j<halfHeight; j++){
                if (!(((0-board[i][j])) == (board[boardRows-(i+1)][boardColumns-(j+1)]))){
                    not++;
                }
            }
        }
        if (!widthEven && !heightEven){
            if (board[halfWidth+1][halfHeight+1] != 0){
                return false; //In this situation the cetre piece has been taken, it is impossible to shadow. 
            }
        }
        if (!widthEven){
            System.out.println("width not even" );
            //If the width is not even, iterate down the middle column on width. 
            for (int heightCount =0; heightCount<halfWidth; heightCount++){
                if((0-board[heightCount][halfWidth+1])!= board[boardRows-(heightCount+1)][halfWidth+1]){
                    not++;
                }
            }
        }
        if (!heightEven){
            System.out.println("height not even"); 
            //If the height is uneven, iterate through the middle row of height.
            for (int widthCount =0; widthCount<halfHeight; widthCount++){
                int counts = widthCount+1;
                if ((0-board[halfHeight+1][widthCount]) != board[halfHeight+1][boardColumns-(counts)]){

                    not++;
                }
            }
        }
        System.out.println("not: " +not);
        if (not!=1){
            return false;
        }
        System.out.println("Board can be shaowed.");
        return true; 

    }

    /*** TODO What does this method do, is it still needed?
     * @param board
     * @return
     */
    private int[] createMove(int[][] board) {
        // jackson and max
        createdMove = true;
        int[] turn = {0, 0, 1};
        return turn;
    }
    /**
     * maxMove for minimax implementation.
     * 
     * @param board Double indexed array representing the board state
     * @return The score for a given boardstate
     */
    private int maxMove(int[][] board) {
        if (this.depth > this.maxDepth) {
            return boardEvaluation(board);
        }
        int[][] possibleMoves = getPossibleMoves(board, true);
        //printBoard(board);
        printPossibleMoves(possibleMoves);
        
        int bestScore = Integer.MIN_VALUE; // Set best score to min int (ie. Assume the worst)
        this.depth++;
        for (int[] move : possibleMoves) {
            try {
                if (Utilities.hasMove(board)) {
                    int[][] boardWithMove = applyMove(board, move);
                    int moveScore = boardEvaluation(boardWithMove);
                    moveScore = this.minMove(boardWithMove); // Score the opponents best move
                    if (moveScore > bestScore) {
                        bestScore = moveScore;
                    }
                }
            } catch(Exception e) {
                System.out.println("Error in maxMove");
                e.printStackTrace();
            }
        }
        this.depth -= 1;
        return bestScore;
    }
    

    /**
     * minMove for minimax implementation
     *
     * @param board Double indexed array representing the board state. 
     * This function attempts to find the minium possible score
     * @return The score for a given boardstate
     */
    
    private int minMove(int[][] board) {
        if (this.depth > this.maxDepth || !Utilities.hasMove(board)) {
            return boardEvaluation(board);
        }
        int[][] possibleMoves = getPossibleMoves(board, false);
        int bestScore = Integer.MAX_VALUE; // Set best score to min int (ie. Assume the worst)
        this.depth++;
        for (int[] move : possibleMoves) {
            move[2] *= -1;
            try {
                if (Utilities.hasMove(board)) {
                    int[][] boardWithMove = applyMove(board, move);
                    int moveScore = boardEvaluation(boardWithMove);
                    moveScore = this.maxMove(boardWithMove);
                    if (moveScore < bestScore) {
                        bestScore = moveScore;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        this.depth -= 1;
        return bestScore;
    }

    /**
     * Takes a board state, and an array representing a move, and returns the boardstate 
     * after the move is made.
     * 
     * @param initialBoard A representation of the initial board
     * @param move Should folow the format {row, column, value}
     * @return New boardstate with the given move applied to it
     */
    public int[][] applyMove(int[][] initialBoard, int[] move) throws Exception {
        // Create a copy of the board
        int[][] boardCopy = new int[initialBoard.length][];
        for (int row = 0; row < initialBoard.length; row++) {
            boardCopy[row] = initialBoard[row].clone();
        }

        int moveRow = move[0];
        int moveCol = move[1];
        int moveVal = move[2];

        if (boardCopy[moveRow][moveCol] == 0) { // If desired space is empty
            boardCopy[moveRow][moveCol] = moveVal;
            return boardCopy;
        } else {
            String exceptString = "ERROR in applyMove: Tried to move into space that is already taken: "+moveRow+", "+moveCol;
            throw new Exception(exceptString);
        }
    }
    
    /**
     * Evaluated a given boardstate
     * @param board 
     * @return an int score of how good the boardstate is
     */
    public int boardEvaluation(int[][] board) {
        // Find the max positive value, and min negative value
        int maxPositiveVal = 0;
        int minNegativeValue = 0;
        for (int[] row : board) {
            for (int value : row) {
                if (maxPositiveVal < value) {   maxPositiveVal = value; }
                if (value < minNegativeValue) { minNegativeValue = value; }
            }
        }
        return maxPositiveVal + minNegativeValue; // Player score - Oponent score
    }
    

    /***
     * Returns the chosen move for a given boardstate
     * 
     * @param board Current Boardstate.
     * 
     * @return int[] representing the location, and value of the choosen move
     */
    public int[] makeMove(int[][] board) {

        int[] turn;

        if (!hasSetup) {
            setup(board);
        }
        
        if (canShadow(board)) { // Should be if board can be made symmetrical this turn
            System.out.println ("Got here");
            boolean isFirstToMove = isBoardMovesEven(board);
            if (isFirstToMove || createdMove) {
                turn = createMove(board);
            } else {
                turn = copyOtherPlayer(board);
            }

            this.previousBoard = board; // POINTER ISSUE HERE!

            printBoard(board); // For debugging

            return turn;
        }

        int[] myMove = {0, 0 , 0};
        return myMove;
    }

        
        /**
        // Otherwise use minimax if symmetry is not possible
        int[][] possibleMoves = getPossibleMoves(board, true); // Get possible moves
        printPossibleMoves(possibleMoves);
        printBoard(board);
        System.out.println("=================================");
        System.out.println("Calculating Move:");
        int bestScore = Integer.MIN_VALUE; // No move is the worst
        // Will cause an error unless replaced with another move (Which it should be)
        int[] bestMove = new int[] {-1, -1, -1};
        System.out.println("Finding Best Move Using Minimax:");
        for (int[] move : possibleMoves) {
            try {
                // Create a copy of the board
                int[][] boardCopy = new int[boardWidth][];
                for (int row = 0; row < board.length; row++) {
                    boardCopy[row] = board[row].clone();
                }
        if (!hasSetup) {
            setup(board);
            moveTree = new BoardTree(board, this.boardWidth, this.boardHeight);
        }
        return bestMove; //bestMove;
    }
    */
}