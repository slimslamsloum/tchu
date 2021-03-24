package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Private state of a player
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class PlayerState extends PublicPlayerState {
    //private attributes of a player: his tickets, cards, and routes, and we added carCount and claimPoints so that
    //these attributes are computed when constructing a Player State
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * Player State constructor
     * @param tickets the players tickets
     * @param cards the players cards
     * @param routes the players routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
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
     * Initiates a player state given some initial cards
     * @param initialCards the players cards at the beginning of the game
     * @return initial state of a player, where he doesn't have any tickets, routes, and has the cards given in
     * argument
     * @throws IllegalArgumentException if size of initial cards isn't 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument( initialCards.size()==4);
        SortedBag.Builder <Ticket> tickets = new SortedBag.Builder<>();
        List<Route> routes = new ArrayList<>();
        return new PlayerState(tickets.build(), initialCards, routes);
    }

    /**
     * Tickets getter
     * @return a player's tickets
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * Cards getter
     * @return a player's cards
     */
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
     * Method that adds tickets to a player state
     * @param newTickets tickets to be added to player state
     * @return identical player state, except that the tickets given as argument have been added to the previous list
     * of tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        List<Ticket> listWithTickets = tickets.toList();
        for (Ticket ticket : newTickets) {
            listWithTickets.add(ticket);
        }
        SortedBag.Builder<Ticket> new_Builder = new SortedBag.Builder<>();
        for (Ticket ticket : listWithTickets){
            new_Builder.add(ticket);
        }
        return new PlayerState(new_Builder.build(), cards, routes);
    }

    /**
     * Adds a card to a player state
     * @param card to be added to the list of cards
     * @return identical player state, except that the card that has been given as argument has been added to the
     * previous list of cards
     */
    public PlayerState withAddedCard(Card card){
        List<Card> listWithCard = cards.toList();
        listWithCard.add(card);
        SortedBag.Builder<Card> new_Builder = new SortedBag.Builder<>();
        for (Card single_card : listWithCard){
            new_Builder.add(single_card);
        }
        return new PlayerState(tickets, new_Builder.build(), routes);
    }

    /**
     * Adds several cards to a player state
     * @param additionalCards to be added to the list of cards
     * @return identical player state, except that the cards that have been given as argument have been added to the
     * previous list of cards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        List<Card> listWithCard = cards.toList();
        for (Card card: additionalCards){
            listWithCard.add(card);
        }
        SortedBag.Builder<Card> new_Builder = new SortedBag.Builder<>();
        for (Card single_card : listWithCard){
            new_Builder.add(single_card);
        }
        return new PlayerState(tickets, new_Builder.build(), routes);
    }

    /**
     * Method that verifies if a player can claim a given route or not
     * @param route road claimed by the player
     * @return true if the route can be claimed, false if it can't. A route can be claimed if the player has the necessary cars
     * and cards.
     */
    public boolean canClaimRoute(Route route){
        boolean canClaim = false;
        if (this.carCount() >= route.length() ){
            for (SortedBag<Card> SB : possibleClaimCards(route)){
                if (cards.contains(SB)){
                    canClaim = true;
                }
            }
        }
        return canClaim;
    }

    /**
     * Returns all the possible combinations of cards a player can use to claim a route
     * @param route that wants to be claimed by the player
     * @return all the possible combinations of cards a player can use to claim a route
     * @throws IllegalArgumentException the car count is smaller than the route length
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(this.carCount() >= route.length());
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        for (SortedBag<Card> SB : route.possibleClaimCards()){
            if (cards.contains(SB)){
                possibleClaimCards.add(SB);
            }
        }
        return possibleClaimCards;
    }

    /**
     * Method that gives the cards a player has to burn when trying to claim a tunnel
     * @param additionalCardsCount the number of cards the player has to add
     * @param initialCards the initial cards used to take the tunnel
     * @param drawnCards the cards drawn from the stock
     * @return all possible additional cards that the player has to add
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(additionalCardsCount>=1 && additionalCardsCount<=3 && drawnCards.size()==3
                && initialCards != null && initialCards.size() != 0);
        Preconditions.checkArgument(initialCards.toSet().size()<=2);

        SortedBag<Card> newCards = cards.difference(initialCards);

        for(Card card : drawnCards){
            for(Card initial_card : initialCards){
                SortedBag.Builder<Card> dC = new SortedBag.Builder<>();
                if (card.equals(initial_card) || card.equals(Card.LOCOMOTIVE)){
                    break;
                }
                dC.add(card);
                SortedBag<Card> uselessCard = dC.build();
                drawnCards = drawnCards.difference(uselessCard);
            }
        }
        SortedBag<Card> allUsableCards = allUsableCards(newCards,drawnCards);
        if(allUsableCards.size() >= additionalCardsCount){
            List<SortedBag<Card>> possibleAdditionalCards = new ArrayList<>(allUsableCards.subsetsOfSize(additionalCardsCount));
            possibleAdditionalCards.sort(
                    Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
            return possibleAdditionalCards;
        }
        else {
            return new ArrayList<>();
        }
    }


    /**
     *
     * @param deck the cards in possession of the payer
     * @param drawnCards the cards randomly drawn from the stock
     * @return all the cards that can be used by the player from his own deck of cards
     */
    private static SortedBag<Card> allUsableCards(SortedBag<Card> deck,SortedBag<Card> drawnCards){
        SortedBag.Builder<Card> temp = new SortedBag.Builder<>();
        for (Card my_card : deck) {
            for (Card additional_card : drawnCards){
                if(my_card.equals(additional_card) || my_card.equals(Card.LOCOMOTIVE)){
                    temp.add(my_card);
                    break;
                }
            }
        }
        return temp.build();
    }

    /**
     * Adds a claimed route to a player's player state and burns the cards he used to claim that route
     * @param route that has been claimed by the player
     * @param claimCards cards used to claim the route
     * @return new player state without the cards used to claim the route, but with the new route added to the list of
     * routes
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> routesWithClaimedRoute =new ArrayList<>(routes) ;
        routesWithClaimedRoute.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), routesWithClaimedRoute);
    }

    /**
     * Method that computes the points a player has won with his tickets
     * @return the number of total points won / lost with the players tickets
     */
    public int ticketPoints(){
        int stationCount=0;
        for(Route route : routes){
            int station1Id = route.station1().id();
            int station2Id = route.station2().id();
            if (station2Id > station1Id && station2Id > stationCount){
                stationCount = station2Id;
            }
            else if (station1Id > stationCount) {
                stationCount = station1Id;
            }
        }

        StationPartition.Builder partition = new StationPartition.Builder(stationCount+1);

        for (Route route : routes) {
            Station departure = route.station1();
            Station arrival = route.station2();
            partition.connect(departure,arrival);
        }
        StationConnectivity connectivity = partition.build();
        int totalPoints=0;
        for (Ticket ticket : tickets) {
            totalPoints+=ticket.points(connectivity);
        }
        return totalPoints;
    }

    /**
     * Method that gives the final number of points a player has
     * @return the final number of points the player has obtained
     */
    public int finalPoints(){
        return ticketPoints()+claimPoints();
    }
}