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
        PlayerId currentPlayerId=PlayerId.PLAYER_2;
        if ( rng.nextInt(2)<1 ){currentPlayerId=PlayerId.PLAYER_1; }
        Deck<Ticket> ticketsDeck = Deck.of(tickets,rng);
        Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);

        for(PlayerId playerId : PlayerId.ALL ){
            SortedBag<Ticket> playerTickets = ticketsDeck.topCards(Constants.INITIAL_TICKETS_COUNT);
            ticketsDeck = ticketsDeck.withoutTopCards(Constants.INITIAL_TICKETS_COUNT);
            SortedBag<Card> playerCards = cardDeck.topCards(Constants.INITIAL_CARDS_COUNT);
            cardDeck = cardDeck.withoutTopCards(Constants.INITIAL_TICKETS_COUNT);
            PlayerState playerState= new PlayerState(playerTickets,playerCards,new ArrayList<>());
            playerStateMap.put(playerId, playerState);
        }
        return new GameState(ticketsDeck.size(),ticketsDeck,CardState.of(cardDeck),currentPlayerId,playerStateMap,currentPlayerId.next());
    }

    public PlayerState playerState(PlayerId playerId){
        return privatePlayerState.get(playerId);
    }

    public PlayerState currentPlayerState(){
        return privatePlayerState.get(currentPlayerId());
    }

    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count>=0 || count<=ticketDeck.size());
        return ticketDeck.topCards(count);
    }

    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count>=0 || count<=ticketDeck.size());
        return new GameState(ticketsCount(), ticketDeck.withoutTopCards(count), privateCardState,currentPlayerId(), privatePlayerState,lastPlayer());
    }

    public Card topCard(){
        Preconditions.checkArgument(!privateCardState.isDeckEmpty());
        return privateCardState.topDeckCard();
    }

    public GameState withoutTopCard(){
        Preconditions.checkArgument(!privateCardState.isDeckEmpty());
        return new GameState(ticketsCount(), ticketDeck, privateCardState.withoutTopDeckCard(),currentPlayerId(), privatePlayerState,lastPlayer());
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(ticketsCount(), ticketDeck, privateCardState.withMoreDiscardedCards(discardedCards),currentPlayerId(), privatePlayerState,lastPlayer());
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if(privateCardState.isDeckEmpty()){
            return new GameState(ticketsCount(), ticketDeck, privateCardState.withDeckRecreatedFromDiscards(rng),currentPlayerId(), privatePlayerState,lastPlayer());
        }
        return new GameState(ticketsCount(), ticketDeck, privateCardState,currentPlayerId(), privatePlayerState,lastPlayer());
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
