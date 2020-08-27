package seqtournament;

import java.net.*;
import java.io.*;
import java.util.Scanner;


// https://medium.com/martinomburajr/java-create-your-own-hello-world-server-2ca33b6957e

public class seqServer {
    public static void main(String[] args) {
        int portNum = 8081;
        System.out.println("Started Server on port: "+portNum);
        while (true) {
            // Create a new socket
            try(ServerSocket serverSocket = new ServerSocket(8081)) {
                // Wait for a connection to the socket
                Socket connectionSocket = serverSocket.accept();
                System.out.println("Accepted Connection");

                // Get the streams for the socket
                InputStream inputStream = connectionSocket.getInputStream();
                OutputStream outputStream = connectionSocket.getOutputStream();

                // Create a printwriter for output
                PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                
                // Read the input
                String inputString = new Scanner(inputStream).useDelimiter("\\Z").next(); // Scan all input into a single string

                /*

                // Convert the input into a boardstate and request a move from the sequencium player
                int[][] boardState = new int[][] {{0, 0}, {0, 0}};

                CliffBooth seqPlayer = new CliffBooth();
                int[] move = seqPlayer.makeMove(boardState);
                */

                // Send output and close current connection
                output.println(inputString+" From a newly compiled booth");
                System.out.println(inputString);
                output.flush();
                connectionSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}