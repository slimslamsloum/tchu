package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A train trip
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Trip {

    //attributes needed for a Trip object: a starting station, the ending station, and the number of points
    //given for the trip
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Trip constructor
     * @param from starting station
     * @param to ending station
     * @param points number of points given for the trip
     * @throws IllegalArgumentException
     * throws exception if the number of points is smaller or equal to 0
     */
    public Trip(Station from, Station to, int points){
        Preconditions.checkArgument(points>0);
        this.from=Objects.requireNonNull(from);;
        this.to=Objects.requireNonNull(to);
        this.points=points;
    }

    /**
     * Method that returns a List with all the trips from the starting stations to the ending stations
     * @param from List of a certain amount of starting stations
     * @param to List of a certain amount of ending stations
     * @param points Number of points given for each trip
     * @return a list with the trips connecting all the starting stations to all the ending stations
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        if (from == null || to == null || points <= 0){
            throw new IllegalArgumentException();
        }
        ArrayList allTrips = new ArrayList();
        for (Station station1 : from){
            for (Station station2 : to){
                Trip trip= new Trip(station1, station2, points);
                allTrips.add(trip);
            }
        }
        return allTrips;
    }

    //getters
    public Station from(){ return from; }
    public  Station to(){ return to;}
    public int points(){ return points;
    }
    /**
     * method that increases or decreases number of points depending on if 2 stations were connected or not
     * @param connectivity
     * @return the number of points if the stations are connected, else returns the number of points * -1
     */
    public int points(StationConnectivity connectivity){
        if (connectivity.connected(from, to)){
            return points;
        }
        else return -points;
    }
}
