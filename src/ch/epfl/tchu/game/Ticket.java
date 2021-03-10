package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

/**
 * A train ticket
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Ticket implements Comparable<Ticket> {

    //Attributes needed for a ticket: the list of trips, a its textual representation
    private final List<Trip> trips;
    private final String text;

    /**
     * Primary ticket constructor
     * @param trips list of trips given as parameter for the ticket
     * @throws IllegalArgumentException
     * throws exception if trips is null or starting stations don't match
     */
    public Ticket(List<Trip> trips){
        Preconditions.checkArgument(!(trips == null || trips.size() == 0));
        String expectedStation = trips.get(0).from().toString();
        for(Trip trip : trips){
            String station = trip.from().toString();
            Preconditions.checkArgument(station.equals(expectedStation));
        }
        this.trips = trips;
        text=computeText();
    }

    /**
     * Secondary ticket constructor
     * @param from  starting stations
     * @param to ending stations
     * @param points points for ticket
     */
    public Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from,to,points)));
    }


    /**
     * @param that ticket that is being compared to
     * @return positive, negative or null integer depending on the comparison between the two tickets
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text);
    }

    /**
     * Creates the textual representation of the ticket
     * @return the textual representation of the ticket in String
     */
    private String computeText(){
        int destinations = trips.size();
        if(destinations == 1){
            String departure = trips.get(0).from().toString();
            String arrival = trips.get(0).to().toString();
            int points = trips.get(0).points();
            return String.format("%s - %s (%s)",departure, arrival, points);
        }
        else {
            TreeSet stations= new TreeSet();
            String departure = trips.get(0).from().toString();
            for (Trip trip : trips) {
                String arrival = trip.to().toString();
                int points = trip.points();
                arrival = arrival + " ("+ points + ")";
                stations.add(arrival);
            }
            String arrivals = String.join(", ", stations);
            return String.format("%s - {%s}",departure, arrivals);
        }
    }

    /**
     *
     * @param connectivity connectivity is a parameter to verify if two stations were connected or not
     * @return
     * if at least a trip was completed: returns the number of points from the most rewarding trip
     * else, returns the negative number of points from the least rewarding trip
     */
    public int points(StationConnectivity connectivity){
        int ticket_points=0;
        boolean isconnected=false;
        for (Trip single_trip : trips){
            if (connectivity.connected(single_trip.from(), single_trip.to()) && single_trip.points()>ticket_points){
                isconnected=true;
                ticket_points = single_trip.points();
            }
        }
        if(!isconnected){
            ticket_points = trips.get(0).points();
            for (Trip single_trip : trips){
                if (single_trip.points()<ticket_points){
                    ticket_points= single_trip.points();
                }
            }
            ticket_points=-ticket_points;
        }
        return ticket_points;
    }

    //getter
    public String text(){
        return text;
    }
}