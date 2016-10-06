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
    private PriorityQueue<Priordinates> nextMoveQueue = new PriorityQueue<Priordinates>(90, new Comparator<Priordinates>() {
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
        spot.setPriority(getPrioFromHorizontal(spotX,spotY) + getPrioFromVertical(spotX,spotY) + getPrioFromDiagonals(spotX,spotY));
    }

    private int getPrioFromDiagonals(int spotX, int spotY) {
        return getPrioFromUpstairDiagonal(spotX,spotY) + getPrioFromDownstairDiagonal(spotX,spotY);
    }

    private int getPrioFromDownstairDiagonal(int spotX, int spotY) {
        int prio = 0;
        int[] fromupperneighbours = getPrioFromUpperNeighboursDownstairDiagonal(spotX, spotY);
        prio += fromupperneighbours[0];
        prio += getPrioFromLowerNeighboursDownstairDiagonal(spotX, spotY,fromupperneighbours[1],fromupperneighbours[2]);
        return prio;
    }

    private int[] getPrioFromUpperNeighboursDownstairDiagonal(final int spotX, final int spotY) {
        int prio = 0;
        int friendlyNeighbours = 0;
        int hostileNeighbours = 0;
        for(int upperNeighbours = 1; upperNeighbours < 4; upperNeighbours++) {
            if (hostileNeighbours == 0 && !board.isOutsideBoard(spotX - upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX - upperNeighbours, spotY - upperNeighbours) && board.getMarker(spotX - upperNeighbours, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += Math.pow(10,upperNeighbours);
                friendlyNeighbours++;
            } else if (friendlyNeighbours == 0 && !board.isOutsideBoard(spotX - upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX - upperNeighbours, spotY - upperNeighbours)) {
                prio += Math.pow(9,upperNeighbours);
                hostileNeighbours++;
            }else{
                return new int[]{prio,friendlyNeighbours,hostileNeighbours};
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
        }
        return new int[]{prio,friendlyNeighbours,hostileNeighbours};
    }

    private int getPrioFromLowerNeighboursDownstairDiagonal(final int spotX, final int spotY, int friendlyNeighbours, int hostileNeighbours) {
        int prio = 0;
        int friendlyLowerNeighbours = 0;
        int hostileLowerNeighbours = 0;
        for(int lowerNeighbours = 1; lowerNeighbours < 4; lowerNeighbours++) {
            if (hostileLowerNeighbours == 0 && !board.isOutsideBoard(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.getMarker(spotX + lowerNeighbours, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += Math.pow(10,(lowerNeighbours + friendlyNeighbours));
                friendlyLowerNeighbours++;
            } else if (friendlyLowerNeighbours == 0 && !board.isOutsideBoard(spotX + lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX + lowerNeighbours, spotY + lowerNeighbours)) {
                prio += Math.pow(9,(lowerNeighbours + hostileNeighbours));
                hostileLowerNeighbours++;
            }else{
                return prio;
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromUpstairDiagonal(int spotX, int spotY) {
        int prio = 0;
        int[] fromupperneighbours = getPrioFromUpperNeighboursUpstairDiagonal(spotX, spotY);
        prio += fromupperneighbours[0];
        prio += getPrioFromLowerNeighboursUpstairDiagonal(spotX, spotY,fromupperneighbours[1],fromupperneighbours[2]);
        return prio;
    }

    private int[] getPrioFromUpperNeighboursUpstairDiagonal(final int spotX, final int spotY) {
        int prio = 0;
        int friendlyNeighbours = 0;
        int hostileNeighbours = 0;
        for(int upperNeighbours = 1; upperNeighbours < 4; upperNeighbours++) {
            if (hostileNeighbours == 0 && !board.isOutsideBoard(spotX + upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX + upperNeighbours, spotY - upperNeighbours) && board.getMarker(spotX + upperNeighbours, spotY - upperNeighbours).getColor().equals(winningColor)) {
                prio += Math.pow(10,upperNeighbours);
                friendlyNeighbours++;
            } else if (friendlyNeighbours == 0 && !board.isOutsideBoard(spotX + upperNeighbours, spotY - upperNeighbours) && board.isAnyMarkerAt(spotX + upperNeighbours, spotY - upperNeighbours)) {
                prio += Math.pow(9,upperNeighbours);
                hostileNeighbours++;
            }else{
                return new int[]{prio,friendlyNeighbours,hostileNeighbours};
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
        }
        return new int[]{prio,friendlyNeighbours,hostileNeighbours};
    }

    private int getPrioFromLowerNeighboursUpstairDiagonal(final int spotX, final int spotY, int friendlyNeighbours, int hostileNeighbours) {
        int prio = 0;
        int friendlyLowerNeighbours = 0;
        int hostileLowerNeighbours = 0;
        for(int lowerNeighbours = 1; lowerNeighbours < 4; lowerNeighbours++) {
            if (hostileLowerNeighbours == 0 && !board.isOutsideBoard(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.getMarker(spotX - lowerNeighbours, spotY + lowerNeighbours).getColor().equals(winningColor)) {
                prio += Math.pow(10,(lowerNeighbours + friendlyNeighbours));
                friendlyLowerNeighbours++;
            } else if (friendlyLowerNeighbours == 0 && !board.isOutsideBoard(spotX - lowerNeighbours, spotY + lowerNeighbours) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY + lowerNeighbours)) {
                prio += Math.pow(9,(lowerNeighbours + hostileNeighbours));
                hostileLowerNeighbours++;
            }else{
                return prio;
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromVertical(int spotX, int spotY) {
        int prio = 0;
        int[] fromupperneighbours = getPrioFromUpperNeighboursVertical(spotX, spotY);
        prio += fromupperneighbours[0];
        prio += getPrioFromLowerNeighboursVertical(spotX, spotY,fromupperneighbours[1],fromupperneighbours[2]);
        return prio;
    }

    private int[] getPrioFromUpperNeighboursVertical(final int spotX, final int spotY) {
        int prio = 0;
        int friendlyNeighbours = 0;
        int hostileNeighbours = 0;
        for(int upperNeighbours = 1; upperNeighbours < 4; upperNeighbours++) {
            if (hostileNeighbours == 0 && !board.isOutsideBoard(spotX, spotY + upperNeighbours) && board.isAnyMarkerAt(spotX, spotY + upperNeighbours) && board.getMarker(spotX, spotY + upperNeighbours).getColor().equals(winningColor)) {
                prio += Math.pow(10,upperNeighbours);
                friendlyNeighbours++;
            } else if (friendlyNeighbours == 0 && !board.isOutsideBoard(spotX, spotY + upperNeighbours) && board.isAnyMarkerAt(spotX, spotY + upperNeighbours)) {
                prio += Math.pow(9,upperNeighbours);
                hostileNeighbours++;
            }else{
                return new int[]{prio,friendlyNeighbours,hostileNeighbours};
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
        }
        return new int[]{prio,friendlyNeighbours,hostileNeighbours};
    }

    private int getPrioFromLowerNeighboursVertical(final int spotX, final int spotY, int friendlyNeighbours, int hostileNeighbours) {
        int prio = 0;
        int friendlyLowerNeighbours = 0;
        int hostileLowerNeighbours = 0;
        for(int lowerNeighbours = 1; lowerNeighbours < 4; lowerNeighbours++) {
            if (hostileLowerNeighbours == 0 && !board.isOutsideBoard(spotX, spotY - lowerNeighbours) && board.isAnyMarkerAt(spotX, spotY - lowerNeighbours) && board.getMarker(spotX, spotY - lowerNeighbours).getColor().equals(winningColor)) {
                prio += Math.pow(10,(lowerNeighbours + friendlyNeighbours));
                friendlyLowerNeighbours++;
            } else if (friendlyLowerNeighbours == 0 && !board.isOutsideBoard(spotX, spotY - lowerNeighbours) && board.isAnyMarkerAt(spotX, spotY - lowerNeighbours)) {
                prio += Math.pow(9,(lowerNeighbours + hostileNeighbours));
                hostileLowerNeighbours++;
            }else{
                return prio;
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
        }
        return prio;
    }

    private int getPrioFromHorizontal(int spotX, int spotY) {
        int prio = 0;
        int[] fromupperneighbours = getPrioFromUpperNeighboursHorizontal(spotX, spotY);
        prio += fromupperneighbours[0];
        prio += getPrioFromLowerNeighboursHorizontal(spotX, spotY,fromupperneighbours[1],fromupperneighbours[2]);
        return prio;
    }

    private int[] getPrioFromUpperNeighboursHorizontal(final int spotX, final int spotY) {
        int prio = 0;
        int friendlyNeighbours = 0;
        int hostileNeighbours = 0;
        for(int upperNeighbours = 1; upperNeighbours < 4; upperNeighbours++) {
            if (hostileNeighbours == 0 && !board.isOutsideBoard(spotX + upperNeighbours, spotY) && board.isAnyMarkerAt(spotX + upperNeighbours, spotY) && board.getMarker(spotX + upperNeighbours, spotY).getColor().equals(winningColor)) {
                prio += Math.pow(10,upperNeighbours);
                friendlyNeighbours++;
            } else if (friendlyNeighbours == 0 && !board.isOutsideBoard(spotX + upperNeighbours, spotY) && board.isAnyMarkerAt(spotX + upperNeighbours, spotY)) {
                prio += Math.pow(9,upperNeighbours);
                hostileNeighbours++;
            }else{
                return new int[]{prio,friendlyNeighbours,hostileNeighbours};
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
        }
        return new int[]{prio,friendlyNeighbours,hostileNeighbours};
    }

    private int getPrioFromLowerNeighboursHorizontal(final int spotX, final int spotY, int friendlyNeighbours, int hostileNeighbours) {
        int prio = 0;
        int friendlyLowerNeighbours = 0;
        int hostileLowerNeighbours = 0;
        for(int lowerNeighbours = 1; lowerNeighbours < 4; lowerNeighbours++) {
            if (hostileLowerNeighbours == 0 && !board.isOutsideBoard(spotX - lowerNeighbours, spotY) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY) && board.getMarker(spotX - lowerNeighbours, spotY).getColor().equals(winningColor)) {
                prio += Math.pow(10,(lowerNeighbours + friendlyNeighbours));
                friendlyLowerNeighbours++;
            } else if (friendlyLowerNeighbours == 0 && !board.isOutsideBoard(spotX - lowerNeighbours, spotY) && board.isAnyMarkerAt(spotX - lowerNeighbours, spotY)) {
                prio += Math.pow(9,(lowerNeighbours + hostileNeighbours));
                hostileLowerNeighbours++;
            }else{
                return prio;
            }
            // TODO: 9016-09-99 Maybe deprioritize markers close to edge
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
