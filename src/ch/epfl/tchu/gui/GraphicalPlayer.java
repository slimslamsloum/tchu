package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;

public class GraphicalPlayer {

    private final SimpleObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>();

    private ObservableGameState observableGameState;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames){
        assert isFxApplicationThread();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        assert isFxApplicationThread();
        observableGameState.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String message){
        assert isFxApplicationThread();
    }

    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler,
                          ActionHandlers.DrawCardHandler drawCardHandler,
                          ActionHandlers.ClaimRouteHandler claimRouteHandler){
        assert isFxApplicationThread();

        this.claimRouteHandler.set(claimRouteHandler);

        if(observableGameState.canDrawCards()){ this.drawCardHandler.set(drawCardHandler); }
        else{ this.drawCardHandler.set(null); }

        if(observableGameState.canDrawTickets()){ this.drawTicketsHandler.set(drawTicketsHandler);}
        else{ this.drawTicketsHandler.set(null); }

        this.claimRouteHandler.set((cards,route) ->{
            claimRouteHandler.onClaimRoute(cards,route);
            emptyHandlers();
         });

        this.drawTicketsHandler.set(() -> {
            emptyHandlers();
        });

        this.drawCardHandler.set( i -> {
            drawCardHandler.onDrawCard(i);
            emptyHandlers();
        } );
    }

    public void chooseTickets(ActionHandlers.ChooseTicketsHandler handler, SortedBag<Ticket> tickets){
        assert isFxApplicationThread();

    }

    public void drawCard(ActionHandlers.DrawCardHandler handler){
        assert isFxApplicationThread();

    }

    public void chooseClaimCards(ActionHandlers.ChooseCardsHandler handler, List<SortedBag<Card>> possibleClaimCards){
        assert isFxApplicationThread();

    }

    public void chooseAdditionalCards(ActionHandlers.ChooseCardsHandler handler, List<SortedBag<Card>> additionalCards){
        assert isFxApplicationThread();
    }

    private void emptyHandlers(){
        this.claimRouteHandler.set(null);
        this.drawTicketsHandler.set(null);
        this.drawCardHandler.set(null);
    }

}
