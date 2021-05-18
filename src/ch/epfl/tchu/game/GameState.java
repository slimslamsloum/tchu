package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * The state of a tChu game
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class GameState extends PublicGameState {

    //private attributes for GameState: map of private player states, tickets, and private elements of card state
    private final Map<PlayerId, PlayerState> privatePlayerState;
    private final Deck<Ticket> ticketDeck;
    private final CardState privateCardState;

    /**
     * Game State constructor
     *
     * @param ticketsCount     ticket count
     * @param ticketDeck       tickets in deck
     * @param privateCardState private card state
     * @param currentPlayerId  current player id
     * @param playerState      map of player states
     * @param lastPlayer       last player id
     */
    private GameState(int ticketsCount, Deck<Ticket> ticketDeck, CardState privateCardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, privateCardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.privatePlayerState = playerState;
        this.ticketDeck = ticketDeck;
        this.privateCardState = privateCardState;
    }

    /**
     * creates the initial state of the game
     * @param tickets all the tickets available in the game
     * @param rng     a random value
     * @return the beginning of the game
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        PlayerId currentPlayerId = PlayerId.PLAYER_2;
        if (rng.nextInt(2) < 1) {
            currentPlayerId = PlayerId.PLAYER_1;
        }
        Deck<Ticket> ticketsDeck = Deck.of(tickets, rng);
        Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);

        for (PlayerId playerId : PlayerId.ALL) {
            SortedBag<Card> playerCards = cardDeck.topCards(Constants.INITIAL_CARDS_COUNT);
            cardDeck = cardDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
            playerStateMap.put(playerId, PlayerState.initial(playerCards));
        }
        return new GameState(ticketsDeck.size(), ticketsDeck, CardState.of(cardDeck), currentPlayerId, playerStateMap, null);
    }

    /**
     * Method that returns he private state of a player
     * @param playerId the identity of a player
     * @return the private state of (= all the intel about) this player
     */
    public PlayerState playerState(PlayerId playerId) {
        return privatePlayerState.get(playerId);
    }

    /**
     * Method that returns the private state of the current
     * @return the private state of the player playing currently
     */
    public PlayerState currentPlayerState() {
        return privatePlayerState.get(currentPlayerId());
    }

    /**
     * Method that returns the "count" tickets at the top of the deck
     * @param count A number between 0 and the size of the deck
     * @return the "count" first tickets at the top of the ticket Deck
     * @throws IllegalArgumentException if the count is smaller than than 0 or bigger than the decks size
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketDeck.size());
        return ticketDeck.topCards(count);
    }

    /**
     * Method that returns the ticket deck without the "count" top Cards
     * @param count A number between 0 and the size of the deck
     * @return the ticket deck without the "count" top Cards
     *  @throws IllegalArgumentException if the count is smaller than than 0 or bigger than the decks size
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketDeck.size());
        return new GameState(ticketsCount() - count, ticketDeck.withoutTopCards(count), privateCardState, currentPlayerId(), privatePlayerState, lastPlayer());
    }

    /**
     * Method that returns the top card of the deck of cards
     * @return the top card of the deck of cards
     * @throws IllegalArgumentException if the deck of cards is empty
     */
    public Card topCard() {
        Preconditions.checkArgument(!privateCardState.isDeckEmpty());
        return privateCardState.topDeckCard();
    }

    /**
     * Method that returns the deck of cards without its top card
     * @return the deck of cards without its top card
     * @throws IllegalArgumentException if the deck of cards is empty
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!privateCardState.isDeckEmpty());
        return new GameState(ticketsCount(), ticketDeck, privateCardState.withoutTopDeckCard(), currentPlayerId(), privatePlayerState, lastPlayer());
    }

    /**
     * @param discardedCards the card that are to be put in the discard
     * @return the discard with the additional discarded cards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(ticketsCount(), ticketDeck, privateCardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), privatePlayerState, lastPlayer());
    }

    /**
     * @param rng A random number
     * @return return a new deck from the discard id the deck of cards is empty
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (privateCardState.isDeckEmpty()) {
            return new GameState(ticketsCount(), ticketDeck, privateCardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), privatePlayerState, lastPlayer());
        }
        return new GameState(ticketsCount(), ticketDeck, privateCardState, currentPlayerId(), privatePlayerState, lastPlayer());
    }

    /**
     * returns game state where a given player has taken the tickets given in argument
     *
     * @param playerId      player that takes tickets
     * @param chosenTickets tickets he chose
     * @return game state where the player takes the tickets he chose at the beginning
     * @throws IllegalArgumentException if tickets in player state is not empty
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(privatePlayerState.get(playerId).ticketCount() == 0);
        PlayerState withTickets = privatePlayerState.get(playerId).withAddedTickets(chosenTickets);
        Map<PlayerId, PlayerState> MapWithTickets = new EnumMap<>(privatePlayerState);
        MapWithTickets.put(playerId, withTickets);
        MapWithTickets.put(playerId.next(), privatePlayerState.get(playerId.next()));
        return new GameState(ticketsCount(), ticketDeck, privateCardState, currentPlayerId(), MapWithTickets, lastPlayer());
    }

    /**
     * returns game state where amongst the drawn tickets, player has taken the chosen tickets
     *
     * @param drawnTickets  drawn tickets from deck
     * @param chosenTickets tickets the player chose
     * @return game state where amongst the drawn tickets, player has taken the chosen tickets
     * @throws IllegalArgumentException if drawn tickets don't contain chosen tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        PlayerState withTickets = privatePlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets);
        Map<PlayerId, PlayerState> MapWithTickets = new EnumMap<>(PlayerId.class);
        MapWithTickets.put(currentPlayerId(), withTickets);
        MapWithTickets.put(currentPlayerId().next(), privatePlayerState.get(currentPlayerId().next()));
        return new GameState(ticketsCount() - drawnTickets.size(), ticketDeck.withoutTopCards(drawnTickets.size()), privateCardState,
                currentPlayerId(), MapWithTickets, lastPlayer());
    }

    /**
     * returns game state where current player has drawn face up card at index slot
     *
     * @param slot a face up card slot
     * @return game state where current player has drawn face up card at index slot
     * @throws IllegalArgumentException if player can draw cards
     */
    public GameState withDrawnFaceUpCard(int slot) {
        PlayerState withCard = privatePlayerState.get(currentPlayerId()).withAddedCard(cardState().faceUpCard(slot));
        Map<PlayerId, PlayerState> MapWithCard = new EnumMap<>(PlayerId.class);
        MapWithCard.put(currentPlayerId(), withCard);
        MapWithCard.put(currentPlayerId().next(), privatePlayerState.get(currentPlayerId().next()));
        CardState withoutCard = privateCardState.withDrawnFaceUpCard(slot);
        return new GameState(ticketsCount(), ticketDeck, withoutCard, currentPlayerId(), MapWithCard, lastPlayer());
    }

    /**
     * Method that returns game state where player has drawn top card of the deck
     *
     * @return game state where player has drawn top card of the deck
     * @throws IllegalArgumentException if player can't draw cards
     */
    public GameState withBlindlyDrawnCard() {
        PlayerState withCard = privatePlayerState.get(currentPlayerId()).withAddedCard(privateCardState.topDeckCard());
        CardState withoutCard = privateCardState.withoutTopDeckCard();
        Map<PlayerId, PlayerState> MapWithCard = new EnumMap<>(PlayerId.class);
        MapWithCard.put(currentPlayerId(), withCard);
        MapWithCard.put(currentPlayerId().next(), privatePlayerState.get(currentPlayerId().next()));
        return new GameState(ticketsCount(), ticketDeck, withoutCard, currentPlayerId(), MapWithCard, lastPlayer());
    }

    /**
     * Method the returns game state where current player has claimed route with a certain combination of cards
     *
     * @param route route claimed by player
     * @param cards cards used to claim route
     * @return game state where current player has claimed route with a certain combination of cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        PlayerState withRoute = privatePlayerState.get(currentPlayerId()).withClaimedRoute(route, cards);
        CardState withDiscard = privateCardState.withMoreDiscardedCards(cards);
        Map<PlayerId, PlayerState> MapWithRoute = new EnumMap<>(PlayerId.class);
        MapWithRoute.put(currentPlayerId(), withRoute);
        MapWithRoute.put(currentPlayerId().next(), privatePlayerState.get(currentPlayerId().next()));
        return new GameState(ticketsCount(), ticketDeck, withDiscard, currentPlayerId(), MapWithRoute, lastPlayer());
    }

    /**
     * Method that tells if last turn is starting or not
     * @return true if last turn is starting (i.e current player has 2 cars or less), else returns false
     */
    public boolean lastTurnBegins() {
        return privatePlayerState.get(currentPlayerId()).carCount() <= 2;
    }

    /**
     * Method that returns game state depending on if last turn is starting or not
     * @return game state where last player id = current player id if last turn begins, else return previous game state
     */
    public GameState forNextTurn() {
        if (lastTurnBegins()) {
            return new GameState(ticketsCount(), ticketDeck, privateCardState,
                    currentPlayerId().next(), privatePlayerState, currentPlayerId());
        }
        return new GameState(ticketsCount(), ticketDeck, privateCardState,
                currentPlayerId().next(), privatePlayerState, lastPlayer());
    }
}