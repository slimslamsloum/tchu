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
import javafx.scene.control.ListCell;
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
import javafx.util.Callback;
import javafx.util.StringConverter;
import static javafx.application.Platform.isFxApplicationThread;
import static ch.epfl.tchu.gui.StringsFr.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Graphical player
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

//all public methods assert that they are called within the JavaFX thread, because it is imperative that only
//this thread controls the graphical interface

public class GraphicalPlayer {

    //We need 3 handlers that are in properties to handle the startTurn method
    private final SimpleObjectProperty<DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<DrawCardHandler> drawCardHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>();

    //observable list of texts that will be displayed in the InfoView
    private final ObservableList<Text> texts = FXCollections.observableList(new ArrayList<>());

    //Observable state of the game in an attribute
    private final ObservableGameState observableGameState;

    //main stage of the game
    private final Stage mainStage = new Stage();

    /**
     * Graphical player constructor
     * @param playerId playerId of the player watching the game
     * @param playerNames map of the player names associated to the playerIds
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames){
        assert isFxApplicationThread();

        observableGameState=new ObservableGameState(playerId);

        //creation of the view of the map, info, cards and hand
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTicketsHandler, drawCardHandler);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, observableGameState, texts);
        Node mapView = MapViewCreator.createMapView(observableGameState, claimRouteHandler, (cards,handler)->chooseClaimCards(handler,cards));
        Node handView = DecksViewCreator.createHandView(observableGameState);

        //creation of the borderpane, which will be associated with mainStage
        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        DarkModeButton.changeToDarkMode("darkMainPane.css",mainPane);
        mainPane.setId("mainPane");
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
        Text text = new Text(message);
        text.setId("text");
        texts.add(text);
    }

    /**
     * Method that handles the start of a player's turn
     * @param drawTicketsHandler handler for drawing tickets
     * @param drawCardHandler handler for drawing cards
     * @param claimRouteHandler handler for claiming a route
     */
    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler){
        assert isFxApplicationThread();

        if(observableGameState.canDrawTickets()){
            this.drawTicketsHandler.set(() -> {
                drawTicketsHandler.onDrawTickets();
                emptyHandlers();
            });
        }

        if (observableGameState.canDrawCards()){
            this.drawCardHandler.set( i -> {
                drawCardHandler.onDrawCard(i);
                emptyHandlers();
            } );
        }

        //independently of which handler is called, all handlers are then set to null with the method emptyHandlers
        this.claimRouteHandler.set((route,cards) ->{
            claimRouteHandler.onClaimRoute(route, cards);
            emptyHandlers();
        });
    }

    /**
     * Method that handles the tab when a player is choosing tickets
     * @param handler ticket handler
     * @param tickets all the tickets from which the player can choose from
     */
    public void chooseTickets(ChooseTicketsHandler handler, SortedBag<Ticket> tickets){
        assert isFxApplicationThread();

        //listView, button and intro Text are created
        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(tickets.toList()));
        Button choiceButton = new Button(CHOOSE);
        int ticketBagSize = tickets.size();
        String introText = String.format(CHOOSE_TICKETS, ticketBagSize-Constants.DISCARDABLE_TICKETS_COUNT,
                plural(ticketBagSize));

        //stage is created with auxiliary method choiceStage
        Stage stage = choiceStage(listView, TICKETS_CHOICE, introText, choiceButton);

        //selection mode becomes MULTIPLE
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //choiceButton is disabled while player hasn't selected more or exactly ticketBagSize-2 tickets,
        //where 2 is the maximum of tickets a player can discard
        choiceButton.disableProperty()
                .bind(Bindings.greaterThan(ticketBagSize-Constants.DISCARDABLE_TICKETS_COUNT,
                        Bindings.size(listView.getSelectionModel().getSelectedItems())));

        //tab can't be closed while picking tickets
        choiceButton.setOnAction(event -> {
            stage.hide();
            List<Ticket> ticketList = listView.getSelectionModel().getSelectedItems();
            SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
            ticketList.forEach(ticketBuilder::add);
            handler.onChooseTickets(ticketBuilder.build());
        });
        stage.setOnCloseRequest(Event::consume);
    }

    /**
     * Method that is called when the player is drawing a card for the second time
     * @param handler drawCard handler
     */
    public void drawCard(DrawCardHandler handler){
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
    public void chooseClaimCards(ChooseCardsHandler handler, List<SortedBag<Card>> possibleClaimCards){
        assert isFxApplicationThread();
        //listView, choiceButton and stage are created
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(possibleClaimCards));
        Button choiceButton = new Button(CHOOSE);
        //stage is created with auxiliary method choiceStage
        Stage stage = choiceStage(listView, CARDS_CHOICE, CHOOSE_CARDS, choiceButton);

        //button is disabled while player hasn't chosen a combination of cards
        choiceButton.disableProperty()
                .bind(Bindings.equal(0, Bindings.size(listView.getSelectionModel().getSelectedItems())));

        //tab can't be closed while picking cards
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
    public void chooseAdditionalCards(ChooseCardsHandler handler, List<SortedBag<Card>> additionalCards){
        assert isFxApplicationThread();

        //listView, button and stage are created
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(additionalCards));
        Button choiceButton = new Button(CHOOSE);
        //stage is created with choiceStage method
        Stage stage = choiceStage(listView, CARDS_CHOICE, CHOOSE_ADDITIONAL_CARDS, choiceButton);

        //tab can't be closed while picking cards
        stage.setOnCloseRequest(Event::consume);
        choiceButton.setOnAction(event -> {
            stage.hide();
            handler.onChooseCards(listView.getSelectionModel().getSelectedItem() == null
                    ? SortedBag.of() : listView.getSelectionModel().getSelectedItem());
        });
    }

    /**
     * method that constructs a tab when a player is drawing tickets, or claiming a route
     * @param listView View of the list of items (cards or tickets)
     * @param title title of the tab
     * @param intro intro text
     * @param choiceButton button of the tab
     * @return stage of the new tab
     */
    private <T> Stage choiceStage(ListView<T> listView, String title, String intro, Button choiceButton){

        //creation of stage, vbox. scene, textflow
        Stage stage = new Stage(StageStyle.UTILITY);
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        //intro text is added with the TextFlow
        Text text = new Text(intro);
        text.setId("text");
        TextFlow textFlow = new TextFlow(text);
        if(DecksViewCreator.darkModeButton.isSelected()){
            scene.getStylesheets().add("darkChooser.css");
        }

        listView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<T>(){
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-control-inner-background: " + "derive(-fx-base,80%)" + ";");
                        }
                        else{
                            setStyle("-fx-control-inner-background: " + "derive(-fx-base,80%)" + ";");
                            setText(item.toString());
                        }
                        if (DecksViewCreator.darkModeButton.isSelected()){
                            if (item == null || empty) {
                                setText(null);
                                setStyle("-fx-control-inner-background: " + "#312d38" + ";");
                            }
                            else{
                                setStyle("-fx-control-inner-background: " + "#312d38" + ";");
                                setText(item.toString());
                            }
                        }
                    }
                };
            };
        });

        //stage is given new title
        stage.setTitle(title);

        //vbox children are created, stage is then shown
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, choiceButton);
        stage.show();

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