package seqplayer;

import java.util.Queue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import java.util.function.Supplier;
import java.util.function.Function;

/***
 * This class uses a breadth first search to explore the children of a given board input, 
 * where it follows each board to its eventual state and chooses the move that is best.
 * It essentially finds moves that can be added to the board, adds each one to a child 
 * of the board, then follows the possible moves of each child until it reaches a final 
 * state then working out which eventual board is best based on either the difference 
 * between player's scores (if our player has won) or how much space there is for either
 *  player to use (more is better)
 */
public class BoardTree {
    private static boolean VERBOSE = false;
    private static boolean PRINT_PROGRESS = false;

    private static final int MAX_NUM_NODES = 250000;
    private static final int WIN_REWARD = 1000;
    private static final int INACCESSABLE_SPACE_DISTANCE = 50;
    private static int numNodes = 0;

    private static int boardsEvaluated;

    // Node values
    private final int[] prevMove; // The previous move that led to this boardstate
    private final int[][] board; // The current boardstate
    private boolean isPlayersMove; // True if the player gets to make the next move on the current boardstate
    private ArrayList<BoardTree> children; // The children of this BoardTree
    private int evaluation;

    /***
     * Constructor: Creates a new BoardTree, and recursively adds children breadth first.
     * 
     * @param board The boardstate of the root node.
     */
    public BoardTree(int[][] board) {
        boardsEvaluated = 0;
        // resets static variables
        numNodes = 0;

        this.prevMove = null;
        this.isPlayersMove = true;
        this.board = cloneBoard(board);

        final Queue<BoardTree> deepestNodes = new LinkedList<BoardTree>();
        deepestNodes.add(this);

        whileLoop:
        while (!deepestNodes.isEmpty()) {
            final BoardTree bottomNode = deepestNodes.remove();
            bottomNode.children = bottomNode.getChildren();

            if (bottomNode.children.isEmpty()) {
                break whileLoop;
            }

            if (PRINT_PROGRESS) {
                System.out.println("numNodes:"+numNodes+" queueLen:"+deepestNodes.size());
            }

            for (BoardTree child : bottomNode.children) {
                deepestNodes.add(child);
            }
        }


        deepestNodes.clear();

        this.evaluateChildren();
        
        if (VERBOSE) {
            System.out.println("numNodes:"+numNodes);
            System.out.println("boardsEvaluated:"+boardsEvaluated);
        }
    }
    /***
     * Constructor: Creates a new BoardTree without any children.
     * 
     * @param prevMove The previous move that led to this boardstate
     * @param board The current boardstate
     * @param isPlayersMove True if the player gets to make the next move on the current
     * boardstate
     */
    private BoardTree(final int[] prevMove, final int[][] board, final boolean isPlayersMove) {
        this.prevMove = prevMove;
        this.board = cloneBoard(board);
        this.isPlayersMove = !isPlayersMove;
        numNodes++;
    }

    /***
     * Used to get the children of this BoardTree by adding each possible move to a 
     * board, and adding this board as a child of the BoardTree.
     * 
     * @return An ArrayList of children BoardNodes generated from this boardnode
     */
    public ArrayList<BoardTree> getChildren() {
        final ArrayList<BoardTree> children = new ArrayList<>();

        int[][] possibleMoves = getPossibleMoves(this.board, this.isPlayersMove);
        
        // For each possible move, add a child node
        for (int[] possMove : possibleMoves) {
            if (numNodes >= MAX_NUM_NODES) return children;

            final int[][] updatedBoard = applyMove(this.board, possMove);
            
            final BoardTree newChild = new BoardTree(possMove, updatedBoard, this.isPlayersMove);

            children.add(newChild);
        }

        // If the node we are creating children for does not have any possible moves for it's 
        //player, but the opponent does, make this node belong to the other player, and get 
        //it's children.
        // This handles the case where a player has "boxed off" an area.
        if (possibleMoves.length < 1 && Utils.hasMove(board)) {
            this.isPlayersMove = !this.isPlayersMove;
            return this.getChildren();
        }
        return children;
    }

    /**
     * Chooses a move based on the child with the best evaluation, remembering the best move it
     * is able to find and returning the best move of all the current board's children.
     * @return The child of this with the best evaluation as ant int array.
     */
    public int[] getMove() {
        int[] bestMove = new int[] {-1, -1, -1};
        int bestEval = Integer.MIN_VALUE;
        for (final BoardTree child : this.children) {
            if (child.evaluation > bestEval) {
                bestEval = child.evaluation;
                bestMove = child.prevMove;
            }
            if (VERBOSE) {
                System.out.println(Arrays.toString(child.prevMove)+":"+child.evaluation);
            }
        }
        delTree(this);

        if (VERBOSE) {
            System.out.println("Chose eval: "+bestEval);
        }
        return bestMove;
    }

    // =========================================================================================================
    //
    //                                           Evaluation Methods
    //
    // =========================================================================================================
    /** 
     * This  checks the children of the board. It uses recursion to continue following the 
     * children of the current board, exiting at a leaf (aka finished board) and then using 
     * the evaluate board method. This means it explores each child and returns the best 
     * score the eventual board can get, allowing us to pick the best move in getMove. 
     * 
     * @return bestEval the int score of the best board once the node has no children, as
     * done by the givenBoard method
    */
    private int evaluateChildren() {
        if (PRINT_PROGRESS) {
            System.out.println("Evaled:"+boardsEvaluated);
        }
        if (this.children == null || this.children.isEmpty()) { // Exit case: Node is a leaf
            return evaluateBoard(this.board);
        }
        
        int bestEval = Integer.MIN_VALUE;
        for (final BoardTree child : this.children) {
            child.evaluation = child.evaluateChildren();
            if (child.evaluation > bestEval) {
                bestEval = child.evaluation;
            }
        }
        return bestEval;
    }

    /**
     * Calculates an evaluation for a given board in its final state, used by
     * the evaluate children method once there are no children
     * 
     * @param givenBoard, the board that the evaluation method will use. 
     * @return an integer representing how good a board is: best if they win,
     *  but also takes into account the difference between the scores (the amount they won by.)
     * If the game hasn't finished, it returns a better score if our agent is closer to more squares
     * on average than the opponen (aka more spread out and able to take areas)
     */
    private static int evaluateBoard(final int[][] givenBoard) {
        final int[][] board = cloneBoard(givenBoard);

        final int numRows = givenBoard.length;
        final int numCols = givenBoard[0].length;

        
        // If the game is complete
        if (!Utils.hasMove(board)) {
            // Find largest absolute move value for player and opponent
            int playerScore = 0;
            int opponentScore = 0;
            for (final int[] row : board) {
                for (final int val : row) {
                    if (val > playerScore) playerScore = val;
                    if (val < opponentScore) opponentScore = val;
                } 
            }
            // Give a big reward + the ammount we won the game by
            return WIN_REWARD + playerScore + opponentScore;
        }
        boardsEvaluated++;

        
        // If the game is incomplete, return the diference between the average 
        // distance that the player and opponent are from each empty square
        int[][] playerDistances = distanceGrid(board);
        int[][] opponentDistances = distanceGrid(flipBoard(board));
        
        int totalPlayerDistance = 0;
        int totalOpponentDistance = 0;
        
        int maxPlayerDistance = 0;
        int maxOpponentDistance = 0;
        
        
        int largestPlayerMove = 0;
        int smallestOpponentMove = 0;
        
        int numEmptySpaces = 0;
        
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (playerDistances[r][c] > 0) {
                    totalPlayerDistance += playerDistances[r][c];
                    numEmptySpaces++;
                }
                if (opponentDistances[r][c] > 0) {
                    totalOpponentDistance += opponentDistances[r][c];
                }
                
                int boardVal = board[r][c];
                if (largestPlayerMove < boardVal) largestPlayerMove = boardVal;
                if (smallestOpponentMove > boardVal) smallestOpponentMove = boardVal;
                
            } 
        }
        int averageDistanceDifference = totalOpponentDistance - totalPlayerDistance;

        int largestMoveDifference = largestPlayerMove + smallestOpponentMove;


        return averageDistanceDifference + largestMoveDifference;
    }

    /**
     * Creates an int[][] where each int[r][c] is equal to the grid distance to a 
     * positive value on the given board. If a space on the board is < 0 (ie. taken 
     * by an opponent) the space on int[][] will be -1. If the space on the board is 
     * > 0 (ie. taken by the player) the space on the grid will be 0.
     * 
     * @param board A boardstate.
     * 
     * @return An int[][] where each int[r][c] is equal to the grid distance to a 
     * positive value on the given board.
    */
    private static int[][] distanceGrid(final int[][] board) {
        final int numRows = board.length;
        final int numCols = board[0].length;

        final int distance = 0;

        final int[][] distanceGrid = new int[numRows][numCols];
        for (final int[] row : distanceGrid) {
            Arrays.fill(row, INACCESSABLE_SPACE_DISTANCE);
        }
        // For each space on the board
        for (int r = 0; r < numRows; r++) {
            nextBoardSpace:
            for (int c = 0; c < numCols; c++) {
                ArrayList<int[]> explored = new ArrayList<>();
                Queue<int[]> toExplore = new LinkedList<>();
                Map<String,Integer> dists = new HashMap<>();

                // Add the space to the explore queue
                toExplore.add(new int[] {r, c, 0});
                
                // While there is more to explore
                while (!toExplore.isEmpty()) {
                    int[] currExplor = toExplore.remove();
                    explored.add(currExplor);
                    int explRow = currExplor[0];
                    int explCol = currExplor[1];
                    int explDist = currExplor[2];

                    String key = Arrays.toString(new int[] {explRow, explCol});
                    dists.put(key, 1);
                    
                    // If the space contains a player's move, add the distance to the 
                    //distanceGrid, and continue on the next boardspace
                    if (board[explRow][explCol] > 0) {
                        distanceGrid[r][c] = explDist;
                        continue nextBoardSpace;
                    }
                    
                    // Otherwise, if the space is empty, add it's neighbours to the queue
                    if (board[explRow][explCol] == 0) {
                        ArrayList<int[]> neighbours = Utils.neighbours(explRow, explCol, numRows, numCols);
                        for (int[] neighbour : neighbours) {
                            int[] temp = new int[] {neighbour[0], neighbour[1], explDist+1};
                            if (dists.containsKey(key)) continue;
                            toExplore.add(temp);
                        }
                    }
                }

                // If a path to one of our spaces is not found, space is inaccessable
                if (board[r][c] != 0) {
                    distanceGrid[r][c] = -1;
                }

            }
        }
        return distanceGrid;
    }

    // =========================================================================================================
    //
    //                                       Get Possible Moves Methods
    //
    // =========================================================================================================

    /**
     * Returns a int[][] where each entry is an array containing the row, col, and max value of a 
     * move.
     * @param board current board state
     * @param isPlayer is our player, otherwise evaluates other players moves
     * @return Array of int[] which represent a possible move eg. {row, column, maxValue}
     */
    public static int[][] getPossibleMoves(final int[][] givenBoard, final boolean isPlayersMove) {
        final int numRows = givenBoard.length;
        final int numCols = givenBoard[0].length;

        final int[][] board = isPlayersMove ? cloneBoard(givenBoard) : flipBoard(givenBoard);

        final ArrayList<int[]> possibleMoves = new ArrayList<>();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                // If the location is empty
                if ((board[r][c]) == 0) {

                    int largestNeighbourVal = 0;

                    // loop through each neighbour
                    final ArrayList<int[]> neighbours = Utils.neighbours(r, c, numRows, numCols);
                    for (final int[] neighbourCoords : neighbours) {
                        final int neighbourRow = neighbourCoords[0];
                        final int neighbourCol = neighbourCoords[1];
                        final int neighbourVal = board[neighbourRow][neighbourCol];

                        
                        // Check if neighbour contains a player's move
                        if (neighbourVal > largestNeighbourVal) {
                            largestNeighbourVal = neighbourVal;
                            // checks new hash is better than existing hash maxValue
                        }
                    }
                    
                    if (largestNeighbourVal > 0) {
                        if (isPlayersMove)  possibleMoves.add(new int[] {r, c, largestNeighbourVal + 1});
                        else                possibleMoves.add(new int[] {r, c, -1*(largestNeighbourVal + 1)});
                    }
                }
            }
        }
        return possibleMoves.toArray(new int[possibleMoves.size()][]);
    }


    // =========================================================================================================
    //
    //                                           Utility Methods
    //
    // =========================================================================================================

    private static void delTree(BoardTree root) {
        if (root.children == null) return;
        for (BoardTree child : root.children) {
            delTree(child);
        }
        root.children.clear();
        root = null;
    }

    /**
    * @return A copy of the given board
    /** 
     * this method clones the board in order to evaluate
     * without altering the state.
     * 
     * @param board a double indexed array representing the board to be cloned
     * @return copy a cloned version of board occupying a new space in memory.
    */
    
    private static int[][] cloneBoard(final int[][] board) {
        final int numRows = board.length;
        final int numCols = board[0].length;

        final int[][] copy = new int[numRows][numCols];
        
        for (int r = 0; r < numRows; r++) {
            copy[r] = board[r].clone();
        }
        return copy;
    }

    /** 
     * Flips the board to be able to evaluate's the other player's score.
     * 
     * @param board the board to be flipped.
     * @return A new board where every position holds the same value as 
     * the given board, but with it's sign changed.
     * eg. board[r][c] = -1 * flippedBoard[r][c]
     */
    public static int[][] flipBoard(final int[][] board) {
        final int numRows = board.length;
        final int numCols = board[0].length;

        final int[][] flippedBoard = cloneBoard(board);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                flippedBoard[r][c] *= -1;
            }
        }
        return flippedBoard;
    }

    /** 
     * Once a move has been chosen, this method updates the board to include it.
     * 
     * @param board The board to have a move added to it.
     * @param move The move to apply to the given board. Should be of format: {row, column, moveValue}
     * @return A new board with the given move applied to it.
    */
    private static int[][] applyMove(final int[][] board, final int[] move) {
        final int[][] updatedBoard = cloneBoard(board);

        final int moveRow = move[0];
        final int moveCol = move[1];
        final int moveVal = move[2];

        updatedBoard[moveRow][moveCol] = moveVal;

        return updatedBoard;
    }
}