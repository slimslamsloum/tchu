package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Route
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Route {
    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    //different types of level for enum Level
    public enum Level{
        OVERGROUND, UNDERGROUND
    }

    /**
     * Route constructor
     * @param id id of the route
     * @param station1 first station of the route
     * @param station2 ending station of the route
     * @param length length of the route
     * @param level level of the route (overground or underground)
     * @param color color of the route
     * @throws IllegalArgumentException if the 2 stations are the same or route length isn't between 1 and 6
     * @throws NullPointerException if level, a station or route id is null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color){
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(!(length > Constants.MAX_ROUTE_LENGTH || length < Constants.MIN_ROUTE_LENGTH));
        Objects.requireNonNull(id);
        Objects.requireNonNull(level);
        Objects.requireNonNull(station2);
        this.id=id;
        this.color=color;
        this.length=length;
        this.level=level;
        this.station1=station1;
        this.station2=station2;
    }

    /**
     * Stations getter
     * @return station 1 and 2
     */
    public List<Station> stations(){
        return List.of(station1, station2);
    }

    /**
     * Computes opposite station to the one given in argument
     * @param station station given as argument
     * @return station opposite to station given as an argument i.e returns ending station if starting station
     * is given as argument and vice-versa
     */
    public Station stationOpposite(Station station){
        Preconditions.checkArgument(station.equals(station1)||station.equals(station2));
        if(station.equals(station1)){ return station2; }
        else{ return station1; }
    }

    /**
     * Method that calculates all the possible combinations of cards that a player can use to take a certain route
     * @return a List<SortedBag<Card>>, i.e a List composed of different SortedBag that each contains a possible
     * combination of cards that can be used
     * @throws IllegalArgumentException if the level is neither overground or underground
     */
    public List<SortedBag<Card>> possibleClaimCards(){
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        SortedBag.Builder<Card> arbitrary_SB = new SortedBag.Builder<>();
        Preconditions.checkArgument(level.equals(Level.OVERGROUND)||level.equals(Level.UNDERGROUND));
        // first we check if the level is Overground
        if (level.equals(Level.OVERGROUND)){
            if (color == null){
                // if the color is null, any card can be used to claim the road, except for Locomotives
                for (Card card: Card.CARS){
                    for (int i = 0; i < length; i ++){
                        arbitrary_SB.add(card);
                    }
                    possibleClaimCards.add(arbitrary_SB.build());
                    arbitrary_SB = new SortedBag.Builder<>();
                }
            }
            else{
                // if the color is defined, the road can only be take with the necessary number of cards of this color
                for (int i = 0; i < length; i ++){
                    arbitrary_SB.add(Card.of(color));
                }
                possibleClaimCards.add(arbitrary_SB.build());
            }
        }
        // if the level is not Overground, the it must be Underground
        else {
            if (color == null){
                // if the color is null, any card can be used to claim the road
                // we create a List with all the possible combinations, which are stored in SortedBags
                for (int k = length; k >0; k --){
                    for (Card card: Card.CARS){
                        for (int j=0; j<k; j++){
                            arbitrary_SB.add(card);
                        }
                        while(arbitrary_SB.size() < length) {
                            arbitrary_SB.add(Card.LOCOMOTIVE);
                        }
                        possibleClaimCards.add(arbitrary_SB.build());
                        arbitrary_SB = new SortedBag.Builder<>();
                    }
                }
                // a SortedBag of "length" locomotives are added at the end of the List, which is the last possible option
                for (int i = 0; i < length; i ++){
                    arbitrary_SB.add(Card.LOCOMOTIVE);
                }
            }
            else{
                // if the color is not null, we build a List with all the possible cards,
                //  which is composed of the cards of the defined color and locomotives
                for (int k = length; k >0; k --){
                    for (int j=0; j<k; j++){
                        arbitrary_SB.add(Card.of(color));
                    }
                    while(arbitrary_SB.size() < length){
                        arbitrary_SB.add(Card.LOCOMOTIVE);
                    }
                    possibleClaimCards.add(arbitrary_SB.build());
                    arbitrary_SB = new SortedBag.Builder<>();
                }
                for (int i = 0; i < length; i ++){
                    arbitrary_SB.add(Card.LOCOMOTIVE);
                }
            }
            possibleClaimCards.add(arbitrary_SB.build());
        }
        return possibleClaimCards;
    }


    /**
     * Method that calculates the number of additional cards a player has to discard when he tries to take
     * an underground route
     * @param claimCards cards used to claim a route
     * @param drawnCards cards that are drawn after claiming the route
     * @return number of cards the player will need to discard
     * @throws IllegalArgumentException if there are not exactly 3 cards drawn, or if the level isn't underground
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(!(drawnCards.size() != Constants.ADDITIONAL_TUNNEL_CARDS || level != Level.UNDERGROUND));
        int additionalClaimCardsCount=0;
        for (Card additional_card : drawnCards){
            SortedBag.Builder<Card> dC = new SortedBag.Builder<>();
            for (Card my_card : claimCards){
                if (!additional_card.equals(Card.LOCOMOTIVE)){
                    if(additional_card.equals(my_card)){
                        dC.add(my_card);
                        SortedBag<Card> usedCard = dC.build();
                        drawnCards= drawnCards.difference(usedCard);
                        additionalClaimCardsCount++;
                        break;
                    }
                }
                else{
                    dC.add(Card.LOCOMOTIVE);
                    SortedBag<Card> usedCard = dC.build();
                    drawnCards = drawnCards.difference(usedCard);
                    additionalClaimCardsCount++;
                    break;
                }
            }
        }
        return additionalClaimCardsCount;
    }

    /**
     * Returns claim points of route
     * @return a certain number of points that depends on the length of the route taken
     */
    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Id getter
     * @return id of Station
     */
    public String id(){ return id;}

    /**
     * Station 1 getter
     * @return Station 1
     */
    public Station station1(){return station1;}

    /**
     * Station 2 getter
     * @return Station 2
     */
    public Station station2(){return station2;}

    /**
     * Length getter
     * @return route length
     */
    public int length(){return length;}

    /**
     * Level getter
     * @return level (underground or overground)
     */
    public Level level(){return level;}

    /**
     * Color getter
     * @return color of route
     */
    public Color color(){return color;}
}