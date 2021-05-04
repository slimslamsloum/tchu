package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Different action handlers
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public interface ActionHandlers {
    @FunctionalInterface
    interface DrawTicketsHandler{
        void onDrawTickets();
    }
    @FunctionalInterface
    interface DrawCardHandler{
        void onDrawCard(int slot);
    }
    @FunctionalInterface
    interface ClaimRouteHandler{
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }
    @FunctionalInterface
    interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> tickets);
    }
    @FunctionalInterface
    interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cards);
    }
}