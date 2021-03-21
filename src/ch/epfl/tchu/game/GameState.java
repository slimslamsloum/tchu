package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class GameState extends PublicGameState {

    private GameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
    }

    public GameState initial(SortedBag<Ticket> tickets, Random rng) {

    }

    public PlayerState playerState(PlayerId playerId){

    }

    public PlayerState currentPlayerState(){

    }

    public SortedBag<Ticket> topTickets(int count){

    }

    public GameState withoutTopTickets(int count){

    }

    public Card topCard(){

    }

    public GameState withoutTopCard(){

    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){

    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng){

    }

    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){

    }

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){

    }

    public GameState withDrawnFaceUpCard(int slot){

    }

    public GameState withBlindlyDrawnCard(){

    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){

    }

    public boolean lastTurnBegins(){}

    public GameState forNextTurn(){}


}
