package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class arbitraryTest {
    final SortedBag<Card> testCards = SortedBag.of(Card.ALL.subList(0,4));
    final Station[] stations = new Station[]{ new Station(0, "a"),
            new Station(1, "b"),
            new Station(2, "c"),
            new Station(3, "d"),
            new Station(4, "e")};

    @Test
    void initial() {
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(SortedBag.of(Card.ALL)));
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(SortedBag.of()));
        assertDoesNotThrow(() -> PlayerState.initial(SortedBag.of(Card.ALL.subList(0,4))));
    }

    @Test
    void tickets() {
        assertEquals(0, PlayerState.initial(testCards).tickets().size());
        assertEquals(1, new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 2, Route.Level.OVERGROUND, null))).tickets().size());
        assertEquals(4, new PlayerState(SortedBag.of(4, new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 2, Route.Level.OVERGROUND, null))).tickets().size());
    }

    @Test
    void withAddedTickets() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 2, Route.Level.OVERGROUND, null)));
        assertEquals(1, ps.tickets().size());
        assertEquals(2, ps.withAddedTickets(SortedBag.of(new Ticket(stations[0], stations[1], 2))).tickets().size());
        assertEquals(3, ps.withAddedTickets(SortedBag.of(2, new Ticket(stations[0], stations[1], 2))).tickets().size());
    }

    @Test
    void withAddedCard() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 2, Route.Level.OVERGROUND, null)));
        assertEquals(4, ps.cards().size());
        assertEquals(5, ps.withAddedCard(Card.BLACK).cards().size());

    }

    @Test
    void withAddedCards() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 2, Route.Level.OVERGROUND, null)));
        assertEquals(8, ps.withAddedCards(testCards).cards().size());
    }

    @Test
    void canClaimRoute() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        PlayerState almostFullps = ps.withClaimedRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null), SortedBag.of());
        assertTrue(almostFullps.withAddedCards(SortedBag.of(5, Card.RED)).canClaimRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, Color.RED)));
        assertTrue(almostFullps.withAddedCards(SortedBag.of(5, Card.RED)).canClaimRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        assertFalse(almostFullps.withAddedCards(SortedBag.of(2, Card.RED)).canClaimRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        assertFalse(almostFullps.canClaimRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, Color.RED)));
        for(int i = 0; i < 34; i+= 5) {
            almostFullps = almostFullps.withClaimedRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null), SortedBag.of());
        }
        PlayerState finalAlmostFullps = almostFullps;
        assertFalse(finalAlmostFullps.withAddedCards(SortedBag.of(5, Card.RED)).canClaimRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        assertFalse(ps.withAddedCards(SortedBag.of(5, Card.LOCOMOTIVE)).canClaimRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, Color.RED)));
        assertTrue(ps.withAddedCards(SortedBag.of(5, Card.LOCOMOTIVE)).canClaimRoute(new Route("r", stations[0], stations[1], 5, Route.Level.UNDERGROUND, Color.RED)));
    }

    @Test
    void possibleClaimCards() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        assertEquals(1, ps.withAddedCards(SortedBag.of(5, Card.LOCOMOTIVE)).possibleClaimCards(new Route("r", stations[0], stations[1], 5, Route.Level.UNDERGROUND, Color.RED)).size());
        assertEquals(6, ps.withAddedCards(SortedBag.of(5, Card.LOCOMOTIVE)).withAddedCards(SortedBag.of(5, Card.RED)).possibleClaimCards(new Route("r", stations[0], stations[1], 5, Route.Level.UNDERGROUND, Color.RED)).size());
    }

    @Test
    void possibleAdditionalCards() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), SortedBag.of(), List.of(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        SortedBag looser = SortedBag.of(3, Card.GREEN);
        PlayerState psWithAddedCards = ps.withAddedCards(SortedBag.of(2, Card.LOCOMOTIVE, 8, Card.GREEN));
        PlayerState psWithLocos = ps.withAddedCards(SortedBag.of(8, Card.LOCOMOTIVE));
        PlayerState anotherPs = ps.withAddedCards(SortedBag.of(8, Card.LOCOMOTIVE, 1, Card.GREEN));

        assertEquals(1, psWithLocos.possibleAdditionalCards(3, SortedBag.of(5, Card.LOCOMOTIVE), looser).size()); //
        assertEquals(0, psWithLocos.possibleAdditionalCards(3, SortedBag.of(7, Card.LOCOMOTIVE), looser).size()); // pas assez de locomotives, zut
        assertEquals(0, psWithLocos.possibleAdditionalCards(2, SortedBag.of(7, Card.LOCOMOTIVE), looser).size()); // pas assez de locomotives, zut
        assertEquals(1, psWithLocos.possibleAdditionalCards(1, SortedBag.of(7, Card.LOCOMOTIVE), looser).size()); // a 8-7 locomotives, il peut faire 1 choix
        assertEquals(0, anotherPs.possibleAdditionalCards(1, SortedBag.of(8, Card.LOCOMOTIVE), looser).size()); // doit payer en locomotives

        assertEquals(4, psWithAddedCards.withAddedCard(Card.LOCOMOTIVE).possibleAdditionalCards(3, SortedBag.of(4, Card.GREEN), looser).size());
        assertEquals(3, psWithAddedCards.possibleAdditionalCards(3, SortedBag.of(4, Card.GREEN), looser).size());
        assertEquals(3, psWithAddedCards.possibleAdditionalCards(2, SortedBag.of(4, Card.GREEN), looser).size());
        assertEquals(1, psWithAddedCards.possibleAdditionalCards(2, SortedBag.of(8, Card.GREEN), looser).size());
        assertEquals(0, psWithAddedCards.possibleAdditionalCards(2, SortedBag.of(8, Card.GREEN, 2, Card.LOCOMOTIVE), looser).size());
    }

    @Test
    void withClaimedRoute() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, List.of(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null)));
        assertEquals(0, ps.withClaimedRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null), testCards).cards().size());
        assertEquals(2, ps.withClaimedRoute(new Route("r", stations[0], stations[1], 5, Route.Level.OVERGROUND, null), testCards).routes().size());
    }

    @Test
    void ticketPoints() {
        PlayerState ps = new PlayerState(SortedBag.of(new Ticket(stations[0], stations[1], 2)), testCards, Collections.emptyList());
        assertEquals(-2, ps.ticketPoints());
        assertEquals(-6, new PlayerState(SortedBag.of(3, new Ticket(stations[0], stations[1], 2)), testCards, Collections.emptyList()).ticketPoints());
        assertEquals(6, new PlayerState(SortedBag.of(3, new Ticket(stations[0], stations[1], 2)), testCards, Collections.emptyList()).withClaimedRoute(new Route("a", stations[0], stations[1], 2, Route.Level.UNDERGROUND, null), SortedBag.of()).ticketPoints());
        assertEquals(3, new PlayerState(SortedBag.of(3, new Ticket(stations[0], stations[1], 2), 1, new Ticket(stations[2], stations[3], 3)), testCards, Collections.emptyList()).withClaimedRoute(new Route("a", stations[0], stations[1], 2, Route.Level.UNDERGROUND, null), SortedBag.of()).ticketPoints());
    }
}
