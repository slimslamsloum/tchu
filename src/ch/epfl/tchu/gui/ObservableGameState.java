package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ObservableGameState {

    //properties de l'etat public de la partie

    private final SimpleIntegerProperty percentageTickets = null;
    private final SimpleIntegerProperty percentageCards = null;
    private final List<ObjectProperty<Card>> faceUpCards =
            createFaceUpCards();
    private final List<ObjectProperty<PlayerId>> allRoutes = createRoutes();

    //properties qui concerne l'etat public des deux joueurs

    private final List<SimpleIntegerProperty> nbTickets = null;
    private final List<SimpleIntegerProperty> nbCards = null;
    private final List<SimpleIntegerProperty> nbCars = null;
    private final List<SimpleIntegerProperty> nbPoints = null;

    //properties de l'etat complet du joueur

    private final ObservableList<Ticket> playerTickers = null;
    private final List<SimpleIntegerProperty> numberPerCard = null;
    private final List<SimpleBooleanProperty> booleanForEachRoute = null;

    public ObservableGameState(PlayerId playerId){

    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState){

        //properties de l'etat public de la partie

        percentageTickets.set((newGameState.ticketsCount()/Constants.IN_GAME_TICKETS_COUNT)*100);
        percentageCards.set((newGameState.cardState().deckSize()/Constants.INITIAL_CARDS_COUNT)*100);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        for (int i=0; i<ChMap.routes().size(); i++) {
            Route route = ChMap.routes().get(i);
            if (newGameState.playerState(PlayerId.PLAYER_1).routes().contains(route)){
                allRoutes.get(i).set(PlayerId.PLAYER_1);
            }
            if (newGameState.playerState(PlayerId.PLAYER_2).routes().contains(route)){
                allRoutes.get(i).set(PlayerId.PLAYER_2);
            }
            else{
                allRoutes.get(i).set(null);
            }

        }

        //properties qui concerne l'etat public des deux joueurs
        for (PlayerId playerId: PlayerId.ALL){
            int i;
            if(playerId==PlayerId.PLAYER_1){ i=0; }
            else {i=1;}
            nbTickets.get(i).set(newGameState.playerState(playerId).ticketCount());
            nbCards.get(i).set(newGameState.playerState(playerId).cardCount());
            nbCars.get(i).set(newGameState.playerState(playerId).carCount());
            nbPoints.get(i).set(newGameState.playerState(playerId).claimPoints());
        }

        //properties de l'etat complet du joueur



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

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }




}
