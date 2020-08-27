package seqtournament;
import sequencium.*;
import java.util.*;

public class Training implements Player {

    public String getName() {
        return "You";
    }

    public int[] makeMove(int[][] board) {
        boolean boardSetup = false;
        Scanner sc = new Scanner(System.in);
        int boardRows;
        int boardColumns;
        boolean cbStarts;
        CliffBooth cb = new CliffBooth();


        System.out.println("Enter 3 values to setup the game:");
        ///When these values are etered if the first one is 1 it means that we start, the next two values are the boards dimensions.
           
        while(sc.hasNextInt()){
            if(!boardSetup){
                if (sc.nextInt()== 1){
                    cbStarts = false;
                }
                boardRows=sc.nextInt();
                boardColumns=sc.nextInt();
                board = new int[boardRows][boardColumns];
                // if (cbStarts){
                //     ///What do we do if cb goes first?
                //     ///This could be something important or could just be play a game before
                // }else{
                // }
            }else{
                agentMove(sc.nextInt(), sc.nextInt(), sc.nextInt(), board); ///Need to make this method
                System.out.println("Board after our move:");
                printBoard(board);
                int[] cbMove = cb.makeMove(board);
                board[cbMove[0]][cbMove[1]] = cbMove[2];
                System.out.println("Board after cb move:");
                printBoard(board);
                ///printboard again??
                ///remake board using cb's moves?
            }
        }
        int[] m = new int[3];
        return m;
    }

    ///Pass this into setup board, this takes
    /**
     * Scanner sc = new Scanner (System.in);
     int x;
     int y;
     int val;
     int boardRows;
     int boardColumns;
     int i=0;
     boolean boardSetup = false;
     CliffBooth cb = new CliffBooth();

     while (sc.hasNextInt()){
     if (!boardSetup){
     System.out.println("Enter board row and cloumn number:");
     boardRows=sc.nextInt();
     boardColumns=sc.nextInt();
     int[][]board = new int[boardRows][boardColumns];
     boardSetup=true;
     }else{
     System.out.println("Enter row, column number and value to put in it:");
     if (i==0){
     x=sc.nextInt();
     }else if (i==1){
     y=sc.nextInt();
     }else if (i==2){
     val = sc.nextInt();
     }else if (i==3){
                 
     }
     i++;
     if(i ==3){
     board[x][y] = val;
     cb.makeMove(board);
     printBoard(board);
     i=0;
     }
     }
     }
     }

     /**
     * Prints current board to stdout
     *
     * @param board
     */
    private static void printBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int tile : row) {
                sb.append(String.valueOf(tile) + " ");
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
    }

    public static void agentMove(int x, int y, int val, int[][] board){
        ///Here is where we make our agents move by passing val to the board at x and y.
        board[x][y] = val;
    }

}

