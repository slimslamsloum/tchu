package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * Graphical player
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class GraphicalPlayer {

    //We need to 3 handlers that are in properties to handle the startTurn method
    private final SimpleObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>();

    //observable list of texts that will be displayed in the InfoView
    private ObservableList<Text> texts = FXCollections.observableList(new ArrayList<>());

    //Observable state of the game in an attribute
    private ObservableGameState observableGameState;

    //main stage of the game
    private Stage mainStage = new Stage();

    /**
     * Graphical player constructor
     * @param playerId playerId of the player watching the game
     * @param playerNames map of the player names associated to the playerIds
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames){
        assert isFxApplicationThread();

        observableGameState=new ObservableGameState(playerId);

        //creation of the view of the map, info, cards and hand
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, observableGameState, texts);
        Node mapView = MapViewCreator.createMapView(observableGameState, claimRouteHandler, (cards,handler)->chooseClaimCards(handler,cards));
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTicketsHandler, drawCardHandler);
        Node handView = DecksViewCreator.createHandView(observableGameState);

        //creation of the borderpane, which will be associated with mainStage
        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        mainStage.setScene(new Scene(mainPane));
        mainStage.setTitle("tChu \u2014"+playerNames.get(playerId));
        mainStage.show();
    }

    /**
     * Call to the method setStage in observableGameState
     * @param newGameState the new game state
     * @param newPlayerState the new player state
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        assert isFxApplicationThread();
        observableGameState.setState(newGameState, newPlayerState);
    }

    /**
     * Adds a message to the list of messages displayed on screen, with a maximum of 5 messages displayed.
     * @param message message to be added to the list
     */
    public void receiveInfo(String message){
        assert isFxApplicationThread();
        if (texts.size() == GuiConstants.MAX_MESSAGE_DISPLAYED){ texts.remove(0); }
        texts.add(new Text(message));
    }

    /**
     * Method that handles the start of a player's turn
     * @param drawTicketsHandler handler for drawing tickets
     * @param drawCardHandler handler for drawing cards
     * @param claimRouteHandler handler for claiming a route
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler,
                          ActionHandlers.DrawCardHandler drawCardHandler,
                          ActionHandlers.ClaimRouteHandler claimRouteHandler){
        assert isFxApplicationThread();

        //claimRouteHandler is always filled
        this.claimRouteHandler.set(claimRouteHandler);

        //drawCardHandler is filled only if player can draw cards
        if(observableGameState.canDrawCards()){ this.drawCardHandler.set(drawCardHandler); }
        else{ this.drawCardHandler.set(null); }

        //drawTicketsHandler is filled only if player can draw tickets
        if(observableGameState.canDrawTickets()){ this.drawTicketsHandler.set(drawTicketsHandler);}
        else{ this.drawTicketsHandler.set(null); }

        //independent of which handler is called, all handlers are then set to null with the method emptyHandlers
        this.claimRouteHandler.set((route,cards) ->{
            claimRouteHandler.onClaimRoute(route, cards);
            emptyHandlers();
         });

        this.drawTicketsHandler.set(() -> {
            drawTicketsHandler.onDrawTickets();
            emptyHandlers();
        });

        this.drawCardHandler.set( i -> {
            drawCardHandler.onDrawCard(i);
            emptyHandlers();
        } );
    }

    /**
     * Method that handles the tab when a player is choosing tickets
     * @param handler ticket handler
     * @param tickets all the tickets from which the player can choose from
     */
    public void chooseTickets(ActionHandlers.ChooseTicketsHandler handler, SortedBag<Ticket> tickets){
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(StringsFr.TICKETS_CHOICE);
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        TextFlow textFlow = new TextFlow();
        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(tickets.toList()));
        Button choiceButton = new Button();
        int ticketBagSize = tickets.size();
        String introText = String.format(StringsFr.CHOOSE_TICKETS, ticketBagSize, StringsFr.plural(ticketBagSize));
        Text text = new Text(introText);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        textFlow.getChildren().add(text);
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, choiceButton);
        stage.show();

        choiceButton.disableProperty()
                .bind(Bindings.equal(0, Bindings.size(listView.getSelectionModel().getSelectedItems())));

        choiceButton.setOnAction(event -> {
            stage.hide();
            List<Ticket> ticketList = listView.getSelectionModel().getSelectedItems();
            SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
            for (Ticket ticket : ticketList) {
                ticketBuilder.add(ticket);
            }
            handler.onChooseTickets(ticketBuilder.build());
        });
        stage.setOnCloseRequest(Event::consume);

    }

    /**
     * Method that is called when the player is drawing a card for the second time
     * @param handler drawCard handler
     */
    public void drawCard(ActionHandlers.DrawCardHandler handler){
        assert isFxApplicationThread();

        this.drawCardHandler.set(i -> {
            handler.onDrawCard(i);
            emptyHandlers();
        });
    }

    /**
     * Method that handles the tab when the player decides to choose the cards to claim a route
     * @param handler chooseCardsHandler
     * @param possibleClaimCards list of possible SortedBags of cards from which the player can choose from
     */
    public void chooseClaimCards(ActionHandlers.ChooseCardsHandler handler, List<SortedBag<Card>> possibleClaimCards){
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(StringsFr.CARDS_CHOICE);
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        TextFlow textFlow = new TextFlow();
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(possibleClaimCards));
        Button choiceButton = new Button();
        Text text = new Text(StringsFr.CHOOSE_CARDS);

        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        textFlow.getChildren().add(text);
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, choiceButton);
        stage.show();

        choiceButton.disableProperty()
                .bind(Bindings.equal(0, Bindings.size(listView.getSelectionModel().getSelectedItems())));

        stage.setOnCloseRequest(Event::consume);
        choiceButton.setOnAction(event -> {
            stage.hide();
            handler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });
    }

    /**
     * Method that handles the tab when a player chooses additional cards (when attempting to claim a tunnel)
     * @param handler chooseCardsHandler
     * @param additionalCards list of possible SortedBags of additional cards from which the player can choose from
     */
    public void chooseAdditionalCards(ActionHandlers.ChooseCardsHandler handler, List<SortedBag<Card>> additionalCards){
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(StringsFr.CARDS_CHOICE);
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        TextFlow textFlow = new TextFlow();
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(additionalCards));
        Button choiceButton = new Button();
        Text text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);

        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        textFlow.getChildren().add(text);
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, choiceButton);
        stage.show();

        choiceButton.disableProperty()
                .bind(Bindings.equal(0, Bindings.size(listView.getSelectionModel().getSelectedItems())));

        stage.setOnCloseRequest(Event::consume);
        choiceButton.setOnAction(event -> {
            stage.hide();
            handler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });
    }

    private <T extends Comparable<T>> Stage choiceStage(int i, List<SortedBag<T>> items){
        Stage stage = new Stage(StageStyle.UTILITY);
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        TextFlow textFlow = new TextFlow();
        ListView<SortedBag<T>> listView = new ListView<>(FXCollections.observableList(items));
        Button choiceButton = new Button();
        Text text = new Text();

        switch (i){
            case 1:
                stage.setTitle(StringsFr.TICKETS_CHOICE);
                break;

            case 2:
                stage.setTitle(StringsFr.CARDS_CHOICE);
                text = new Text(StringsFr.CHOOSE_CARDS);
                //listView.setCellFactory(v ->
                  //      new TextFieldListCell<>(new CardBagStringConverter()));
                break;

            case 3:
                stage.setTitle(StringsFr.CARDS_CHOICE);
                text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
                //listView.setCellFactory(v ->
                  //      new TextFieldListCell<>(new CardBagStringConverter()));
                break;
        }

        textFlow.getChildren().add(text);
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, choiceButton);
        stage.show();

        switch (i){
            case 1:

                break;

            case 2:

                break;

            case 3:

                break;
        }



        return stage;
    }

    /**
     * Private method that sets the 3 handlers to null
     */
    private void emptyHandlers(){
        this.claimRouteHandler.set(null);
        this.drawTicketsHandler.set(null);
        this.drawCardHandler.set(null);
    }

    /**
     * Nested static class to give textual representation of a sorted bag of cards
     */
    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        @Override
        public String toString(SortedBag<Card> object) {
            return Info.setContent(object);
        }

        @Override
        public SortedBag<Card> fromString(String string){
            throw new UnsupportedOperationException();
        }
    }

}
