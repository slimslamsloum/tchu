package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;

import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;

public class GraphicalPlayer {

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

    public void startTurn(ActionHandlers.ChooseTicketsHandler chooseTicketsHandler,
                          ActionHandlers.DrawCardHandler drawCardHandler,
                          ActionHandlers.ChooseCardsHandler chooseCardsHandler){
        assert isFxApplicationThread();

    }

    public void chooseTickets(ActionHandlers.ChooseTicketsHandler handler){
        assert isFxApplicationThread();

    }

    public void drawCard(ActionHandlers.DrawCardHandler handler){
        assert isFxApplicationThread();

    }

    public void chooseClaimCards(ActionHandlers.ChooseCardsHandler handler){
        assert isFxApplicationThread();

    }

}
