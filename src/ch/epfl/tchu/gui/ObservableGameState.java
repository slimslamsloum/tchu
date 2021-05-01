package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObservableGameState {

    private final PlayerId ownPlayerId;
    private PublicGameState publicGameState;
    private PlayerState playerState;

    //group1
    private final SimpleIntegerProperty percentageTickets;
    private final SimpleIntegerProperty percentageCards;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, SimpleObjectProperty<PlayerId>> allRoutes;

    //group2
    private final Map<PlayerId, SimpleIntegerProperty> nbTickets;
    private final Map<PlayerId, SimpleIntegerProperty> nbCards;
    private final Map<PlayerId, SimpleIntegerProperty> nbCars;
    private final Map<PlayerId, SimpleIntegerProperty> nbPoints;

    //group3
    private final ObservableList<Ticket> playerTickets;
    private final Map<Card, SimpleIntegerProperty> numberPerCard;
    private final Map<Route, SimpleBooleanProperty> booleanForEachRoute;

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
            boolean bool = (alreadyClaimed(route, newPlayerState) &&
                    isCurrentPlayer(newGameState, ownPlayerId) && canClaim(newPlayerState, route));
            booleanForEachRoute.put(route, new SimpleBooleanProperty(bool));
        }
    }

    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> l = new ArrayList();
        for (int i =0; i<Constants.FACE_UP_CARDS_COUNT; i++){
            l.add(new SimpleObjectProperty<Card>());
        }
        return l;
    }

    private List<Route> doubleRoutes(Route route){
        for (Route route1: ChMap.routes()){
            if (route!=route1 && route.station1().equals(route1.station1())
                    && route.station2().equals(route1.station2())){
                return List.of(route, route1);
            }
        }
        return null;
    }

    private boolean isCurrentPlayer(PublicGameState gameState, PlayerId playerId){
        return gameState.currentPlayerId().equals(playerId);
    }

    private boolean alreadyClaimed(Route route, PlayerState playerState){
        if (!playerState.routes().contains(route)){
            if (isDouble(route)){
                if (!playerState.routes().contains(doubleRoutes(route).get(1))){
                    return true;
                }
                else return false;
            }
            return true;
        }
        return false;
    }

    private boolean canClaim(PlayerState playerState, Route route){
        return playerState.canClaimRoute(route);
    }

    private boolean isDouble(Route route){
        return !(doubleRoutes(route)==null);
    }

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    public ReadOnlyIntegerProperty percentageTickets(){return percentageTickets;}
    public ReadOnlyIntegerProperty percentageCards(){return percentageCards;}
    public ReadOnlyObjectProperty<PlayerId> routePlayerId(Route route){ return allRoutes.get(route);}

    public ReadOnlyIntegerProperty nbTickets(PlayerId playerid){
        return nbTickets.get(playerid);}
    public ReadOnlyIntegerProperty nbCards(PlayerId playerid){return nbCards.get(playerid);}
    public ReadOnlyIntegerProperty nbCars(PlayerId playerid){return nbCars.get(playerid);}
    public ReadOnlyIntegerProperty nbPoints(PlayerId playerid){return nbPoints.get((playerid));}

    public ObservableList<Ticket> playerTickets(){return FXCollections.unmodifiableObservableList(playerTickets);}

    public ReadOnlyIntegerProperty numberPerCard(Card card){return numberPerCard.get(card);}
    public ReadOnlyBooleanProperty booleanForEachRoute(Route route){
        return booleanForEachRoute.get(route);}

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
