 package tests;

import sequencium.*;
import seqtournament.CliffBooth;

public class MirrorTest {
    private static Player cliffBooth = new CliffBooth();

    public static void main(String[] args) {
        System.out.println(cliffBooth.getName());

        int[][] currBoard = {{1, 0, 0, 0, 0, 0},
                             {3, 2, 0, 0, 0, 0},
                             {0, 0, 3, 4,  0, 0},
                             {0, 0, -5, 0, 0, 0},
                             {0, 0, -4, -3, 0, 0},
                             {0, 0, 0, 0, -2, -3},
                             {0, 0, 0, 0, 0, -1}};

        int[] move = cliffBooth.makeMove(currBoard);

        System.out.format( "%d %d \n", move[0], move[1]);
    }

}
