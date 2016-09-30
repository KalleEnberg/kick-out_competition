package fourinarowbot.gameengine;

import fourinarowbot.FourInARowApplication;
import fourinarowbot.board.Board;
import fourinarowbot.domain.Coordinates;
import fourinarowbot.domain.MarkerColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.stream.Collectors;

public class ScoreBot implements GameEngine {
    private PriorityQueue<Priordinates> nextMoveQueue = new PriorityQueue<Priordinates>(10, new Comparator<Priordinates>() {
        @Override
        public int compare(Priordinates first,Priordinates second){
            return first.getPriority().compareTo(second.getPriority());
        }
    });
    private MarkerColor winningColor;
    private Board board;
    @Override
    public Coordinates getCoordinatesForNextMakerToPlace(final Board board, final MarkerColor myColor) {
        ArrayList<Priordinates> availableSpots = new ArrayList<>();
        for(int columnIndex = 0; columnIndex<board.getNumberOfCols(); columnIndex++){
            for(int rowIndex = board.getNumberOfRows()-1; rowIndex>-1; rowIndex--){
                if(!board.isAnyMarkerAt(columnIndex,rowIndex)){
                    availableSpots.add(new Priordinates(columnIndex,rowIndex));
                }
            }
        }
        winningColor = myColor;
        this.board = board;
        availableSpots.stream().filter(this::isSpotPlaceable).collect(Collectors.toList()).forEach(this::queueSpot);
        return getBestPlayableSpot(nextMoveQueue);
    }

    private Coordinates getBestPlayableSpot(final PriorityQueue<Priordinates> nextMoveQueue) {
        while(board.isAnyMarkerAt(nextMoveQueue.peek().getX(),nextMoveQueue.peek().getY())){
            nextMoveQueue.poll();
        }
        return nextMoveQueue.poll();
    }

    private boolean isSpotPlaceable(final Priordinates spot){
        return board.isOutsideBoard(spot.getX(),spot.getY()+1) || board.isAnyMarkerAt(spot.getX(),spot.getY()+1);
    }

    private void queueSpot(final Priordinates spot){
        setSpotPriority(spot);
        nextMoveQueue.add(spot);
    }

    private void setSpotPriority(Priordinates spot){
        int spotX = spot.getX();
        int spotY = spot.getY();
        spot.setPriority(getPrioFromHorizontal(spotX,spotY) + getPrioFromVertical(spotX,spotY) + getPrioFromDiagonal(spotX,spotY));
    }

    private int getPrioFromDiagonal(int spotX, int spotY) {
        return getPrioFromUpstairDiagonal(spotX,spotY) + getPrioFromDownstairDiagonal(spotX,spotY);
    }

    private int getPrioFromDownstairDiagonal(int spotX, int spotY) {
        int prio = 0;
        prio += getPrioFromUpperNeighbours(spotX, spotY, prio);
        prio += getPrioFromLowerNeighbours(spotX, spotY, prio);
        return prio;
    }

    private int getPrioFromLowerNeighbours(final int spotX, final int spotY, int prio) {
        for(int lowerNeighbours = 1; lowerNeighbours < 4; lowerNeighbours++) {
            if (!board.isOutsideBoard(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.getMarker(spotX + lowerNeighbours, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += 4 * lowerNeighbours;
            } else if (!board.isOutsideBoard(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX + lowerNeighbours, spotY + lowerNeighbours)) {
                prio += 3 * lowerNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromUpperNeighbours(final int spotX, final int spotY, int prio) {
        for(int upperNeighbours = 1; upperNeighbours < 4; upperNeighbours++) {
            if (!board.isOutsideBoard(spotX - upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX - upperNeighbours, spotY - upperNeighbours) && board.getMarker(spotX - upperNeighbours, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += 4 * upperNeighbours;
            } else if (!board.isOutsideBoard(spotX - upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX - upperNeighbours, spotY - upperNeighbours)) {
                prio += 3 * upperNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromUpstairDiagonal(int spotX, int spotY) {
        int prio = 0;
        prio += getPrioFromUpperNeighboursUpstairDiagonal(spotX, spotY, prio);
        prio += getPrioFromLowerNeighboursUpstairDiagonal(spotX, spotY, prio);
        return prio;
    }

    private int getPrioFromLowerNeighboursUpstairDiagonal(final int spotX, final int spotY, int prio) {
        for(int lowerNeighbours = 1; lowerNeighbours < 4; lowerNeighbours++) {
            if (!board.isOutsideBoard(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.getMarker(spotX - lowerNeighbours, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += 4 * lowerNeighbours;
            } else if (!board.isOutsideBoard(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY + lowerNeighbours)) {
                prio += 3 * lowerNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromUpperNeighboursUpstairDiagonal(final int spotX, final int spotY, int prio) {
        for(int upperNeighbours = 1; upperNeighbours < 4; upperNeighbours++) {
            if (!board.isOutsideBoard(spotX + upperNeighbours, spotY - upperNeighbours)  && board.isAnyMarkerAt(spotX + upperNeighbours, spotY - upperNeighbours) && board.getMarker(spotX + upperNeighbours, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += 4 * upperNeighbours;
            } else if (!board.isOutsideBoard(spotX + upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX + upperNeighbours, spotY - upperNeighbours)) {
                prio += 3 * upperNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromVertical(int spotX, int spotY) {
        int prio = 0;
        prio += getPrioFromUpperNeighboursVertical(spotX, spotY, prio);
        prio += getPrioFromLowerNeighboursVertical(spotX, spotY, prio);
        return prio;
    }

    private int getPrioFromLowerNeighboursVertical(final int spotX, final int spotY, int prio) {
        for(int lowerNeighbours = 1; lowerNeighbours < 4; lowerNeighbours++) {
            if (!board.isOutsideBoard(spotX, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX, spotY + lowerNeighbours) && board.getMarker(spotX, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += 4 * lowerNeighbours;
            } else if (!board.isOutsideBoard(spotX, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX, spotY + lowerNeighbours)) {
                prio += 3 * lowerNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromUpperNeighboursVertical(final int spotX, final int spotY, int prio) {
        for(int upperNeighbours = 1; upperNeighbours < 4; upperNeighbours++) {
            if (!board.isOutsideBoard(spotX, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX, spotY - upperNeighbours) && board.getMarker(spotX, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += 4 * upperNeighbours;
            } else if (!board.isOutsideBoard(spotX, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX, spotY - upperNeighbours)) {
                prio += 3 * upperNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromHorizontal(int spotX, int spotY) {
        int prio = 0;
        prio += getPrioFromLeftNeighboursHorizontal(spotX, spotY, prio);
        prio += getPrioFromRightNeighboursHorizontal(spotX, spotY, prio);
        return prio;
    }

    private int getPrioFromRightNeighboursHorizontal(final int spotX, final int spotY, int prio) {
        for(int rightNeighbours = 1; rightNeighbours < 4; rightNeighbours++) {
            if (!board.isOutsideBoard(spotX + rightNeighbours, spotY) && board.isAnyMarkerAt(spotX + rightNeighbours, spotY) && board.getMarker(spotX + rightNeighbours, spotY).getColor().equals(winningColor)) {
                prio += 4 * rightNeighbours;
            } else if (!board.isOutsideBoard(spotX + rightNeighbours, spotY) && board.isAnyMarkerAt(spotX + rightNeighbours, spotY)) {
                prio += 3 * rightNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromLeftNeighboursHorizontal(final int spotX, final int spotY, int prio) {
        for(int leftNeighbours = 1; leftNeighbours < 4; leftNeighbours++) {
            if (!board.isOutsideBoard(spotX - leftNeighbours, spotY) && board.isAnyMarkerAt(spotX - leftNeighbours, spotY) && board.getMarker(spotX - leftNeighbours, spotY).getColor().equals(winningColor)) {
                prio += 4 * leftNeighbours;
            } else if (!board.isOutsideBoard(spotX - leftNeighbours, spotY) && board.isAnyMarkerAt(spotX - leftNeighbours, spotY)) {
                prio += 3 * leftNeighbours;
            }else{
                return prio;
            }
            // TODO: 3016-09-39 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    // Run this main to start the game
    public static void main(final String[] args) {
        final FourInARowApplication fourInARowApplication = new FourInARowApplication(new ScoreBot(), true);

        // Run game once
        fourInARowApplication.runGameOnce();

        // Run game multiple times
        //fourInARowApplication.runGameMultipleGames(100);
    }

    private class Priordinates extends Coordinates {
        Integer priority = 0;
        private Priordinates(int x, int y){
            super(x,y);
        }
        private Integer getPriority(){
            return priority;
        }
        private void setPriority(int newpriority){
            priority = newpriority;
        }
    }
}
