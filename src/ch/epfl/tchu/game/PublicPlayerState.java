package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public final class PublicPlayerState {

    private int ticketCount;
    private int cardCount;
    private List<Route> routes;
    private int carCount;
    private int claimPoints;

    PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(cardCount >= 0 && ticketCount >= 0);
        this.cardCount = cardCount;
        this.ticketCount = ticketCount;
        this.routes = routes;
        int routes_size=0;
        claimPoints=0;
        for (Route route: routes){
            routes_size += route.length();
            claimPoints += route.claimPoints();
        }
        carCount = 40 - routes_size;
    }

    public int ticketCount(){
        return ticketCount;
    }
    public int cardCount(){
        return cardCount;
    }
    public List<Route> routes(){
        return routes;
    }
    public int carCount(){
        return cardCount;
    }
    public int claimPoints(){
        return claimPoints;
    }
}
