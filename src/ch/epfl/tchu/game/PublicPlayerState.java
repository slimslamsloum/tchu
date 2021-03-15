package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

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

    public int ticketCount(){ return ticketCount; }
    public int cardCount(){ return cardCount; }
    public List<Route> routes(){ return routes; }
    public int carCount(){ return cardCount; }
    public int claimPoints(){ return claimPoints; }
}
