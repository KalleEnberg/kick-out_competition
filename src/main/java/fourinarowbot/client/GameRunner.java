package fourinarowbot.client;

import fourinarowbot.gameengine.GameEngine;
import fourinarowbot.gameengine.ScoreBot;

public class GameRunner {

    public static void main(final String[] args) {
        final String     playerName   = "IGotThis";
        final String     gameName     = "EpicGame";
        final GameEngine myGameEngine = new ScoreBot();

        RemoteGame.startGame(playerName, gameName, myGameEngine);
    }
}
