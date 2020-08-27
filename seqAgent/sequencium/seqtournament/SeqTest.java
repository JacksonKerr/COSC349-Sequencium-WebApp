package seqtournament;

import sequencium.*;
import java.util.Arrays;
import java.util.Scanner;

public class SeqTest {
    
    public static void main(String[] args) {
        int[][] currBoard = {{-1, 0, 0},
                            {0, 0, 1}};
        
        Player cliffBooth = new BoardTree();
        System.out.println(cliffBooth.getName());
        
        // For 5 moves
        int maxMoves = 2;
        int numMoves = 0;
        while (Utilities.hasMove(currBoard)) {
            if (numMoves >= maxMoves) {
                break;
            }
            System.out.println("*********************************************");
            String printBoard = stringifyBoard(currBoard);
            System.out.println("Initial board after "+numMoves+" moves");
            System.out.println(printBoard);
            int[] move = cliffBooth.makeMove(currBoard);
            currBoard[move[0]][move[1]] = move[2];
            //flipBoard(currBoard);
            
            numMoves++;
            
            printBoard = stringifyBoard(currBoard);
            System.out.println("Board after "+numMoves+" move");
            System.out.println(printBoard);
            
            Scanner sc = new Scanner(System.in);
            int row = sc.nextInt();
            int col = sc.nextInt();
            int val = sc.nextInt();
            currBoard[row][col] = val;
        }
    }










    public static void flipBoard(int[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                board[row][col] *= -1;
            }
        }
    }

    public static String stringifyBoard(int[][] nboard) { 
        StringBuilder sb = new StringBuilder();
        for (int[] row : nboard) {
            for (int tile : row) {
                sb.append(String.valueOf(tile) + " ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}