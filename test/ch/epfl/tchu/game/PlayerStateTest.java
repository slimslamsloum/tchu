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
