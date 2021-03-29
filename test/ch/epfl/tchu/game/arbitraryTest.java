package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class arbitraryTest {

    @Test
    void initial() {
        GameState initial = GameState.initial(SortedBag.of(new Ticket(new Station(1, "A"), new Station(2, "B"), 2)), new Random(1));
        assertEquals(1, initial.ticketsCount());
        assertNotEquals(Card.LOCOMOTIVE, initial.topCard());

        assertEquals(PlayerId.PLAYER_1, initial.currentPlayerId());
        assertEquals(Constants.INITIAL_CARDS_COUNT, initial.currentPlayerState().cards().size());
    }

    @Test
    void playerState() {
        GameState initial = GameState.initial(SortedBag.of(new Ticket(new Station(1, "A"), new Station(2, "B"), 2)), new Random(1));
        assertEquals(Constants.INITIAL_CAR_COUNT, initial.playerState(PlayerId.PLAYER_1).carCount());
        assertEquals(Constants.INITIAL_CAR_COUNT, initial.playerState(PlayerId.PLAYER_2).carCount());
    }

    @Test
    void currentPlayerState() {
        GameState initial = GameState.initial(SortedBag.of(new Ticket(new Station(1, "A"), new Station(2, "B"), 2)), new Random(1));
        assertEquals(initial.playerState(initial.currentPlayerId()), initial.currentPlayerState());
    }

    @Test
    void topTickets() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertTrue(initial.topTickets(1).contains(ticketB));
        assertFalse(initial.topTickets(1).contains(ticketA));
        assertTrue(initial.topTickets(2).contains(ticketA));
        assertTrue(initial.topTickets(2).contains(ticketB));
    }

    @Test
    void withoutTopTickets() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertEquals(0, initial.withoutTopTickets(2).ticketsCount());
        assertEquals(1, initial.withoutTopTickets(1).ticketsCount());
        assertEquals(2, initial.withoutTopTickets(0).ticketsCount());
    }

    @Test
    void topCard() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertEquals(Card.YELLOW, initial.topCard());
    }

    @Test
    void withoutTopCard() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertEquals(Card.YELLOW, initial.topCard());
        assertEquals(Card.LOCOMOTIVE, initial.withoutTopCard().topCard());
        while(initial.cardState().deckSize() != 0)
            initial = initial.withoutTopCard();
        assertThrows(IllegalArgumentException.class, initial::withoutTopCard);
    }

    @Test
    void withMoreDiscardedCards() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertEquals(15, initial.withMoreDiscardedCards(SortedBag.of(15, Card.LOCOMOTIVE)).cardState().discardsSize());
        assertEquals(0, initial.cardState().discardsSize());
    }

    @Test
    void withCardsDeckRecreatedIfNeeded() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        PublicCardState baseCardState = initial.cardState();
        assertEquals(initial.cardState(), initial.withCardsDeckRecreatedIfNeeded(new Random(1)).cardState());
        while(initial.cardState().deckSize() != 0)
            initial = initial.withoutTopCard();
        assertNotEquals(baseCardState, initial.withCardsDeckRecreatedIfNeeded(new Random()).cardState());
    }

    @Test
    void withInitiallyChosenTickets() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(), new Random(1));
        assertThrows(IllegalArgumentException.class, () -> initial.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(ticketA)).withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(ticketA)));
        assertThrows(IllegalArgumentException.class, () -> initial.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(ticketA)).withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(ticketA)));
        assertDoesNotThrow(() -> initial.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(ticketA)).withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(ticketA)));
        assertEquals(0, initial.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of()).playerState(PlayerId.PLAYER_2).ticketCount());
        assertEquals(1, initial.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(ticketA)).playerState(PlayerId.PLAYER_2).ticketCount());
        assertEquals(2, initial.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(2, ticketA)).playerState(PlayerId.PLAYER_2).ticketCount());
        assertEquals(0, initial.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of()).playerState(PlayerId.PLAYER_1).ticketCount());
        assertEquals(1, initial.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(ticketA)).playerState(PlayerId.PLAYER_1).ticketCount());
        assertEquals(2, initial.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(2, ticketA)).playerState(PlayerId.PLAYER_1).ticketCount());
    }

    @Test
    void withChosenAdditionalTickets() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertThrows(IllegalArgumentException.class, () -> initial.withChosenAdditionalTickets(SortedBag.of(ticketA), SortedBag.of(ticketB)));
        assertThrows(IllegalArgumentException.class, () -> initial.withChosenAdditionalTickets(SortedBag.of(ticketA), SortedBag.of(1, ticketA, 1, ticketB)));
        assertDoesNotThrow(() -> initial.withChosenAdditionalTickets(SortedBag.of(1, ticketA, 1, ticketB), SortedBag.of(ticketA)));
    }

    @Test
    void withDrawnFaceUpCard() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        Card newCard = initial.topCard();
        for (int i = 0; i < 5; i++) {
            assertEquals(newCard, initial.withDrawnFaceUpCard(i).cardState().faceUpCard(i));
            assertTrue(initial.withDrawnFaceUpCard(i).currentPlayerState().cards().difference(initial.currentPlayerState().cards()).contains(initial.cardState().faceUpCard(i)));
            assertEquals(1, initial.withDrawnFaceUpCard(i).currentPlayerState().cards().difference(initial.currentPlayerState().cards()).size());
        }
    }

    @Test
    void withBlindlyDrawnCard() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        SortedBag<Card> initialCards = initial.currentPlayerState().cards();

        assertEquals(1, initial.withBlindlyDrawnCard().currentPlayerState().cards().difference(initialCards).size());
        assertNotEquals(initial.topCard(), initial.withBlindlyDrawnCard().topCard());
        assertEquals(initial.topCard(), initial.withBlindlyDrawnCard().currentPlayerState().cards().difference(initialCards).get(0));
    }

    @Test
    void withClaimedRoute() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        Route routeToClaim = new Route("ab", new Station(2, "lol"), new Station(3, "lol"), 2, Route.Level.UNDERGROUND, null);
        SortedBag<Card> cards = SortedBag.of(2, Card.GREEN);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertEquals(1, initial.withClaimedRoute(routeToClaim, cards).currentPlayerState().routes().size());
        assertEquals(2, initial.withClaimedRoute(routeToClaim, cards).cardState().discardsSize());
        assertEquals(routeToClaim, initial.withClaimedRoute(routeToClaim, cards).currentPlayerState().routes().get(0));
        assertEquals(initial.currentPlayerState().cards().difference(cards), initial.withClaimedRoute(routeToClaim, cards).currentPlayerState().cards());
    }

    @Test
    void lastTurnBegins() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        Route routeToClaim = new Route("ab", new Station(2, "lol"), new Station(3, "lol"), 2, Route.Level.UNDERGROUND, null);
        SortedBag<Card> cards = SortedBag.of(2, Card.GREEN);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        for (int i = 0; i < 19; i++) {
            assertFalse(initial.lastTurnBegins());
            initial = initial.withClaimedRoute(routeToClaim, cards);
        }
        assertTrue(initial.lastTurnBegins());
    }

    @Test
    void forNextTurn() {
        Ticket ticketA = new Ticket(new Station(1, "A"), new Station(2, "B"), 2);
        Ticket ticketB = new Ticket(new Station(3, "C"), new Station(4, "D"), 2);
        Route routeToClaim = new Route("ab", new Station(2, "lol"), new Station(3, "lol"), 2, Route.Level.UNDERGROUND, null);
        SortedBag<Card> cards = SortedBag.of(2, Card.GREEN);
        GameState initial = GameState.initial(SortedBag.of(1, ticketA, 1, ticketB), new Random(1));
        assertEquals(PlayerId.PLAYER_2, initial.currentPlayerId());
        assertEquals(PlayerId.PLAYER_1, initial.forNextTurn().currentPlayerId());
        for (int i = 0; i < 19; i++) {
            assertNull(initial.forNextTurn().lastPlayer());
            initial = initial.withClaimedRoute(routeToClaim, cards);
        }
        assertEquals(initial.currentPlayerId(), initial.forNextTurn().lastPlayer());
    }
}
