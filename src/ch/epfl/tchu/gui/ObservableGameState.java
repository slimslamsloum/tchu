package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
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
    private final Map<Route, SimpleBooleanProperty> booleanForEachRoute;

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
        allRoutes=new SimpleMapProperty<Route, SimpleObjectProperty<PlayerId>>();

        //initialization of the properties that concern public information about both players
        nbTickets=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();
        nbCards=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();
        nbCars=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();
        nbPoints=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();

        //initialization of properties concerning information about the player with id "ownPlayerId"
        playerTickets=FXCollections.observableArrayList();
        numberPerCard=new SimpleMapProperty<Card, SimpleIntegerProperty>();
        booleanForEachRoute=new SimpleMapProperty<Route, SimpleBooleanProperty>();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        //setting new values for the ObservableGameState's PublicGameState and PlayerState attributes
        publicGameState=newGameState;
        playerState=newPlayerState;

        //setting new values for the first set of properties
        percentageTickets.set((newGameState.ticketsCount()/Constants.IN_GAME_TICKETS_COUNT)*100);
        percentageCards.set((newGameState.cardState().deckSize()/Constants.INITIAL_CARDS_COUNT)*100);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        for (Route route: ChMap.routes()) {
            if (newGameState.playerState(PlayerId.PLAYER_1).routes().contains(route)){
                allRoutes.put(route, new SimpleObjectProperty<PlayerId>(PlayerId.PLAYER_1));
            }
            if (newGameState.playerState(PlayerId.PLAYER_2).routes().contains(route)){
                allRoutes.put(route, new SimpleObjectProperty<PlayerId>(PlayerId.PLAYER_2));
            }
            else{
                allRoutes.put(route, null);
            }
        }

        //setting new values for the second set of properties
        for (PlayerId playerId: PlayerId.ALL){
            nbTickets.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).ticketCount()));
            nbCards.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).cardCount()));
            nbCars.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).carCount()));
            nbPoints.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).claimPoints()));
        }

        //setting new values for the third set of properties
        playerTickets.setAll(newPlayerState.tickets().toList());

        for (Card card : Card.ALL){
            numberPerCard.put(card, new SimpleIntegerProperty(newPlayerState.cards().countOf(card)));
        }
        for(Route route : ChMap.routes()){
            boolean bool = (notClaimed(route) &&
                    isCurrentPlayer(newGameState, ownPlayerId) && canClaim(newPlayerState, route));
            booleanForEachRoute.put(route, new SimpleBooleanProperty(bool));
        }
    }

    /**
     * Face Up Cards Creator
     * @return List of 5 Object Properties of Cards (one for each face up card)
     */
    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> l = new ArrayList();
        for (int i =0; i<Constants.FACE_UP_CARDS_COUNT; i++){
            l.add(new SimpleObjectProperty<Card>());
        }
        return l;
    }

    /**
     * Gives neighboring route of a route, returns null if there isn't any
     * @param route for which we want to find the neighboring route
     * @return neighboring route if there is one, else returns null
     */
    private List<Route> doubleRoutes(Route route){
        for (Route route1: ChMap.routes()){
            if (route!=route1 && route.station1().equals(route1.station1())
                    && route.station2().equals(route1.station2())){
                return List.of(route, route1);
            }
        }
        return null;
    }

    /**
     * Checks if playerId given in argument is the current player
     * @param gameState current game state
     * @param playerId a player Id
     * @return true iff playerId is the current player
     */
    private boolean isCurrentPlayer(PublicGameState gameState, PlayerId playerId){
        return gameState.currentPlayerId().equals(playerId);
    }

    /**
     * Checks, if a certain route has already been claimed or not
     * @param route route to be checked
     * @return true, if route is single and hasn't been claimed OR if route is double and the two routes haven't
     * been claimed, returns false in all other scenarios
     */
    private boolean notClaimed(Route route){
        if (allRoutes.get(route).equals(null)){
            if (isDouble(route)){
                return allRoutes.get(doubleRoutes(route)).equals(null);
            }
            else return true;
        }
        else return false;
    }

    /**
     * Checks if player of the player state given in argument can claim the route
     * @param playerState current player state
     * @param route route to be claimed
     * @return true iff player of the player state can claim the route
     */
    private boolean canClaim(PlayerState playerState, Route route){
        return playerState.canClaimRoute(route);
    }

    /**
     * Checks if route given in argument has a neighboring route
     * @param route route to be checked
     * @return true iff route has a neighboring route
     */
    private boolean isDouble(Route route){
        return !(doubleRoutes(route)==null);
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
    public ReadOnlyBooleanProperty booleanForEachRoute(Route route){ return booleanForEachRoute.get(route);}

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
     * Possible claim cards
     * @param route route to be used in possible claim cards
     * @return the method possible claim cards applied to the current player state with argument route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return playerState.possibleClaimCards(route);
    }
}
