package fourinarowbot.gameengine;

import fourinarowbot.FourInARowApplication;
import fourinarowbot.board.Board;
import fourinarowbot.domain.Coordinates;
import fourinarowbot.domain.MarkerColor;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Properties;

public class ScoreBot implements GameEngine {
    private PriorityQueue<Priordinates> nextMoveQueue = new PriorityQueue<>();
    private MarkerColor winningColor;
    private Board board;
    @Override
    public Coordinates getCoordinatesForNextMakerToPlace(final Board board, final MarkerColor myColor) {
        ArrayList<Priordinates> availableSpots = new ArrayList<>();
        for(int columnIndex = 0; columnIndex<board.getNumberOfCols()-1; columnIndex++){
            for(int rowIndex = board.getNumberOfRows()-1; rowIndex>0; rowIndex--){
                if(!board.isAnyMarkerAt(columnIndex,rowIndex)){
                    availableSpots.add(new Priordinates(columnIndex,rowIndex));
                }
            }
        }
        //TODO: some kind of priority system with neighbour checking methods (if neighbour has neighbour in same direction etc... = priority HIGH) then pop a priorityQueue.
        //TODO: test tomorrow with spring!
        this.winningColor = myColor;
        this.board = board;
        availableSpots.stream().filter(this::isSpotPlaceable).forEach(this::queueSpot);
        Priordinates spot1 = availableSpots.get(0);
        spot1.setPriority(10);
        Priordinates spot2 = availableSpots.get(availableSpots.size()-1);
        spot1.setPriority(11);
        nextMoveQueue.add(spot1);
        nextMoveQueue.add(spot2);
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
        for(int upperNeighbours = 1; upperNeighbours<4; upperNeighbours++) {
            if (!board.isOutsideBoard(spotX - upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX - upperNeighbours, spotY - upperNeighbours) && board.getMarker(spotX - upperNeighbours, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += 3 * upperNeighbours;
            } else if (!board.isOutsideBoard(spotX - upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX - upperNeighbours, spotY - upperNeighbours)) {
                prio += 2 * upperNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        for(int lowerNeighbours = 1; lowerNeighbours<4; lowerNeighbours++) {
            if (!board.isOutsideBoard(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.getMarker(spotX + lowerNeighbours, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += 3 * lowerNeighbours;
            } else if (!board.isOutsideBoard(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX + lowerNeighbours, spotY + lowerNeighbours)) {
                prio += 2 * lowerNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromUpstairDiagonal(int spotX, int spotY) {
        int prio = 0;
        for(int upperNeighbours = 1; upperNeighbours<4; upperNeighbours++) {
            if (!board.isOutsideBoard(spotX + upperNeighbours, spotY - upperNeighbours)  && board.isAnyMarkerAt(spotX + upperNeighbours, spotY - upperNeighbours) && board.getMarker(spotX + upperNeighbours, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += 3 * upperNeighbours;
            } else if (!board.isOutsideBoard(spotX + upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX + upperNeighbours, spotY - upperNeighbours)) {
                prio += 2 * upperNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        for(int lowerNeighbours = 1; lowerNeighbours<4; lowerNeighbours++) {
            if (!board.isOutsideBoard(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.getMarker(spotX - lowerNeighbours, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += 3 * lowerNeighbours;
            } else if (!board.isOutsideBoard(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY + lowerNeighbours)) {
                prio += 2 * lowerNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromVertical(int spotX, int spotY) {
        int prio = 0;
        for(int upperNeighbours = 1; upperNeighbours<4; upperNeighbours++) {
            if (!board.isOutsideBoard(spotX, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX, spotY - upperNeighbours) && board.getMarker(spotX, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += 3 * upperNeighbours;
            } else if (!board.isOutsideBoard(spotX, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX, spotY - upperNeighbours)) {
                prio += 2 * upperNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        for(int lowerNeighbours = 1; lowerNeighbours<4; lowerNeighbours++) {
            if (!board.isOutsideBoard(spotX, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX, spotY + lowerNeighbours) && board.getMarker(spotX, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += 3 * lowerNeighbours;
            } else if (!board.isOutsideBoard(spotX, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX, spotY + lowerNeighbours)) {
                prio += 2 * lowerNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromHorizontal(int spotX, int spotY) {
        int prio = 0;
        for(int leftNeighbours = 1; leftNeighbours<4; leftNeighbours++) {
            if (!board.isOutsideBoard(spotX - leftNeighbours, spotY) && board.isAnyMarkerAt(spotX - leftNeighbours, spotY) && board.getMarker(spotX - leftNeighbours, spotY).getColor().equals(winningColor)) {
                prio += 3 * leftNeighbours;
            } else if (!board.isOutsideBoard(spotX - leftNeighbours, spotY) && board.isAnyMarkerAt(spotX - leftNeighbours, spotY)) {
                prio += 2 * leftNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        for(int rightNeighbours = 1; rightNeighbours<4; rightNeighbours++) {
            if (!board.isOutsideBoard(spotX + rightNeighbours, spotY) && board.isAnyMarkerAt(spotX + rightNeighbours, spotY) && board.getMarker(spotX + rightNeighbours, spotY).getColor().equals(winningColor)) {
                prio += 3 * rightNeighbours;
            } else if (!board.isOutsideBoard(spotX + rightNeighbours, spotY) && board.isAnyMarkerAt(spotX + rightNeighbours, spotY)) {
                prio += 2 * rightNeighbours;
            }
            // TODO: 2016-09-29 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    // Run this main to start the game
    public static void main(final String[] args) {
        final FourInARowApplication fourInARowApplication = new FourInARowApplication(new ScoreBot()), true);

        // Run game once
        fourInARowApplication.runGameOnce();

        // Run game multiple times
        //        fourInARowApplication.runGameMultipleGames(100);
    }

    private class Priordinates extends Coordinates {
        Integer priority = 0;
        private Priordinates(int x, int y){
            super(x,y);
        }
        private int getPriority(){
            return this.priority;
        }
        private void setPriority(int newpriority){
            this.priority = newpriority;
        }
        public int compareTo(Priordinates other){
            return priority.compareTo(other.getPriority());
        }
    }
}
