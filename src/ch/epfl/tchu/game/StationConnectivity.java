package ch.epfl.tchu.game;

/**
 * A connection between two stations
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public interface StationConnectivity {
    /**
     * Abstract method that will return boolean value depending on if the 2 stations are connected or not
     * @param s1 first station
     * @param s2 second station
     * @return true if both stations are connected, else returns false
     */
    public boolean connected (Station s1, Station s2);
}
