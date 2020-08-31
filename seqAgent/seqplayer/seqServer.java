package seqplayer;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

/***
 * seqServer.java
 * 
 * Listens on port 8081 and when given data about a sequencium game, 
 * returns the suggested next move.
 */
public class seqServer {
    public static void main(String[] args) {
        int portNum = 8081;

        System.out.println("Started Server on port: "+portNum);
        while (true) {
            // Create a new socket
            try(ServerSocket serverSocket = new ServerSocket(8081)) {
                // Wait for a connection to the socket
                Socket connectionSocket = serverSocket.accept();
                
                System.out.println("=============================");
                System.out.println("Accepted Connection");
                
                // Get the streams for the socket
                InputStream inputStream = connectionSocket.getInputStream();
                OutputStream outputStream = connectionSocket.getOutputStream();

                // Create a printwriter for output
                PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                
                Scanner inputScan = new Scanner(inputStream);

                int numRows = inputScan.nextInt();
                int numCols = inputScan.nextInt();

                // True if Cliff should use the mirroring strategy when possibe
                boolean cliffMirrors = inputScan.nextBoolean();

                // Either 'random' or 'minimax'
                String cliffStrat = inputScan.next();

                // Create an int[][] from the given data to represent the current boardstate
                int[][] board = new int[numRows][numCols];
                for (int i = 0; i < numRows; i++) {
                    for (int j = 0; j < numCols; j++) {
                        board[i][j] = inputScan.nextInt();
                    }
                }

                // Print debugging info to std out
                System.out.println("Given board");
                for (int[] row : board) { System.out.println(Arrays.toString(row)); }
                
                // Flip the board so it can be played from Cliff's perspective
                board = BoardTree.flipBoard(board);

                // Get the suggested next move
                CliffBooth seqPlayer = new CliffBooth();
                int[] move = seqPlayer.makeMove(board, cliffMirrors, cliffStrat);

                System.out.println("Returned Move: "+Arrays.toString(move));

                // Send output and close current connection
                output.println(Arrays.toString(move));
                output.flush();
                
                System.out.flush();
                connectionSocket.close();
                System.out.println("Closed connection");

            } catch (Exception e) {
                System.out.println("*** An Error Occured ***");
                e.printStackTrace();
            }
        }
    }
}