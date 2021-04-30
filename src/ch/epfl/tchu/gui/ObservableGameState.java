package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ObservableGameState {

    private final PlayerId ownPlayerId;

    //group1
    private final SimpleIntegerProperty percentageTickets;
    private final SimpleIntegerProperty percentageCards;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final List<ObjectProperty<PlayerId>> allRoutes;

    //group2
    private final List<SimpleIntegerProperty> nbTickets;
    private final List<SimpleIntegerProperty> nbCards;
    private final List<SimpleIntegerProperty> nbCars;
    private final List<SimpleIntegerProperty> nbPoints;

    //group3
    private final ObservableList<Ticket> playerTickets;
    private final List<SimpleIntegerProperty> numberPerCard;
    private final List<SimpleBooleanProperty> booleanForEachRoute;

    public ObservableGameState(PlayerId playerId){
        ownPlayerId=playerId;

        percentageCards=null;
        percentageTickets=null;
        faceUpCards=createFaceUpCards();
        allRoutes=createRoutes();

        nbTickets=null;
        nbCards=null;
        nbCars=null;
        nbPoints=null;

        playerTickets=FXCollections.observableArrayList();
        numberPerCard=null;
        booleanForEachRoute=null;
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState){

        //group1
        percentageTickets.set((newGameState.ticketsCount()/Constants.IN_GAME_TICKETS_COUNT)*100);
        percentageCards.set((newGameState.cardState().deckSize()/Constants.INITIAL_CARDS_COUNT)*100);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        for (Route route: ChMap.routes()) {
            if (newGameState.playerState(PlayerId.PLAYER_1).routes().contains(route)){
                allRoutes.get(ChMap.routes().indexOf(route)).set(PlayerId.PLAYER_1);
            }
            if (newGameState.playerState(PlayerId.PLAYER_2).routes().contains(route)){
                allRoutes.get(ChMap.routes().indexOf(route)).set(PlayerId.PLAYER_2);
            }
            else{
                allRoutes.get(ChMap.routes().indexOf(route)).set(null);
            }

        }

        //group2
        for (PlayerId playerId: PlayerId.ALL){
            nbTickets.get(PlayerId.ALL.indexOf(playerId)).set(newGameState.playerState(playerId).ticketCount());
            nbCards.get(PlayerId.ALL.indexOf(playerId)).set(newGameState.playerState(playerId).cardCount());
            nbCars.get(PlayerId.ALL.indexOf(playerId)).set(newGameState.playerState(playerId).carCount());
            nbPoints.get(PlayerId.ALL.indexOf(playerId)).set(newGameState.playerState(playerId).claimPoints());
        }

        //group3
        for(Ticket ticket : newPlayerState.tickets()){
            playerTickets.add(ticket);
        }
        for (Card card : Card.ALL){
            int numberCards=newPlayerState.cards().countOf(card);
            numberPerCard.get(Card.ALL.indexOf(card)).set(numberCards);
        }
        for(Route route : ChMap.routes()){
            int index = ChMap.routes().indexOf(route);
            boolean bool = (alreadyClaimed(route, newPlayerState) &&
                    isCurrentPlayer(newGameState, ownPlayerId) && canClaim(newPlayerState, route));
            booleanForEachRoute.get(index).set(bool);
        }
    }

    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> l = new ArrayList();
        for (int i =0; i<Constants.FACE_UP_CARDS_COUNT; i++){
            l.add(new SimpleObjectProperty<Card>());
        }
        return l;
    }

    private List<ObjectProperty<PlayerId>> createRoutes(){
        List<ObjectProperty<PlayerId>> l = new ArrayList();
        for (int i =0; i<ChMap.routes().size(); i++){
            l.add(new SimpleObjectProperty<PlayerId>());
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

    public ReadOnlyObjectProperty<Card> faceUpCardPROP(int slot) {
        return faceUpCards.get(slot);
    }
    public ReadOnlyIntegerProperty percentageTicketsPROP(){return percentageTickets;}
    public ReadOnlyIntegerProperty percentageCardsPROP(){return percentageCards;}
    public ReadOnlyObjectProperty<PlayerId> playerIdPROP(int slot){ return allRoutes.get(slot);}

    public ReadOnlyIntegerProperty nbTicketsPROP(PlayerId playerid){
        return nbTickets.get(PlayerId.ALL.indexOf(playerid));}
    public ReadOnlyIntegerProperty nbCardsPROP(PlayerId playerid){return nbCards.get(PlayerId.ALL.indexOf(playerid));}
    public ReadOnlyIntegerProperty nbCarsPROP(PlayerId playerid){return nbCars.get(PlayerId.ALL.indexOf(playerid));}
    public ReadOnlyIntegerProperty nbPointsPROP(PlayerId playerid){return nbPoints.get(PlayerId.ALL.indexOf(playerid));}

    public ReadOnlyObjectProperty<List<Ticket>> playerTicketsPROP(){return playerTickets;}
    public ReadOnlyIntegerProperty numberPerCardPROP(Card card){return numberPerCard.get(Card.ALL.indexOf(card));}
    public ReadOnlyBooleanProperty booleanForEachRoutePROP(Route route){
        return booleanForEachRoute.get(ChMap.routes().indexOf(route));}
}
