package seqtournament;

import sequencium.*;
import java.util.Queue;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Stores a tree of all possible boardstates from the current move onwards. // TODO Set max depth
 */
public class BoardTree implements Player {
    public int leavesEvaluated;
    public int numNodes;

    private final boolean verbose = true;

    BoardNode root;
    ArrayList<BoardNode> leaves;
    
    // cached acted varaibles
    private int nRows;
    private int nCols;
    private int turnCount;
    private boolean playerStarts;
    private boolean hasSetup = false; 
    private boolean isStandardBoard;

    // cached variables
    private int[][] invPositions;
    private int[][] gridPositions;
    private String[] gridPositionHashes;
    private Hashtable<String, int[]> positionMap = new Hashtable<String, int[]>();
    private Hashtable<int[], String> positionHashMap = new Hashtable<int[], String>();

    // uncompiled states
    private final boolean TEST_BOARD = true;
    private final boolean PERFORM_CHECK = true;
    private final boolean CHECK_BOARD_METRICS = true;
    
    /***
     * Default Construstor. See 'setup' for ad-hoc constructor.
     */
    public BoardTree() {
    }

    /**
     * Performs setup operations that can occur from the first move. Acts as constructor
     * when given initial infromation about the board.
     * 
     * Setup involves finding board dimensions and caching position values and hash codes.
     * These variables can be used later on in the script without being recomputed or 
     * allocated in memory. 
     * 
     * @param board initial board state
     */
    private void setup(int[][] board) {

        // creates test board
        if (TEST_BOARD) board = testBoard();

        this.hasSetup = true;
        this.nRows = board.length;
        this.nCols = board[0].length;
        int numPositions = nRows * nCols;
        this.leaves = new ArrayList<BoardNode>();
        
        // conditional
        this.isStandardBoard = nRows == 6 && nCols == 6;
        this.playerStarts = isBoardMovesEven(board);
        this.turnCount = this.playerStarts ? 0 : 1;

        // creates array of all possible positions on board
        this.gridPositions = new int[numPositions][2];
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                int index = (row * nRows) + col;
                this.gridPositions[index][0] = row;
                this.gridPositions[index][1] = col;
            }
        }
        // creates hashtables for positions and string hashed positions.
        this.gridPositionHashes = new String[numPositions];
        for (int i = 0; i < numPositions; i++) {

            int[] position = gridPositions[i];
            String hash = hashCoords(position);

            gridPositionHashes[i] = hash;
            positionMap.put(hash, position);
            positionHashMap.put(position, hash);
        }
        // cache inverse position array
        int split = nRows - (nRows/2);
        this.invPositions = new int[split*nCols][4];
        for (int i = 0; i < split; i++) {
            for (int j = 0; j < nCols; j++) {
                int index = (i * nRows) + j;
                this.invPositions[index][0] = i;
                this.invPositions[index][1] = j;
                this.invPositions[index][2] = (nRows -i) - 1;
                this.invPositions[index][3] = (nCols -j) - 1;
            }
        }

        // creates root node
        this.root = new BoardNode(null, board, true);
    }

    /**
     * -------------------------------------------------------------------------
     *                          Implemented Methods
     * -------------------------------------------------------------------------
     */

    /**
     * @return name of agent
     */
    @Override
    public String getName() {
        return "Cliff Booth";
    }

    /**
     * Returns the presumed best move, and updates the tree + evaluation values
     * 
     * @return proposed move {i, j, value}
     */
    @Override
    public int[] makeMove(int[][] board) {
        
        if (!hasSetup) 
            setup(board);
        
        if (opponentPassed(board)) 
            return handleNoMove(board);

        if (canShadow(board)) 
            return shadow(board);
        
        // finds best move
        int bestEval = Integer.MIN_VALUE;
        BoardNode nextMove = null;
        BoardNode[] possibleMoves = this.root.children;
        for (BoardNode move : possibleMoves) {
            if (move.evaluation > bestEval) {
                nextMove = move;
                bestEval = move.evaluation;
            }
        }
        // removes other possible move children
        for (BoardNode move : possibleMoves) {
            if (!move.equals(nextMove)) {
                move.removeChildren();
                move = null; // TODO check if works in for each?
            }
        }

        this.root = nextMove;
        deepenTree();
        return this.root.move;
    }

    /**
     * -------------------------------------------------------------------------
     *                       Shadowing Opponent Methods
     * -------------------------------------------------------------------------
     */

    /**
     * This method returns true if it we are in the situation where we are playing second
     * and can shadow moves, which will guarantee a draw. 
     *
     * foundlead refers to whether we have found a single reflected position on the board
     * where the player has made a move we haven't. If zero more than one lead occurs, then
     * we are not able to mirror the opponent.
     *
     * @param board state
     * @return whether player can shadow other person
     */
     private boolean canShadow(int[][] board) {

        boolean foundLead = false;
        for (int[] p : invPositions) {
            if (board[p[0]][p[1]] != board[p[2]][p[3]]) {
                if (foundLead) return false;
                foundLead = true;
            }
        }
        return foundLead;
     }

     /**
      * TODO comment
      * 
      * @param board state
      * @return shadowing move {i, j, value}
      */
     private int[] shadow(int[][] board) {

        int cell, invCell;
        int[] turn = new int[3];

        // looks through all inverse postions
        for (int[] p : invPositions) {
            cell = board[p[0]][p[1]];
            invCell = board[p[2]][p[3]];

            if (cell != invCell) {
                if (cell != 0) {
                    turn[0] = p[0];
                    turn[1] = p[1];
                    turn[2] = cell;
                } else {
                    turn[0] = p[2];
                    turn[1] = p[3];
                    turn[2] = invCell;
                }
                return turn;
            }
        }

        System.err.println("Shadow move was not found. Method should not have been invoked");
        System.exit(0);
        return turn;
     }

     /**
      * 
      * @param board
      * @return
      */
     private boolean opponentPassed(int[][] board) {
        for (int[] p : gridPositions) {
            if (board[p[0]][p[1]] != root.board[p[0]][p[1]]) return false; 
        }
        return true;
     }

     /**
      * 
      * @param board
      * @return
      */
     private int[] handleNoMove(int[][] board) {
        int[] a = new int[3];
        return a;
     }


    /**
     * -------------------------------------------------------------------------
     *                                Utilites
     * -------------------------------------------------------------------------
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
     * Compares whether two boards have the same values
     * 
     * @return whether boards are the same
     */
    private boolean compareBoards(int[][] board1, int[][] board2) {
        for (int i = 0; i < board1.length; i++) {
            if (!Arrays.toString(board1[i]).equals(Arrays.toString(board2[i]))) return false;
        }
        return true;
    }
    /**
     * Converts board to string
     * 
     * @param board
     */
    private String stringifyBoard(int[][] nboard) { 
        StringBuilder sb = new StringBuilder();
        for (int[] row : nboard) {
            for (int tile : row) {
                sb.append(String.valueOf(tile) + " ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    /**
     * Returns the presumed best move, and updates the tree + evaluation values
     * 
     * @return The presumed best move for the current (root) boardstate
     */
    public int[] findNextMove(int[][] updatedBoard) {
        if (root.children.length < 1) {
            System.out.println("ERROR: Root has no children");
        }
        if (leaves.size() == 0) {
            System.out.println("ERROR: No leaves exist!");
        }
        System.out.println("Num nodes: "+numNodes);

        // If the root is not == to the current board
        if (!compareBoards(this.root.board, updatedBoard)) {
            setRoot(updatedBoard);
            //deepenTree();
        }
        System.out.println("Len of leaves: "+leaves.size());
        
        leavesEvaluated = 0;
        root.evaluateNodes();
        System.out.println("Leaves evaluated: "+leavesEvaluated+"\n");
        
        
        BoardNode bestChild = null; // Will cause error if not changed (it should be)
        int bestChildEval = Integer.MIN_VALUE; // No move is worst move
        
        // For each child of root
        for (int i = 0; i < root.children.length; i++) {
            BoardNode child = root.children[i];
            if (child.evaluation > bestChildEval) { // If the child's eval is better than the best currently known eval
                bestChildEval = child.evaluation;
                bestChild = child;
            }
        }
        if (verbose && bestChild == null) {
            System.err.println("CRITICAL ERROR: Root has no children with an evaluation > Integer.MIN_VAL");
        }
        setRoot(bestChild.board); // Set the board our turn will create to be the new root
        //deepenTree();
        return bestChild.originMove; // Return the best found move
    }

    /**
     * Sets the root of the tree to a given child of the root node.
     * @param newRoot
     */
    private void setRoot(int[][] updatedBoard) {
        BoardNode newRoot = null;
        BoardNode[] children = this.root.children;
        for (BoardNode child : children) {
            if (compareBoards(child.board, updatedBoard)) {
                newRoot = child;
                continue;
            }
            child.removeChildren();
            // If child was a leaf, remove it from the list of leaves
            for (int i = 0; i < leaves.size(); i++) {
                if (leaves.get(i).equals(child)) {
                    this.leaves.remove(i);
                }
            }
            child = null;
            this.numNodes--;
        }
        if (newRoot == null && verbose) {
            for (BoardNode rootsChild : root.children) {
                System.out.println(stringifyBoard(rootsChild.board));
            }
            System.out.println("ERROR: Failed to find new root in children of root");
            System.out.println(stringifyBoard(updatedBoard));
        }
        // Set root to the best child, deepen the tree, and return the best turn
        this.root = newRoot;
    }


    /***
     * Useful to figure out if we are moving first or second.
     * 
     * @param board current state
     * @return 
     */
    private boolean isBoardMovesEven(int[][] board) {
        int movesCount = 0;
        for (int[] pos : gridPositions) {
            if (board[pos[0]][pos[1]] != 0) movesCount++;
        }
        return movesCount % 2 == 0;
    }

    /***
     * Returns the presumed best move, and updates the tree + evaluation values
     * 
     * @return The presumed best move for the current (root) boardstate
     */
    public int[] apparentBestMove() {
        // Find the child of root with the largest evaluation value
        int bestChildEval = Integer.MIN_VALUE;
        BoardNode bestChild = null; // Will cause error if not changed (it should be)
        for (int i = 0; i < root.children.length; i++) {
            BoardNode child = root.children[i];
            if (child.evaluation > bestChildEval) {
                bestChildEval = child.evaluation;
                bestChild = child;
            }
        }
        // Best child is not new root TODO fix this
        // setRoot(bestChild);

        return bestChild.move;
    }


    /**                           TODO UNCOMMENT
     * Checks that opponent make a move.
     * 
     * @return whether boards are the same
     */
    // private boolean opponentPassed(int[][] newState) {
    //     int[][] prevState = this.root.board;
    //     for (int[] pos : gridPositions) {
    //         if (newState[pos[0]][pos[1]] != prevState[pos[0]][pos[1]]) return true;
    //     }
    //     return false;
    // }


    public int[] handleSameBoard() {
        // method to copy megan and shars work for
        int[] turn = new int[3];
        return turn;
    }

    // /**
    //  * Sets the root of the tree to a given child of the root node.
    //  * @param newRoot
    //  */
    // private void setRoot(int[][] updatedBoard) {
    //     BoardNode[] rootChildren = this.root.children;
    //     boolean newRootIsChild = false;
    //     BoardNode newRoot = null;

    //     // case where oppenent doesn't make move
    //     if (this.root.board.equals(updatedBoard)) {

    //     }

    //     for (int i = 0; i < this.root.children.length; i++) {
    //         if (!rootChildren[i].board.equals(updatedBoard)) { // TODO NOT 100% SURE THIS COMPARISON WORKS!
    //             rootChildren[i].removeChildren();
    //         } else {
    //             newRoot = rootChildren[i];
    //             newRootIsChild = true;
    //         }
    //     if (newRootIsChild) {
    //         this.root = newRoot;
    //         deepenTree();
    //     } else {
    //         System.err.println("ERROR In class BoardTree: New root node must be a child of the current root");
    //     }
    // }

    /***
     * Deepens the tree by 1 level (Called in newRoot)
     * @param newRoot
     * @throws Exception
     */
    private void deepenTree() {
        ArrayList<BoardNode> newLeaves = new ArrayList<BoardNode>();
        ArrayList<BoardNode> oldLeaves = new ArrayList<BoardNode>(leaves);
        while (oldLeaves.size() > 0) {
            BoardNode oldLeaf = oldLeaves.remove(0);
            if (Utilities.hasMove(oldLeaf.board)) {
                newLeaves.add(new BoardNode(oldLeaf.originMove, oldLeaf.board, oldLeaf.playersTurn));
            }
        }
        leaves = newLeaves;
        
        this.leaves = newLeaves;
        // Calculate new evaluation vaues
    }

    private int evaluateBoard(int[][] board) {
        /*
        regions
        -1 0 0 0 0 0 
        -1 -1 -1 -1 -1 -1 
        1 1 -1 2 2 2 
        1 1 -1 2 2 2 
        1 1 -1 2 2 2 
        1 1 -1 2 2 -1 

        player moves away
        0 1 1 1 1 1 
        0 0 0 0 0 0 
        1 1 0 1 1 1 
        2 1 0 1 2 2 
        2 1 0 1 2 3 
        2 1 0 1 2 -1 

        Opponent moves away
        -1 -2 -2 -2 -2 -2 
        -1 -1 -1 -1 -1 -1 
        -1 -1 -1 3 3 3 
        -1 -1 -1 2 2 2 
        -1 -1 -1 2 1 1 
        -1 -1 -1 2 1 0
        */
        
        // REGION BASED METRIC
        // For each region:
            // Get the coords of a space in the region
            // If the space is only the 
            // If the space is only opposition
            // If the space is accessable by both players

        // DISTANCE BASED METRIC
        // If we calculate the players average distance from each space 
        // (Taking unreachable spaces to be boardLen+x dist away) and compare it
        // to the same average for the opposition (Shorter ave = better score)
        // The player will attempt to avoid being boxed off and be incentivised
        // to box off the opponent.
        // Ideally, Squares that are inside regions owned by the other player
        // where the max move inside the region is < out max move inside a region
        // would not be considered in the average.
        // Ideally, shared regions
        
        
        // Calculate average distance:
        return 1;
    }

    /***
     * A stored boardstate
     */
    public class BoardNode {

        // grids
        public int[][] board;
        public int[][] regions = new int[nRows][nCols];
        public int[][] playerMovesAway = new int[nRows][nCols];
        public int[][] opponentMovesAway = new int[nRows][nCols];

        // members
        public int[] originMove;
        public int[] move;
        public BoardNode[] children;
        public boolean playersTurn;
        public int evaluation;
        public boolean isLeaf;

        // metrics
        public int avePlayerMovesAway;
        public int aveOpponentMovesAway;
        public int maxPossibleScore;
        public int playerMaxGuaranteed;
        public int opponentMaxGuaranteed;
        
        // regional metrics
        public int[] regionSizes;
        public int[] regionsMaxScore;
        public int[] regionsLargestNeighbour;
        public boolean[] regionsPlayerOwnsLargest;
        public boolean[] regionsPlayerAvailable;
        public boolean[] regionsOpponentAvailable;

        /**
         * Constructor
         * 
         * Creates tree of possible states. 
         * 
         * @param move The move that led to the current boardstate (null for initial root)
         * @param board The boardstate of a BoardNode
         * @param playersTurn True if at this BoardNode, it is our turn.
         */
        public BoardNode(int[] originMove, int[][] board, boolean playersTurn) {
            numNodes++;
            this.board = board;
            this.playersTurn = playersTurn;

            // metric calculators
            calcDistanceMetrics(); // These can be moved so they are only called in the eval function.
            calcRegionMetrics(); // They are only needed for leaf nodes

            // updates getPossibleMoves dependent members
            int[][] possibleMoves = getPossibleMoves(this.board, playersTurn);

            int numChildren = (possibleMoves.length * 2);
            if (!Utilities.hasMove(board)) leaves.add(this);

            this.children = new BoardNode[numChildren];
            for (int i = 0; i < numChildren; i += 2) {
                
                int x = 0;
                for (int[] move : possibleMoves) {
                    System.out.println(playersTurn+" "+x+" : "+Arrays.toString(move));
                    x++;
                }
                int[] move = possibleMoves[i/2];
                move[2] *= (playersTurn ? 1 : -1); // Negative if opponent move
                
                // appends next move with maximum score
                int[][] nextBoard = createBoardTurn(board, move);
                this.children[i] = new BoardNode(move, nextBoard, !playersTurn);
                
                // appends next move with minimum score
                move[2] = (playersTurn ? 1 : -1); // Negative if opponent move
                nextBoard = createBoardTurn(board, move);
                this.children[i+1] = new BoardNode(move, nextBoard, !playersTurn);
            }
        }

        /**
         * -------------------------------------------------------------------------
         *                               Utilites
         * -------------------------------------------------------------------------
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
         * Generate hash codes of all postions.
         * 
         * @param allPositions all grid coordinate positions
         * @return arraylist of position hashes
         */
        private ArrayList<String> generatePositionHashes(int[][] allPositions) {
            ArrayList<String> hashes = new ArrayList<String>();
            for (int[] position : allPositions) {
                hashes.add(hashCoords(position));
            }
            return hashes;
        }

        /***
         * Handles format for Utilities.neighbours when passed coordinates. This simplifies
         * code when reading.
         * 
         * @return see Utilities.neighbours
         */
        private ArrayList<int[]> getNeighbours(int[] pos) {
            return Utilities.neighbours(pos[0], pos[1], nRows, nCols);
        }

        /**
         * Creates copy of the board
         * 
         * @param board
         */
        private int[][] copyBoardState(int[][] board) {
            int[][] boardClone = new int[nRows][nCols];
            for (int i = 0; i < nRows; i++) {
                boardClone[i] = board[i].clone();
            }
            return boardClone;
        }

        /**
         * Handles a turn on the board
         * 
         * @param board
         */
        private int[][] createBoardTurn(int[][] board, int[] turn) {
            int[][] testBoard = new int[board.length][];
            for (int i = 0; i < testBoard.length; i++) {
                testBoard[i] = board[i].clone();
            }
            int[] testTurn = turn.clone();
            
            if (!this.playersTurn) {
                testTurn[2] *= -1;
                flipBoard(testBoard);
            }
            
            if (!Utilities.checkMove(testTurn, testBoard)) {
                System.out.println(
                    "An invalid turn has been passed from method 'getPossibleMoves'\n"
                    +"Given move\n"
                    +"for turn "+Arrays.toString(turn)+"\n"
                    +"on board \n"+stringifyBoard(board)
                    +"our turn? "+playersTurn+"\n"
                    +"\n"
                    +"Modified move:\n"
                    +"for turn "+Arrays.toString(testTurn)+"\n"
                    +"on board \n"+stringifyBoard(testBoard)
                    +"our turn? "+playersTurn+"\n"
                    );
                System.exit(0);
            }
            int[][] updatedBoard = copyBoardState(board);
            updatedBoard[turn[0]][turn[1]] = turn[2];
            return updatedBoard;
        }

        /**
         * Copies values from an Integer ArrayList to an int array
         * 
         * @param in input array
         * @param out output array 
         * @return output array
         */
        private int[] copyOverIntAL(ArrayList<Integer> in, int[] out) {

            // checks input and output arrays are of same size
            if (PERFORM_CHECK) {
                assert in.size() == out.length;
            }

            for (int i = 0; i < in.size(); i++) out[i] = in.get(i);
            return out;
        }

        /**
         * Copies values from an Boolean ArrayList to an boolean array
         * 
         * @param in input array
         * @param out output array 
         * @return output array
         */
        private boolean[] copyOverBooleanAL(ArrayList<Boolean> in, boolean[] out) {

            // checks input and output arrays are of same size
            if (PERFORM_CHECK) {
                assert in.size() == out.length;
            }

            for (int i = 0; i < in.size(); i++) out[i] = in.get(i);
            return out;
        }

        /***
         * Garbage collection occurs when objects are completely dereferenced from other
         * objects in java. This method removes the references from all nodes below a 
         * selected node. This is done as a recursive process.
         */
        public void removeChildren() {
            for (int i = 0; i < children.length; i++) {
                children[i].removeChildren();
                children[i] = null;
            }
        }

        /**
         * -------------------------------------------------------------------------
         *                            getPossibleMoves
         * -------------------------------------------------------------------------
         */


        /**
         * Switch for whether to get players or opponents moves. This method is used to improve
         * methods performances as they are used and rely on less conditional logic when seperated
         * hopefully improving pipeline processing on cpu.
         * 
         * @param board current board state
         * @return Array of int[] which represent a possible move eg. {row, column, maxValue}
         */
        private int[][] getPossibleMoves(int[][] board, boolean isPlayer) {
            if (isPlayer) {
                return getPlayersMoves(board);
            } else {
                return getOpponentsMoves(board);
            }
        }

        /**
         * Finds players possible moves given a board state. Uses Hashtable initially to ensure
         * that all grid positions are unqiue before converting to int[][]. maxValue will be one
         * more than the maximum neighbouring squares.
         * 
         * @param board current board state
         * @param isPlayer is our player, otherwise evaluates other players moves
         * @return Array of int[] which represent a possible move eg. {row, column, maxValue}
         */
        private int[][] getPlayersMoves(int[][] board) {
            int cell;
            Hashtable<String, int[]> movesTable = new Hashtable<String, int[]>();

            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {

                    // if value on board exists for player, then find neighbours
                    if ((cell = board[i][j]) <= 0) continue;

                    // loops through each neighbour
                    ArrayList<int[]> cellMoves = Utilities.neighbours(i, j, nRows, nCols);
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
         * Finds opponents possible moves given a board state. Uses Hashtable initially to ensure
         * that all grid positions are unqiue before converting to int[][]. maxValue will be one
         * less than the maximum neighbouring squares.
         * 
         * @param board current board state
         * @return Array of int[] which represent a possible move eg. {row, column, maxValue}
         */
        private int[][] getOpponentsMoves(int[][] board) {
            int cell;
            Hashtable<String, int[]> movesTable = new Hashtable<String, int[]>();

            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {

                    // if value on board exists for player, then find neighbours
                    if ((cell = board[i][j]) >= 0) continue;

                    // loops through each neighbour
                    ArrayList<int[]> cellMoves = Utilities.neighbours(i, j, nRows, nCols);
                    for (int[] coords : cellMoves) {

                        // checks value does not already exist at grid position
                        if (board[coords[0]][coords[1]] == 0) {
                            String hash = hashCoords(coords);
                            int[] values = {coords[0], coords[1], cell-1};

                            // checks new hash is better than existing hash maxValue
                            if (!movesTable.containsKey(hash)) {
                                movesTable.put(hash, values);
                            } else {
                                int[] currentValues = movesTable.get(hash);
                                if (cell-1 < currentValues[2]) movesTable.put(hash, values);
                            }
                        }
                    }
                }
            }
            int[][] possibleMoves = movesTable.values().toArray(new int[movesTable.size()][3]);
            int x = 0;
            return possibleMoves;
        }

        /**
         * -------------------------------------------------------------------------
         *                          Metric Calculations
         * -------------------------------------------------------------------------
         */

        /**
         * Handles calcPlayersDistance for both player and opponent. See method below
         * for more details.
         */
        private void calcDistanceMetrics() {
            calcPlayersDistance(this.playerMovesAway, true);
            calcPlayersDistance(this.opponentMovesAway, false);
        }


        /**
         * Uses a breath first approach to finding the distance each player is away 
         * from each square. 
         * 
         * The main structure of the algorithm is that the hashes arraylist is used
         * to manage what grid positions haven't found the minimum distance. When a
         * position without a distance value finds a neighbouring position with a 
         * distance value, we can assume that the distance of this position will be 
         * one more that the one beside it. The hash of that position will then be
         * removed so that the position is not checked again. When a loop of all 
         * hashes occur and no positions are updated, it can be assumed that the 
         * remaining sqaures are not reachable to the player. This grid metric is 
         * computed for the player and the opponent.  
         * 
         * Posisitions are managed by string hashing as int[] hashing uses the memory 
         * address hashes rather than the containing values to hash the positions in
         * the hash table. Address hashing would cause issues when trying to manage 
         * which positions have been updated as this method removes and recomputes 
         * hashes to improve effiecncy. 
         * 
         * Cached variables such as gridPositions, gridPositionHashes and positionMap
         * have been recomputed before by the containing class to avoid unnecessarily
         * recomputing values within for/while loops.
         * 
         * @param output pointed doubled array to contain output
         * @param player whether to compute the distance from the players or opponents
         *               perspective 
         */
        private void calcPlayersDistance(int[][] output, boolean player) {

            int distance = 1;
            int numFreeSquares = 0;
            boolean hasUpdated = true;
            ArrayList<String> toRemove = new ArrayList<String>();
            ArrayList<String> hashes = generatePositionHashes(gridPositions);
            Hashtable<String, Integer> minDistance = new Hashtable<String, Integer>();

            // helper function to explain complex conditional
            Function<int[], Boolean> playerHasValueAt = (int[] pos) -> {
                return (player && this.board[pos[0]][pos[1]] > 0) || (!player && this.board[pos[0]][pos[1]] < 0);
            };

            // helper function to explain complex conditional
            Function<int[], Boolean> opponentHasValueAt = (int[] pos) -> {
                return (player && this.board[pos[0]][pos[1]] < 0) || (!player && this.board[pos[0]][pos[1]] > 0);
            };

            // helper function to handle update to hashing lists and hashtable side effects.
            Function<Integer, Boolean> updatedTable = (Integer dist) -> {
                boolean hasRemovedHashes = toRemove.size() > 0; 
                for (String gridHash : toRemove) {
                    int[] pos = positionMap.get(gridHash);
                    hashes.remove(gridHash);
                    minDistance.put(gridHash, dist);
                }
                toRemove.clear();
                return hasRemovedHashes;
            };

            // updates table with all player/opponent positions
            for (String gridHash : gridPositionHashes) {
                int[] pos = positionMap.get(gridHash);
                if (playerHasValueAt.apply(pos)) {
                    minDistance.put(gridHash, 0);
                    hashes.remove(gridHash);
                }
                if (opponentHasValueAt.apply(pos)) {
                    minDistance.put(gridHash, -1);
                    hashes.remove(gridHash);
                }
            }

            // breath first search
            while (hasUpdated) {
                for (String gridHash : hashes) {
                    int[] pos = positionMap.get(gridHash);
                    for (int[] neighPos : getNeighbours(pos)) {
                        String neighHash = hashCoords(neighPos);
                        if (minDistance.containsKey(neighHash) && minDistance.get(neighHash) >= 0) {
                            toRemove.add(gridHash);
                            break;
                        }
                    }
                }
                // updates table and distance
                hasUpdated = updatedTable.apply(distance);
                distance += 1;
            }

            // updates remaining squares as unreachable
            for (String gridHash : hashes) {
                minDistance.put(gridHash, -2);
            }

            // moves hashed values to output array
            for (String gridHash : gridPositionHashes) {
                int[] pos = positionMap.get(gridHash);
                int dist = minDistance.get(gridHash);
                output[pos[0]][pos[1]] = dist;
            }
        }

        /**
         * Calculate Region Metrics
         * 
         * Method responsible for finding the different regions on the current board
         * state. It additionally calculates the maximum score possible for that 
         * region.
         * 
         */
        private void calcRegionMetrics() {

            // temporary region metrics
            int cell;
            int regionSize;
            int numRegions = 0;
            int largestNeighbour;
            boolean playerOwnsLargest = false;

            // temporary region metrics arrays
            ArrayList<Integer> regionSizesAL = new ArrayList<Integer>();
            ArrayList<Integer> largestNeighbourAL = new ArrayList<Integer>();
            ArrayList<Boolean> playerOwnsLargestAL = new ArrayList<Boolean>();
            ArrayList<Boolean> playerAvailableAL = new ArrayList<Boolean>();
            ArrayList<Boolean> opponentAvailableAL = new ArrayList<Boolean>();
            
            // board region value handlers
            Queue<String> searchQueue = new LinkedList<String>(); 
            ArrayList<String> searched = new ArrayList<String>();
            Hashtable<String, Integer> regionsTable = new Hashtable<String, Integer>();

            // all positions with values cannot be apart of regions
            for (int[] pos : gridPositions) {
                if (this.board[pos[0]][pos[1]] != 0) {
                    regionsTable.put(hashCoords(pos), -1);
                }
            }

            for (String gridHash : gridPositionHashes) {
                if (regionsTable.containsKey(gridHash)) continue;
                
                // exploring new region
                regionSize = 0;
                largestNeighbour = 0;
                searched.add(gridHash);
                searchQueue.add(gridHash);
                int[] pos = positionMap.get(gridHash);
                regionsTable.put(gridHash, numRegions);
                playerAvailableAL.add(this.playerMovesAway[pos[0]][pos[1]] > 0);
                opponentAvailableAL.add(this.opponentMovesAway[pos[0]][pos[1]] < 0); 

                // iteratively search region
                while(!searchQueue.isEmpty()) {
                    regionSize++;
                    String queuedHash = searchQueue.remove();
                    int[] queuedPos = positionMap.get(queuedHash);
                
                    // explores neighbours and adds them to queue if unexplored
                    for (int[] neighPos : getNeighbours(queuedPos)) {
                        String neighHash = hashCoords(neighPos);
                        if (!searched.contains(neighHash) && !regionsTable.containsKey(neighHash)) {
                            searched.add(neighHash);
                            searchQueue.add(neighHash);
                            regionsTable.put(neighHash, numRegions);
                            continue;
                        } 
                        // updates largest current neighbour when new largest found
                        if ((cell = this.board[neighPos[0]][neighPos[1]]) != 0) {
                            playerOwnsLargest = cell  > 0;
                            if (largestNeighbour < Math.abs(cell)) largestNeighbour = Math.abs(cell);
                        }
                    }
                }
                // clean up
                numRegions++;
                regionSizesAL.add(regionSize);
                largestNeighbourAL.add(largestNeighbour);
                playerOwnsLargestAL.add(playerOwnsLargest);
            }

            // moves hashed values to regions array
            for (String gridHash : gridPositionHashes) {
                int[] pos = positionMap.get(gridHash);
                int dist = regionsTable.get(gridHash);
                regions[pos[0]][pos[1]] = dist;
            }

            // arraylist conversions
            this.regionSizes = this.copyOverIntAL(regionSizesAL, new int[numRegions]);
            this.regionsLargestNeighbour = this.copyOverIntAL(largestNeighbourAL, new int[numRegions]);
            this.regionsPlayerAvailable = this.copyOverBooleanAL(playerAvailableAL, new boolean[numRegions]);
            this.regionsOpponentAvailable = this.copyOverBooleanAL(opponentAvailableAL, new boolean[numRegions]);
            this.regionsPlayerOwnsLargest = this.copyOverBooleanAL(playerOwnsLargestAL, new boolean[numRegions]);
            
            // aggregate metrics
            this.regionsMaxScore = new int[numRegions];
            for (int i = 0; i < numRegions; i++) {
                this.regionsMaxScore[i] = this.regionSizes[i] + this.regionsLargestNeighbour[i];
            }

            if (CHECK_BOARD_METRICS) printBoardMetrics();
        }

        /**
         * -------------------------------------------------------------------------
         *                               Evaluation
         * -------------------------------------------------------------------------
         */


        /**
         * Updates all nodes evaluation values, sould be called on root by parent class.
         * 
         * @param board Double indexed array representing the board state.
         * @return The evaluation value of the given node, taking into account the values of it's children reccursively.
         */
        public void evaluateNodes() {
            // Evaluate leaves:
            for (BoardNode leaf : leaves) {
                leaf.evaluation = evaluateBoard(leaf.board);
                leavesEvaluated++;
            }
            root.pullEvalsUpTree();
        }

        public int pullEvalsUpTree() {
            if (this.children.length == 0) {// If is a leaf
                return this.evaluation;
            }



            // else
            int maxChildEval = Integer.MIN_VALUE;
            for (BoardNode child : this.children) {
                int childEvaluation = child.pullEvalsUpTree();
                if (childEvaluation > maxChildEval) {
                    maxChildEval = childEvaluation;
                }
            }
            this.evaluation = maxChildEval;
            return maxChildEval;
        }

        /**
         * -------------------------------------------------------------------------
         *                               DEBUGGING
         * -------------------------------------------------------------------------
         * 
         * TODO all of these methods will not be called in production but will need to 
         * be removed.
         */
        private void printBoardMetrics() {
            System.out.println("\nMetrics\n");
            System.out.format("Board state:\n%s\n", stringifyBoard(this.board));
            System.out.format("Regions:\n%s\n", stringifyBoard(this.regions));
            System.out.format("Distance player state:\n%s\n", stringifyBoard(this.playerMovesAway));
            System.out.format("Distance opponent state:\n%s\n", stringifyBoard(this.opponentMovesAway));
            System.out.println("regionSizes: " + Arrays.toString(regionSizes));
            System.out.println("regionsMaxScore: " + Arrays.toString(regionsMaxScore));
            System.out.println("regionsPlayerAvailable: " + Arrays.toString(regionsPlayerAvailable));
            System.out.println("regionsOpponentAvailable: " + Arrays.toString(regionsOpponentAvailable));
            System.out.println("regionsPlayerOwnsLargest: " + Arrays.toString(regionsPlayerOwnsLargest));
            System.out.println("regionsLargestNeighbour: " + Arrays.toString(regionsLargestNeighbour));
            System.exit(0);
        }
    }

    /**
     * -------------------------------------------------------------------------
     *                               DEBUGGING
     * -------------------------------------------------------------------------
     * 
     * TODO all of these methods will not be called in production but will need to 
     * be removed.
     */

    private int[][] testBoard() {
        int[][] testBoard = {
            { 1,  0,  1,  0,  0,  0},
            { 0,  2,  2,  0,  0,  0},
            { 0,  0,  3,  3,  3,  3},
            { 0,  0,  1,  0,  0,  0},
            {-6, -5, -4, -3, -2, -1},
            { 0,  0,  0,  0,  0, -1},
        };
        return testBoard;
    }

    public void flipBoard(int[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                board[row][col] *= -1;
            }
        }
    }
}