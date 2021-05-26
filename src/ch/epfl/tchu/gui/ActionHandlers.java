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
        /**
         * Method is called when player wants to draw tickets
         */
        void onDrawTickets();
    }

    @FunctionalInterface
    interface DrawCardHandler{
        /**
         * Method is called when the player wants to draw a card
         * @param slot slot of the card the player wants to draw
         */
        void onDrawCard(int slot);
    }
    @FunctionalInterface
    interface ClaimRouteHandler{
        /**
         * Method is called when the player wants to claim a route
         * @param route route the player wants to claim
         * @param cards cards the player uses to claim the route
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }
    @FunctionalInterface
    interface ChooseTicketsHandler{
        /**
         * Method is called when player wants to draw tickets
         * @param tickets tickets the player has kept
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }
    @FunctionalInterface
    interface ChooseCardsHandler{
        /**
         * Method is called when player is claiming a route
         * @param cards cards used as initial claim cards or additional claim cards when claiming a route
         */
        void onChooseCards(SortedBag<Card> cards);
    }
}