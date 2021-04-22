package ch.epfl.tchu.net;

/**
 * Possible messages between server and client
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public enum MessageId {

    /**
     * Different possible messages that could be used between the server and the client
     */

    INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS, CHOOSE_INITIAL_TICKETS, NEXT_TURN,
    CHOOSE_TICKETS, DRAW_SLOT, ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS

}
