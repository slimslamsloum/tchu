package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * State of the public attributes of a player
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class PublicPlayerState {

    //attributes needed for the public player state: number of tickets, number of cards, list of routes taken,
    //number of cars, and number of claim points
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     *PublicPlayerState constructor
     * @param ticketCount
     * @param cardCount
     * @param routes
     * @throws IllegalArgumentException if cardCount or ticketCount is negative
     * also computes within the constructor the car count the number of claim points
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(cardCount >= 0 && ticketCount >= 0);
        this.cardCount = cardCount;
        this.ticketCount = ticketCount;
        this.routes = routes;
        int routes_size=0;
        int temp =0;
        for (Route route: routes){
            routes_size += route.length();
            temp += route.claimPoints();
        }
        claimPoints = temp;
        carCount = 40 - routes_size;
    }

    /**
     * Ticket count getter
     * @return ticket count
     */
    public int ticketCount(){ return ticketCount; }

    /**
     * Card count getter
     * @return card count
     */
    public int cardCount(){ return cardCount; }

    /**
     * Routes getter
     * @return routes
     */
    public List<Route> routes(){ return routes; }

    /**
     * Car count getter
     * @return car count
     */
    public int carCount(){ return carCount; }

    /**
     * Claim points getter
     * @return claim points
     */
    public int claimPoints(){ return claimPoints; }
}