package seqtournament;

import sequencium.*;

public class Sequencium {

    private static Player agent = new BoardTree();
    private static Player player = new RandomPlayer();

    public static void main(String[] args) {
        playFirst();
    }

    public static void playFirst() {
        Game game = new Game(agent, player);
        game.run();
    }
    
    public static void playSecond() {
        Game game = new Game(player, agent);
        game.run();
    }   
}
