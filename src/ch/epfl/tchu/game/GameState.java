package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState {

    private final Map<PlayerId, PlayerState> privatePlayerState;
    private final Deck<Ticket> ticketDeck;
    private final CardState privateCardState;

    private GameState(int ticketsCount, Deck<Ticket> ticketDeck, CardState privateCardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, privateCardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.privatePlayerState=playerState;
        this.ticketDeck=ticketDeck;
        this.privateCardState=privateCardState;
    }

    public GameState initial(SortedBag<Ticket> tickets, Random rng) {

    }

    public PlayerState playerState(PlayerId playerId){
        return privatePlayerState.get(playerId);
    }

    public PlayerState currentPlayerState(){
        return privatePlayerState.get(currentPlayerId());
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
        Preconditions.checkArgument(privatePlayerState.get(playerId).tickets().isEmpty());
        PlayerState withTickets = privatePlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets);
        Map<PlayerId, PlayerState> MapWithTickets = Map.copyOf(privatePlayerState);
        MapWithTickets.replace(currentPlayerId(), withTickets);
        return new GameState(ticketsCount(), ticketDeck, privateCardState, currentPlayerId(), MapWithTickets, lastPlayer());
    }

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        PlayerState withTickets = privatePlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets);
        Map<PlayerId, PlayerState> MapWithTickets = Map.copyOf(privatePlayerState);
        MapWithTickets.replace(currentPlayerId(), withTickets);
        return new GameState(ticketsCount()-drawnTickets.size(), ticketDeck.withoutTopCards(drawnTickets.size()), privateCardState,
                currentPlayerId(), MapWithTickets, lastPlayer());
    }

    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(canDrawCards()==true);
        PlayerState withCard = privatePlayerState.get(currentPlayerId()).withAddedCard(cardState().faceUpCard(slot));
        Map<PlayerId, PlayerState> MapWithCard = Map.copyOf(privatePlayerState);
        MapWithCard.replace(currentPlayerId(),withCard);
        CardState withoutCard = privateCardState.withDrawnFaceUpCard(slot);
        return new GameState(ticketsCount(), ticketDeck, withoutCard, currentPlayerId(), MapWithCard, lastPlayer());
    }

    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(canDrawCards()==true);
        PlayerState withCard = privatePlayerState.get(currentPlayerId()).withAddedCard(privateCardState.topDeckCard());
        CardState withoutCard = privateCardState.withoutTopDeckCard();
        Map<PlayerId, PlayerState> MapWithCard = Map.copyOf(privatePlayerState);
        MapWithCard.replace(currentPlayerId(),withCard);
        return new GameState(ticketsCount(), ticketDeck, withoutCard, currentPlayerId(), MapWithCard, lastPlayer());
    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        PlayerState withCard = privatePlayerState.get(currentPlayerId()).withClaimedRoute(route,cards);
        Map<PlayerId, PlayerState> MapWithRoute = Map.copyOf(privatePlayerState);
        MapWithRoute.replace(currentPlayerId(), withCard);
        return new GameState(ticketsCount(), ticketDeck, privateCardState, currentPlayerId(), MapWithRoute, lastPlayer());
    }

    public boolean lastTurnBegins(){
        if (privatePlayerState.get(currentPlayerId()).carCount() <= 2){ return true; }
        else return false;
    }

    public GameState forNextTurn(){
        if (lastTurnBegins()){
            return  new GameState( ticketsCount(), ticketDeck, privateCardState,
                    currentPlayerId(), privatePlayerState, currentPlayerId());
        }
        return new GameState( ticketsCount(), ticketDeck, privateCardState,
                currentPlayerId().next(), privatePlayerState, lastPlayer());
    }
}
