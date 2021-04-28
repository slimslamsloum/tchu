package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * A player
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public interface Player {

    enum TurnKind {
        //constants for enum TurnKind
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

        /**
         * return all values of TurnKind
         */
        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }

    //Below are a set of undefined methods (that will still be described) used in the class Game

    /**
     *Communicates to a player his own Id as well as the other player's Id
     * @param ownId id of player on which method is called
     * @param playerNames map of Player Ids linked to player names
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     *Displays on a given player's screen the String info
     * @param info info communicated to the player
     */
    void receiveInfo(String info);

    /**
     *Updates the state of a given player
     * @param newState new gamestate
     * @param ownState player's player state
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Shows to the player at the start of the game the 5 tickets from which he will have to chose
     * @param tickets 5 tickets distributed at the beginning of the game to a player
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Asks the player which tickets he wants to keep amongst the 5 tickets
     * that were distributed in setInitialTicketChoice
     * @return SortedBag of tickets the player has chosen
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     *Asks what the player wants to do at the beginning of his turn
     * @return what kind of action the player wants to perform at the beginning of his turn
     */
    TurnKind nextTurn();

    /**
     * Returns slot of card player has chosen when drawing a card
     * @return chosen slot of the card (between 0 and 4 for face up card, -1 for deck)
     */
    int drawSlot();

    /**
     * Returns route player is attempting to claim
     * @return route player tries to claim
     */
    Route claimedRoute();

    /**
     *Returns tickets player chose to keep amongst tickets "options"
     * @param options tickets the player has drawn from the ticket pile
     * @return tickets the player chose to keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Returns initial cards player has used to attempt to claim a route
     * @return cards player chooses to attempt to claim the route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Retunrs additional cards the player burns when attempting to claim a tunnel. If returns an empty SortedBag,
     * the player doesn't have the valid cards to claim the tunnel
     * @param options possible SortedBags that can be used to claim the tunnel
     * @return cards the player will burn to claim the tunnel. If SortedBag is empty, this means the player doesn't have
     * the valid cards to claim the tunnel
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}