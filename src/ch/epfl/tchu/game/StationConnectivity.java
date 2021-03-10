package ch.epfl.tchu.game;

/**
 * A connection between two stations
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public interface StationConnectivity {
    public abstract boolean connected (Station s1, Station s2);
}
