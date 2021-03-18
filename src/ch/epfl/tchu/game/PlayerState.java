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
    //private attributes of a player: his tickets, cards, and routes
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    /**
     * Player State constructor
     * @param tickets
     * @param cards
     * @param routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        this.routes = routes;
    }

    /**
     *
     * @param initialCards
     * @return initial state of a player, where he doesn't have any tickets, routes, and has the cards given in
     * argument
     * @throws IllegalArgumentException if size of initial cards isn't 4
     */
    public PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument( initialCards.size()==4);
        SortedBag.Builder <Ticket> tickets = new SortedBag.Builder<>();
        List<Route> routes = new ArrayList<>();
        return new PlayerState(tickets.build(), initialCards, routes);
    }

    //getters: returns player's tickets and player's cards
    public SortedBag<Ticket> tickets(){
        return tickets;
    }
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
     *
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
     *
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
     *
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
     *
     * @param route
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
     *
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
     *
     * @param additionalCardsCount the number of cards the player has to add
     * @param initialCards the initial cards used to take the tunnel
     * @param drawnCards the cards drawn from the stock
     * @return all possible additional cards that the player has to add
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(additionalCardsCount>=1 && additionalCardsCount<=3 && drawnCards.size()==3
                && initialCards != null);
        Preconditions.checkArgument(initialCards.toSet().size()<=2);

        SortedBag<Card> newCards = cards.difference(initialCards);
        for(Card card : drawnCards){
            SortedBag.Builder<Card> dC = new SortedBag.Builder<>();
            for(Card initial_card : initialCards){
                if (card.equals(initial_card) || card.equals(Card.LOCOMOTIVE)){
                    break;
                }
            }
            dC.add(card);
            SortedBag<Card> uselessCard = dC.build();
            drawnCards.difference(uselessCard);
        }
        SortedBag<Card> allUsableCards = allUsableCards(newCards,drawnCards);
        List<SortedBag<Card>> possibleAdditionalCards = new ArrayList<>(allUsableCards.subsetsOfSize(additionalCardsCount));
        possibleAdditionalCards.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return possibleAdditionalCards;
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
     *
     * @param route that has been claimed by the player
     * @param claimCards cards used to claim the route
     * @return new player state without the cards used to claim the route, but with the new route added to the list of
     * routes
     */
    PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> routesWithoutClaimedRoute =new ArrayList(routes);
        routesWithoutClaimedRoute.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), routesWithoutClaimedRoute);
    }

    /**
     *
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
                stationCount = station2Id;
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
     *
     * @return the final number of points the player has obtained
     */
    public int finalPoints(){
        return ticketPoints()+claimPoints();
    }
}