package ch.epfl.tchu.game;

import java.util.List;

/**
 * Players
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public enum PlayerId {

    //different constants for enum PlayerId: player 1 and player 2
    PLAYER_1, PLAYER_2;

    /**
     * List of different players
     */
    public final static List<PlayerId> ALL = List.of(values());

    /**
     * Number of different players
     */
    public final static int COUNT= ALL.size();

    /**
     * Method that returns the next player's id
     * @return the player different to the one on which this method was called
     */
    public PlayerId next(){
        if (this.equals(PLAYER_1)){ return PLAYER_2; }
        else{ return PLAYER_1; }
    }

}
