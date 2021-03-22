package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Map;

public class PublicGameState {
    private final int ticketCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;


    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);
        if (cardState == null || currentPlayerId == null){
            throw new NullPointerException("current Player Id or cardstate is null");
        }
        this.ticketCount =ticketsCount;
        this.cardState=cardState;
        this.currentPlayerId=currentPlayerId;
        this.playerState=playerState;
        this.lastPlayer=lastPlayer;
    }

    public int ticketsCount(){
        return ticketCount;
    }

    public boolean canDrawTickets(){
        if (cardState.isDeckEmpty()){
            return false;
        }
        else return true;
    }

    public PublicCardState cardState(){
        return cardState;
    }

    public boolean canDrawCards(){
        if (cardState.totalSize() >= 10){
            return true;
        }
        else return false;
    }

    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }

    public PublicPlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    public PublicPlayerState currentPlayerState(){
        return playerState.get(currentPlayerId);
    }

    public List<Route> claimedRoutes(){
        return playerState.get(currentPlayerId).routes();
    }

    public PlayerId lastPlayer(){
        if (lastPlayer == null){
            return null;
        }
        else return lastPlayer;
    }
}
