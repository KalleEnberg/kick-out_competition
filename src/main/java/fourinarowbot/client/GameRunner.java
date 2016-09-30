package fourinarowbot.client;

import fourinarowbot.gameengine.GameEngine;
import fourinarowbot.gameengine.HaxBot;

public class GameRunner {

    public static void main(final String[] args) {
        final String     playerName   = "L33TH4CK3R";
        final String     gameName     = "EpicGame";
        final GameEngine myGameEngine = new HaxBot();

        RemoteGame.startGame(playerName, gameName, myGameEngine);
    }
}
