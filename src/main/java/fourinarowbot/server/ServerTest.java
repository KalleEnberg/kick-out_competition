package fourinarowbot.server;

import fourinarowbot.board.BoardImpl;
import fourinarowbot.domain.Coordinates;
import fourinarowbot.gameengine.MyN00bGameEngine;
import fourinarowbot.server.response.GetBoardResponse;
import fourinarowbot.server.response.PlaceMarkerResponse;

public class ServerTest {

    public static void main(final String[] args) {
        final ServerRestController server   = new ServerRestController();
        final String               gameName = "myGame1";

        new Thread(() -> {
            String                 playerName   = "Player1";
            final MyN00bGameEngine playerEngine = new MyN00bGameEngine();
            startPlaying(server, gameName, playerName, playerEngine);
        }).start();

        new Thread(() -> {
            String                 playerName   = "Player2";
            final MyN00bGameEngine playerEngine = new MyN00bGameEngine();
            startPlaying(server, gameName, playerName, playerEngine);
        }).start();
    }

    private static void startPlaying(final ServerRestController server, final String gameName, final String playerName, final MyN00bGameEngine playerEngine) {
        String message = null;
        while (true) {
            //            System.out.println(playerName + " getting board state...");
            final GetBoardResponse boardStateResponse = server.getBoardState(playerName, gameName);
            if (boardStateResponse.getMessage() != null) {
                message = boardStateResponse.getMessage();
                break;
            }

            System.out.println(playerName + " got board");

            final BoardImpl   board       = new BoardImpl(boardStateResponse.getBoardState().getMarkers());
            final Coordinates coordinates = playerEngine.getCoordinatesForNextMakerToPlace(board);
            //            System.out.println(playerName + " placing marker...");
            final PlaceMarkerResponse placeMarkerResponse = server.placeMarker(playerName, gameName, coordinates.getX(), coordinates.getY());
            System.out.println(playerName + " placed marker");
            if (placeMarkerResponse.getMessage() != null) {
                board.print(); // TODO: REMOVE
                message = placeMarkerResponse.getMessage();
                break;
            }
            sleep(500);
        }
        System.out.println(playerName + " stopped playing after message: " + message);
    }

    private static void sleep(final long time) {
        try {
            Thread.sleep(time);
        }
        catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}