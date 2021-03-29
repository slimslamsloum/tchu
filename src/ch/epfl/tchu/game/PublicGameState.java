package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublicGameState {

    //Attributes for a public game state: ticket count, a card state, the current player id, a map
    //with each player id associated to a player state, and the last player id
    private final int ticketCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Public Game State constructor
     * @param ticketsCount ticket count
     * @param cardState card state
     * @param currentPlayerId current player's id
     * @param playerState map with player id and player states
     * @param lastPlayer last player id
     * @throws IllegalArgumentException if ticket count is smaller than 0 or if map size is not 2
     * @throws NullPointerException if cardState or current player id is null
     */
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

    /**
     * Ticket count getter
     * @return ticket count
     */
    public int ticketsCount(){
        return ticketCount;
    }

    /**
     * Asks if player can draw a ticket
     * @return true if deck isn't empty, else returns false
     */
    public boolean canDrawTickets(){
        if (ticketCount == 0){
            return false;
        }
        else return true;
    }

    /**
     * Card State getter
     * @return card state
     */
    public PublicCardState cardState(){ return cardState; }

    /**
     * Asks if player can draw cards
     * @return true if discard size + deck size is bigger than 5 (i.e total size >10), else returns false
     */
    public boolean canDrawCards(){
        if (cardState.totalSize() >= 10){
            return true;
        }
        else return false;
    }

    /**
     * Current player ID getter
     * @return current player id
     */
    public PlayerId currentPlayerId(){ return currentPlayerId; }

    /**
     * Player state getter
     * @param playerId
     * @return player state of player given in argument
     */
    public PublicPlayerState playerState(PlayerId playerId){ return playerState.get(playerId); }

    /**
     * current player state getter
     * @return player state of current player
     */
    public PublicPlayerState currentPlayerState(){ return playerState.get(currentPlayerId); }

    /**
     * claimed routes getter
     * @return all claimed routes of both players
     */
    public List<Route> claimedRoutes(){
        List<Route> totalRoutes= new ArrayList<Route>();
        for (Route route: playerState.get(currentPlayerId).routes()){
            totalRoutes.add(route);
        }
        for (Route route: playerState.get(currentPlayerId.next()).routes()){
            totalRoutes.add(route);
        }
        return totalRoutes;
    }

    /**
     * Returns id of last player
     * @return null if last player id is unknown, else returns last player id
     */
    public PlayerId lastPlayer(){
        if (lastPlayer == null){ return null; }
        else return lastPlayer;
    }
}
