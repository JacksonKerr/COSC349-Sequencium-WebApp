package tests;

import sequencium.*;
import seqtournament.*;
import java.util.Arrays;

public class Benchmarks {

    private static long start;
    private static long end;
    private static long elapsedTime;

    private static Game game;
    private static Player agent1;
    private static Player agent2;

    private static int[][] sixBySix;
    private static int[][] fiveByFive;
    private static int[][] fourByFour;
    private static int[][] threeByThree;
    

    public static void main(String[] args) {
        playerVsRandom();
        playerVsPlayer();
    }

    private static void playerVsRandom() {
        System.out.println("Player vs Random");
        
        agent1 = new CliffBooth();
        agent2 = new RandomPlayer();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 3, 3);
        System.out.println("Three by Three");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);

        agent1 = new CliffBooth();
        agent2 = new RandomPlayer();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 4, 4);
        System.out.println("Four by Four");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);

        agent1 = new CliffBooth();
        agent2 = new RandomPlayer();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 5, 5);
        System.out.println("Five by Five");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);

        agent1 = new CliffBooth();
        agent2 = new RandomPlayer();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 6, 6);
        System.out.println("Six by Six");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);

    }

    private static void playerVsPlayer() {
        System.out.println("Player vs Player");
        
        agent1 = new CliffBooth();
        agent2 = new CliffBooth();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 3, 3);
        System.out.println("Three by Three");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);

        agent1 = new CliffBooth();
        agent2 = new CliffBooth();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 4, 4);
        System.out.println("Four by Four");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);

        agent1 = new CliffBooth();
        agent2 = new CliffBooth();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 5, 5);
        System.out.println("Five by Five");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);

        agent1 = new CliffBooth();
        agent2 = new CliffBooth();
        start = System.nanoTime();
        game = new Game(agent1, agent2, 6, 6);
        System.out.println("Six by Six");
        game.run();
        end = System.nanoTime();
        elapsedTime = end - start; 
        System.out.format("Time taken: %d\n\n", elapsedTime);
    }
}