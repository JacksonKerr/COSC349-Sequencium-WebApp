package seqtournament;

import sequencium.*;
import java.util.Scanner;

public class SequenciumTest {

    private static Player agent = new CliffBooth();
    private static Player player = new Training();

    public static void main(String[] args) {

        Scanner sc = new Scanner (System.in);
        System.out.println("Do you want to go first (yes -> 1, no -> 0)");
        boolean playerStarts = sc.nextInt() == 1;

        System.out.println("Enter number of rows");
        int rows = sc.nextInt();

        System.out.println("enter number of columns");
        int columns = sc.nextInt();

        
        if( playerStarts) {
            playFirst(rows, columns);
        } else {
            int[][] board = new int[rows][columns];
            // agent.setup(board); // does this work?
         playSecond();
        }
    }
    public static void playFirst(int rows, int columns) {
        Game game = new Game(agent, player, rows, columns); //should player be first if they are going first?
        game.run();
    }
    
    public static void playSecond() {
        Game game = new Game(player, agent);
        game.run();
    }
}