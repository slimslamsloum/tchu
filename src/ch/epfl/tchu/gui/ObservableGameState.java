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
        ownPlayerId=playerId;

        percentageCards=new SimpleIntegerProperty();
        percentageTickets=new SimpleIntegerProperty();
        faceUpCards=createFaceUpCards();
        allRoutes=new SimpleMapProperty<Route, SimpleObjectProperty<PlayerId>>();

        nbTickets=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();
        nbCards=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();
        nbCars=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();
        nbPoints=new SimpleMapProperty<PlayerId, SimpleIntegerProperty>();

        playerTickets=FXCollections.observableArrayList();
        numberPerCard=new SimpleMapProperty<Card, SimpleIntegerProperty>();
        booleanForEachRoute=new SimpleMapProperty<Route, SimpleBooleanProperty>();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        publicGameState=newGameState;
        playerState=newPlayerState;

        //group1
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

        //group2
        for (PlayerId playerId: PlayerId.ALL){
            nbTickets.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).ticketCount()));
            nbCards.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).cardCount()));
            nbCars.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).carCount()));
            nbPoints.put(playerId, new SimpleIntegerProperty(newGameState.playerState(playerId).claimPoints()));
        }

        //group3
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

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) { return faceUpCards.get(slot); }
    public ReadOnlyIntegerProperty percentageTickets(){return percentageTickets;}
    public ReadOnlyIntegerProperty percentageCards(){return percentageCards;}
    public ReadOnlyObjectProperty<PlayerId> routePlayerId(Route route){ return allRoutes.get(route);}

    public ReadOnlyIntegerProperty nbTickets(PlayerId playerid){ return nbTickets.get(playerid);}
    public ReadOnlyIntegerProperty nbCards(PlayerId playerid){return nbCards.get(playerid);}
    public ReadOnlyIntegerProperty nbCars(PlayerId playerid){return nbCars.get(playerid);}
    public ReadOnlyIntegerProperty nbPoints(PlayerId playerid){return nbPoints.get((playerid));}

    public ObservableList<Ticket> playerTickets(){return FXCollections.unmodifiableObservableList(playerTickets);}

    public ReadOnlyIntegerProperty numberPerCard(Card card){return numberPerCard.get(card);}
    public ReadOnlyBooleanProperty booleanForEachRoute(Route route){ return booleanForEachRoute.get(route);}

    public boolean canDrawTickets(){
        return publicGameState.canDrawTickets();
    }
    public boolean canDrawCards(){
        return publicGameState.canDrawCards();
    }
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return playerState.possibleClaimCards(route);
    }
}
