package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

    public static PlayerState PS_generator(int ticketcount, int routecount){
        SortedBag.Builder ticketSB = new SortedBag.Builder();
        for (int i = 0; i < ticketcount; i++){
            ticketSB.add(ChMap.tickets().get(i));
        }
        SortedBag<Ticket> tickets = ticketSB.build();

        SortedBag.Builder cardsSB = new SortedBag.Builder();
        for (Card card: Card.ALL){
            cardsSB.add(card);
        }
        SortedBag<Card> cards = cardsSB.build();
        List<Route> routes = ChMap.routes().subList(0,routecount);

        return new PlayerState(tickets,cards,routes);
    }

    @Test
    void WithAddedTicketWorks(){
        PlayerState PS = PS_generator(7,10);
        SortedBag.Builder ticketSB = new SortedBag.Builder();
        for (int i = 7; i < 11; i++){
            ticketSB.add(ChMap.tickets().get(i));
        }
        SortedBag<Ticket> tickets = ticketSB.build();
        PS=PS.withAddedTickets(tickets);
        assert(PS.tickets().contains(tickets));
    }

    @Test
    void WithAddedCardWorks(){
        PlayerState PS = PS_generator(7,10);
        for (int i = 0; i< 4; i++){
            PS=PS.withAddedCard(Card.BLUE);
        }
        assertEquals(5, PS.cards().countOf(Card.BLUE));
    }

    @Test
    void WithAddedCardsWorks(){
        PlayerState PS = PS_generator(7,10);
        SortedBag.Builder<Card> cards_to_add = new SortedBag.Builder<>();
        cards_to_add.add(Card.ORANGE);
        cards_to_add.add(Card.ORANGE);
        cards_to_add.add(Card.ORANGE);
        cards_to_add.add(Card.BLACK);
        cards_to_add.add(Card.LOCOMOTIVE);
        cards_to_add.add(Card.LOCOMOTIVE);
        cards_to_add.add(Card.ORANGE);
        PS=PS.withAddedCards(cards_to_add.build());
        assertEquals(5, PS.cards().countOf(Card.ORANGE));
        assertEquals(2, PS.cards().countOf(Card.BLACK));
        assertEquals(3, PS.cards().countOf(Card.LOCOMOTIVE));
    }

    @Test
    void canClaimRouteWorks(){
        PlayerState PS = PS_generator(7,10);
        SortedBag.Builder<Card> cards_to_add = new SortedBag.Builder<>();
        cards_to_add.add(Card.RED);
        cards_to_add.add(Card.RED);
        cards_to_add.add(Card.RED);
        cards_to_add.add(Card.RED);
        cards_to_add.add(Card.LOCOMOTIVE);
        cards_to_add.add(Card.LOCOMOTIVE);
        cards_to_add.add(Card.LOCOMOTIVE);
        PS=PS.withAddedCards(cards_to_add.build());
        assert(PS.canClaimRoute(ChMap.routes().get(2)));
        assert(PS.canClaimRoute(ChMap.routes().get(0)));
        assertFalse(PS.canClaimRoute(ChMap.routes().get(3)));
    }

    @Test
    void withClaimedRouteWorks(){
        PlayerState PS = PS_generator(7,10);
        PS=PS.withClaimedRoute(ChMap.routes().get(10), SortedBag.of(Card.YELLOW));
        assert(PS.routes().contains(ChMap.routes().get(10)));
        int yellowcount = 0;
        assertEquals(0, PS.cards().countOf(Card.YELLOW));
    }

    @Test
    void PossibleClaimCardsWorks(){
        PlayerState PS = PS_generator(7,10);
        assert(PS.possibleClaimCards(ChMap.routes().get(1)).contains(SortedBag.of(Card.RED)));
    }

}
