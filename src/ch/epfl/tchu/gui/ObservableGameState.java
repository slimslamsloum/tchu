package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observable state of the game
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class ObservableGameState {
    //attributes (non property) needed for an Observable Game State: the player id of the player
    //watching the game state, a public game state and a player state
    private final PlayerId ownPlayerId;
    private PublicGameState publicGameState;
    private PlayerState playerState;

    //properties that concern public information about the game state
    private final SimpleIntegerProperty percentageTickets;
    private final SimpleIntegerProperty percentageCards;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, SimpleObjectProperty<PlayerId>> allRoutes;

    //properties that concern public information about both players
    private final Map<PlayerId, SimpleIntegerProperty> nbTickets;
    private final Map<PlayerId, SimpleIntegerProperty> nbCards;
    private final Map<PlayerId, SimpleIntegerProperty> nbCars;
    private final Map<PlayerId, SimpleIntegerProperty> nbPoints;

    //properties that concern private information about the player watching the game state
    private final ObservableList<Ticket> playerTickets;
    private final Map<Card, SimpleIntegerProperty> numberPerCard;
    private final Map<Route, SimpleBooleanProperty> canClaimRoute;

    /**
     * Observable Game state constructor
     * @param playerId playerId of the player watching the game state
     * ownPlayerId is initialized with the playerId given as argument, all properties are initialized with default
     * constructor (i.e: are all null)
     */
    public ObservableGameState(PlayerId playerId){
        //initialization of ownPlayerId
        ownPlayerId=playerId;

        //initialization of properties concerning public information of the game state
        percentageCards=new SimpleIntegerProperty();
        percentageTickets=new SimpleIntegerProperty();
        faceUpCards=createFaceUpCards();
        allRoutes=routePropertyMap();

        //initialization of the properties that concern public information about both players
        nbTickets=PlayerIdIntegerPropertyMap();
        nbCards=PlayerIdIntegerPropertyMap();
        nbCars=PlayerIdIntegerPropertyMap();
        nbPoints=PlayerIdIntegerPropertyMap();

        //initialization of properties concerning information about the player with id "ownPlayerId"
        playerTickets=FXCollections.observableArrayList();
        numberPerCard=numberCardPropertyMap();
        canClaimRoute =booleanPropertyMap();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        //setting new values for the ObservableGameState's PublicGameState and PlayerState attributes
        publicGameState=newGameState;
        playerState=newPlayerState;

        //setting new values for the first set of properties
        percentageTickets.set((newGameState.ticketsCount()*100)/Constants.TOTAL_TICKET_COUNT);
        percentageCards.set((newGameState.cardState().deckSize()*100)/Constants.TOTAL_CARDS_COUNT);

        Constants.FACE_UP_CARD_SLOTS.forEach(slot -> faceUpCards.get(slot).set(newGameState.cardState().faceUpCard(slot)) );

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        for (Route route: ChMap.routes()) {
            for (PlayerId player : PlayerId.ALL){
                if (newGameState.playerState(player).routes().contains(route)){
                    allRoutes.get(route).set(player);
                }
            }
        }

        //setting new values for the second set of properties
        for (PlayerId playerId: PlayerId.ALL){
            nbTickets.get(playerId).set(newGameState.playerState(playerId).ticketCount());
            nbCards.get(playerId).set(newGameState.playerState(playerId).cardCount());
            nbCars.get(playerId).set((newGameState.playerState(playerId).carCount()));
            nbPoints.get(playerId).set(newGameState.playerState(playerId).claimPoints());
        }

        //setting new values for the third set of properties
        playerTickets.setAll(newPlayerState.tickets().toList());

        for (Card card : Card.ALL){
            numberPerCard.get(card).set(newPlayerState.cards().countOf(card));
        }
        for(Route route : ChMap.routes()){
            boolean bool = (!routeIsClaimed(route) &&
                    newGameState.currentPlayerId().equals(ownPlayerId) && newPlayerState.canClaimRoute(route));
            canClaimRoute.get(route).set(bool);
        }
    }

    /**
     * Gives neighboring route of a route, returns null if there isn't any
     * @param route for which we want to find the neighboring route
     * @return neighboring route if there is one, else returns null
     */
    private Route doubleRoute(Route route){
        //for all routes, if a double route is found, return the corresponding double route.
        for (Route route1: ChMap.routes()){
            if (route.stations().equals(route1.stations()) && !route.equals(route1)){ return route1;  }
        }
        return null;
    }

    /**
     * Checks, if a certain route has already been claimed or not
     * @param route route to be checked
     * @return true, if route is single and has been claimed OR if route is double and at least one of the two routes
     * have been claimed, returns false in all other scenarios
     */
    private boolean routeIsClaimed(Route route){
        Route doubleRoute = doubleRoute(route);
        boolean isClaimed = doubleRoute==null ? publicGameState.claimedRoutes().contains(route)
                : (publicGameState.claimedRoutes().contains(route)
                || publicGameState.claimedRoutes().contains(doubleRoute));
        return isClaimed;
    }

    /**
     * Face up card property getter
     * @param slot slot of face up card
     * @return read only property of face up card at index slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) { return faceUpCards.get(slot); }

    /**
     * Percentage of tickets property getter
     * @return read only property of the percentage of tickets the ticket deck
     */
    public ReadOnlyIntegerProperty percentageTickets(){return percentageTickets;}

    /**
     * Percentage of cards property getter
     * @return read only property of the percentage of cards in the card deck
     */
    public ReadOnlyIntegerProperty percentageCards(){return percentageCards;}

    /**
     * PlayerId associated to route property getter
     * @param route route to be checked
     * @return the playerId associated to the route as a read only property
     */
    public ReadOnlyObjectProperty<PlayerId> routePlayerId(Route route){ return allRoutes.get(route);}

    /**
     * Getter of the number of tickets a player has (as a read only property)
     * @param playerid playerId of the player
     * @return read only property of the number of tickets the player has
     */
    public ReadOnlyIntegerProperty nbTickets(PlayerId playerid){ return nbTickets.get(playerid);}

    /**
     * Getter of the number of cards a player has (as a read only property)
     * @param playerid playerId of the player
     * @return read only property of the number of cards the player has
     */
    public ReadOnlyIntegerProperty nbCards(PlayerId playerid){return nbCards.get(playerid);}

    /**
     * Getter of the number of cars a player has (as a read only property)
     * @param playerid playerId of the player
     * @return read only property of the number of cars the player has
     */
    public ReadOnlyIntegerProperty nbCars(PlayerId playerid){return nbCars.get(playerid);}

    /**
     * Getter of the number of points a player has (as a readable property)
     * @param playerid playerId of the player
     * @return read only property of the number of points the player has
     */
    public ReadOnlyIntegerProperty nbPoints(PlayerId playerid){return nbPoints.get((playerid));}

    /**
     * Getter of the list of tickets the player watching the game has
     * @return an observable list of the player's tickets
     */
    public ObservableList<Ticket> playerTickets(){return FXCollections.unmodifiableObservableList(playerTickets);}

    /**
     * Getter of the number of cards of a certain type the player watching the game has
     * @param card type of card
     * @return a read only property of the number of cards of the type given in argument
     */
    public ReadOnlyIntegerProperty numberPerCard(Card card){return numberPerCard.get(card);}

    /**
     * Getter of the boolean value associated to a route (for the player watching the game)
     * @param route route to be checked
     * @return read only property of the boolean value associated to the route
     */
    public ReadOnlyBooleanProperty canClaimRoute(Route route){ return canClaimRoute.get(route);}

    /**
     * canDrawTickets
     * @return the method canDrawTickets applied to the current public game state
     */
    public boolean canDrawTickets(){
        return publicGameState.canDrawTickets();
    }

    /**
     * canDrawCards
     * @return the method canDrawCards applied to the current public game state
     */
    public boolean canDrawCards(){
        return publicGameState.canDrawCards();
    }

    /**
     * Returns possible claim cards given a certain route, using player state
     * @param route route to be checked
     * @return possible claim cards to claim the route
     */
    public List<SortedBag<Card>> playerState(Route route){
        return playerState.possibleClaimCards(route);
    }

    /**
     * Face Up Cards Creator
     * @return List of 5 Object Properties of Cards (one for each face up card)
     */
    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> l = new ArrayList<>();
        for (int i =0; i<Constants.FACE_UP_CARDS_COUNT; i++){
            l.add(new SimpleObjectProperty<>());
        }
        return l;
    }

    /**
     * Creator of map of routes
     * @return creates a map with all routes and a simple object property associated to each route
     */
    private Map<Route, SimpleObjectProperty<PlayerId>> routePropertyMap(){
        Map<Route, SimpleObjectProperty<PlayerId>> map = new HashMap<>();
        for (Route route : ChMap.routes()){
            map.put(route, new SimpleObjectProperty<>());
        }
        return map;
    }

    /**
     * Creator of map of playerIds
     * @return creates a map with both player Ids and a simple object property associated to each playerId
     */
    private Map<PlayerId, SimpleIntegerProperty> PlayerIdIntegerPropertyMap(){
        Map<PlayerId, SimpleIntegerProperty> map = new HashMap<>();
        for (PlayerId player: PlayerId.ALL){
            map.put(player, new SimpleIntegerProperty());
        }
        return map;
    }

    /**
     * Creator of map of cards
     * @return creates a map with all cards and a simple object property associated to each card
     */
    private Map<Card, SimpleIntegerProperty> numberCardPropertyMap(){
        Map<Card, SimpleIntegerProperty> map = new HashMap<>();
        for (Card card : Card.ALL){
            map.put(card, new SimpleIntegerProperty());
        }
        return map;
    }

    /**
     * Creator of map of playerIds
     * @return creates a map with all routes and a simple boolean property associated to each route
     */
    private Map<Route, SimpleBooleanProperty> booleanPropertyMap(){
        Map<Route, SimpleBooleanProperty> map = new HashMap<>();
        for (Route route : ChMap.routes()){
            map.put(route, new SimpleBooleanProperty(false));
        }
        return map;
    }
}