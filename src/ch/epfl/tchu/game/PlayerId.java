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

    //getters: methods that return list of constants and size of list
    public final static List<PlayerId> ALL = List.of(values());
    public final static int COUNT= ALL.size();

    /**
     *
     * @return the player different to the one on which this method was called
     */
    public PlayerId next(){
        if (this.equals(PLAYER_1)){
            return PLAYER_2;
        }
        else{
            return PLAYER_1;
        }
    }

}
