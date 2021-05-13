package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class GraphicalPlayer {

    private final SimpleObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandler = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>();

    private ObservableList<Text> texts = FXCollections.observableList(new ArrayList<>());

    private ObservableGameState observableGameState;

    private Stage mainStage = new Stage();

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames){
        assert isFxApplicationThread();

        observableGameState=new ObservableGameState(playerId);

        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, observableGameState, texts);
        Node mapView = MapViewCreator.createMapView(observableGameState, claimRouteHandler, (cards,handler)->chooseClaimCards(handler,cards));
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTicketsHandler, drawCardHandler);
        Node handView = DecksViewCreator.createHandView(observableGameState);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        mainStage.setScene(new Scene(mainPane));
        mainStage.setTitle("tChu \u2014"+playerNames.get(playerId));
        mainStage.show();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        assert isFxApplicationThread();
        observableGameState.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String message){
        assert isFxApplicationThread();
        if (texts.size() == 5){ texts.remove(0); }
        texts.add(new Text(message));
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
        ObservableList<Ticket> listTickets = FXCollections.observableList(tickets.toList());
        ListView listView = new ListView (listTickets);

        Button button_1 = new Button();

        String s;
        if (tickets.size() == 1){ s=" "; }
        else s= "s";

        String introText = String.format(StringsFr.CHOOSE_TICKETS, tickets.size(), s );
        Text text = new Text(introText);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        textFlow.getChildren().add(text);
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, button_1);
        stage.show();

        ObservableList<Ticket> selectedItems = listView.getSelectionModel().getSelectedItems();

        button_1.disableProperty()
                .bind(Bindings.lessThan(Bindings.size(selectedItems), Constants.DISCARDABLE_TICKETS_COUNT));

    }

    public void drawCard(ActionHandlers.DrawCardHandler handler){
        assert isFxApplicationThread();

        this.drawCardHandler.set(i -> {
            handler.onDrawCard(i);
            emptyHandlers();
        });
    }

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
        ListView<SortedBag<Card>> listView = new ListView<SortedBag<Card>>(FXCollections.observableList(possibleClaimCards));
        Button button_2 = new Button();
        Text text = new Text(StringsFr.CHOOSE_CARDS);

        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        textFlow.getChildren().add(text);
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, button_2);
        stage.show();

        ObservableList<SortedBag<Card>> selectedItems = listView.getSelectionModel().getSelectedItems();

        button_2.disableProperty()
                .bind(Bindings.equal(0, Bindings.size(selectedItems)));

    }

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
        ListView<SortedBag<Card>> listView = new ListView<SortedBag<Card>>(FXCollections.observableList(additionalCards));
        Button button_3 = new Button();
        Text text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);

        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        textFlow.getChildren().add(text);
        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow, listView, button_3);
        stage.show();

    }

    private void emptyHandlers(){
        this.claimRouteHandler.set(null);
        this.drawTicketsHandler.set(null);
        this.drawCardHandler.set(null);
    }

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
