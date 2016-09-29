package fourinarowbot.client;

import fourinarowbot.gameengine.GameEngine;
import fourinarowbot.gameengine.HaxBot;

public class GameRunner {

    public static void main(final String[] args) {
        final String     playerName   = "MrHeyhey";
        final String     gameName     = "MyGame1";
        final GameEngine myGameEngine = new HaxBot();

        RemoteGame.startGame(playerName, gameName, myGameEngine);
    }
}
