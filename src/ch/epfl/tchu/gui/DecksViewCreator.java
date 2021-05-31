package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static ch.epfl.tchu.gui.GuiConstants.*;

/**
 * Creation of the visual and playable interface of the games deck
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

class DecksViewCreator {
    /**
     * private constructor
     */
    private DecksViewCreator() {
    }

    //Dark mode button
    public static ToggleButton darkModeButton;

    /**
     * Method that creates the player's hand view
     * @param obsGameState a game observer
     * @return the view of the player's hand
     */
    public static HBox createHandView(ObservableGameState obsGameState){
        //Creation of the visual representation of the hand
        HBox handView = new HBox();
        handView.getStylesheets().addAll("decks.css", "colors.css");

        //Creation of the view of all player's tickets
        ListView<Ticket> ticketListView = new ListView<>(obsGameState.playerTickets());
        ticketListView.setId("tickets");
        handView.getChildren().add(ticketListView);

        ticketListView.setCellFactory(new Callback<ListView<Ticket>, ListCell<Ticket>>() {
            @Override
            public ListCell<Ticket> call(ListView<Ticket> param) {
                return new ListCell<Ticket>(){
                    protected void updateItem(Ticket item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-control-inner-background: " + "derive(-fx-base,80%)" + ";");
                        }
                        else if (obsGameState.ticketPoints(item).greaterThan(0).get()){
                            setStyle("-fx-control-inner-background: " + "green" + ";");
                            setText(item.toString());
                        }
                        else if (darkModeButton.isSelected()){
                            setStyle("-fx-control-inner-background: " + "black" + ";");
                            setText(item.toString());
                        }
                        else {
                            setStyle("-fx-control-inner-background: " + "derive(-fx-base,80%)" + ";");
                            setText(item.toString());
                            darkModeButton.selectedProperty().addListener((obs1, wasSelected, isSelected)->{
                                if (item == null || empty) {
                                    setText(null);
                                }
                                else if (isSelected && !obsGameState.ticketPoints(item).greaterThan(0).get()){
                                    setStyle("-fx-control-inner-background: " + "black" + ";");
                                    setText(item.toString());
                                }
                                else if(!isSelected && !obsGameState.ticketPoints(item).greaterThan(0).get()){
                                    setStyle("-fx-control-inner-background: " + "derive(-fx-base,80%)" + ";");
                                    setText(item.toString());
                                }
                            });

                            obsGameState.ticketPoints(item).addListener((obs2, oldVal, newVal)-> {
                                if (newVal.intValue() > 0) {
                                    setStyle("-fx-control-inner-background: " + "green" + ";");
                                    setText(item.toString());
                                } else if (!darkModeButton.isSelected()) {
                                    setStyle("-fx-control-inner-background: " + "derive(-fx-base,80%)" + ";");
                                    setText(item.toString());
                                }
                            });
                        }
                    }
                };
            };
        });

        //Creation of the view of all player's cards
        HBox cardsHBox = new HBox();
        cardsHBox.setId("hand-pane");

        //iteration on all the cards of the game
        for (Card card : Card.ALL) {
            //Creates the view corresponding to the card and adds it to the player's hand
            cardsHBox.getChildren().add(handCardView(card,obsGameState));
        }
        //the view of the player's cards is now added as a children of the player's global hand view
        handView.getChildren().add(cardsHBox);

        DarkModeButton.changeToDarkMode("darkDecks.css", handView);

        return handView;
    }

    /**
     * Method that creates the deck's view
     * @param obsGameState a game observer
     * @param drawTicketsHP a property containing the action handler if a ticket is drawn
     * @param drawCardHP a property containing the action handler if a card is drawn
     * @return the view of the game's deck
     */
    public static VBox createCardsView(ObservableGameState obsGameState, ObjectProperty<DrawTicketsHandler> drawTicketsHP,
                                       ObjectProperty<DrawCardHandler> drawCardHP){
        // Creation of the global view of the deck
        VBox deckView = new VBox();
        deckView.getStylesheets().addAll("decks.css", "colors.css");
        deckView.setId("card-pane");

        //Creation of the button to draw tickets from the card's stock
        Button ticketDeckButton = buttonView(StringsFr.TICKETS,obsGameState.percentageTickets());

        //Creation of help button
        Button helpButton = new Button("Aide");

        //Creation of Dark Mode button
        darkModeButton = new ToggleButton("Dark Mode");

        deckView.getChildren().add(darkModeButton);

        //The Button is then added as a component of the Deck's view
        deckView.getChildren().add(ticketDeckButton);

        //iteration on all the slots from which cards facing up can be drawn
        for(int slot : Constants.FACE_UP_CARD_SLOTS){

            //Creates the view of the corresponding faceUpCard and adds it to the deck
            deckView.getChildren().add(faceUpCardView(slot,obsGameState,drawCardHP));
        }

        //Creation of the button to draw cards from the card's stock
        Button cardDeckButton = buttonView(StringsFr.CARDS,obsGameState.percentageCards());

        //Then the Button is added as a component of the Deck's view
        deckView.getChildren().add(cardDeckButton);

        deckView.getChildren().add(helpButton);

        //Handles the distribution of cards / tickets in case the player does press on one of the button
        cardDeckButton.setOnMouseClicked(event -> drawCardHP.get().onDrawCard(Constants.DECK_SLOT));
        ticketDeckButton.setOnMouseClicked(event -> drawTicketsHP.get().onDrawTickets());

        VBox vboxHelp = new VBox();
        DarkModeButton.changeToDarkMode("darkRules.css", vboxHelp);
        vboxHelp.setId("background");
        helpButton.setOnMouseClicked(event -> displayHelpStage(vboxHelp));


        //In case the player doesn't want to draw a card or a ticket, the use of the buttons is disabled
        cardDeckButton.disableProperty().bind(drawCardHP.isNull());
        ticketDeckButton.disableProperty().bind(drawTicketsHP.isNull());

        DarkModeButton.changeToDarkMode("darkDecks.css", deckView);

        return deckView;
    }

    /**
     * Method that creates the view of a card in the player's hand
     * @param card the type of the card
     * @param observableGameState the observable GameState, contains the number of cards of the player
     * @return The view of the card
     */
    private static StackPane handCardView(Card card, ObservableGameState observableGameState){

        //creates a new basic view of a card
        StackPane handCardView = cardView();

        //Creates the property of a player containing the number of cards of a single type
        //he has in his possession
        ReadOnlyIntegerProperty countPpt = observableGameState.numberPerCard(card);

        //verifying that only a number of cards bigger than 0 can be seen on the deck
        handCardView.visibleProperty().bind(Bindings.greaterThan(countPpt, 0));

        // Applies the right visual representation of the card according to its color
        String color = card.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : card.color().name();
        handCardView.getStyleClass().addAll(color,"card");

        //Creation of the text that represents the number of cards of a single type
        //the player has in his possession
        Text countText = new Text();
        countText.getStyleClass().add("count");
        countText.textProperty().bind(Bindings.convert(countPpt));
        //verifying that the number of cards of a certain type can be seen only if there is more than one card
        countText.visibleProperty().bind(Bindings.greaterThan(countPpt, 1));

        //the shapes and the text are defined as children of the view of the card
        handCardView.getChildren().add(countText);

        return handCardView;
    }

    private static StackPane faceUpCardView(int slot, ObservableGameState observableGameState,
                                            ObjectProperty<DrawCardHandler> drawCardHP){
        //Creates a new basic view of a card
        StackPane faceUpCardView = cardView();

        faceUpCardView.getStyleClass().addAll("", "card");
        //Adds the new card on the deck if ever a card is drawn by a player
        //And applies the right visual representation of the card according to its color
        observableGameState.faceUpCard(slot).addListener((prop,oldVal,newVal) ->
                faceUpCardView.getStyleClass().set(0, newVal.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : newVal.color().name() ));

        //If the player doesn't want to draw cards, the use of the faceUpCard is disabled
        faceUpCardView.disableProperty().bind(drawCardHP.isNull());

        //Calls the action handler in case the players clicks on a card with his mouse
        faceUpCardView.setOnMouseClicked(event -> drawCardHP.get().onDrawCard(slot));

        return faceUpCardView;
    }

    /**
     * Method that creates the shape of a card
     * @return The visual shape of the card
     */
    private static StackPane cardView(){

        StackPane cardShape = new StackPane();

        //Creation of all the shapes that make up the card's visual representation
        Rectangle outsideRectangle = new Rectangle(DVC_OUTSIDE_LENGTH,DVC_OUTSIDE_HEIGHT);
        outsideRectangle.getStyleClass().add("outside");

        Rectangle insideRectangle = new Rectangle(DVC_INSIDE_LENGTH,DVC_INSIDE_HEIGHT);
        insideRectangle.getStyleClass().addAll("inside", "filled");

        Rectangle imageRectangle = new Rectangle(DVC_INSIDE_LENGTH,DVC_INSIDE_HEIGHT);
        imageRectangle.getStyleClass().add("train-image");

        cardShape.getChildren().addAll(outsideRectangle,insideRectangle,imageRectangle);
        return cardShape;
    }

    /**
     * Method that creates a basic button view with a gauge
     * @param name the name of the button
     * @param pctProperty the pct property to create the gauge
     * @return the view of the button
     */
    private static Button buttonView(String name, ReadOnlyIntegerProperty pctProperty){
        //Creation of the button according to its name
        Button buttonView = new Button(name);
        buttonView.getStyleClass().add("gauged");

        //Creation of the group containing the gauge which indicates the number of elements remaining in the stock
        Group buttonGroup = new Group();

        //Creation of the shapes that make up the gauge
        Rectangle gaugeBackground = new Rectangle(DVC_GAUGE_LENGTH,DVC_GAUGE_HEIGHT);
        gaugeBackground.getStyleClass().add("background");

        Rectangle gaugeForeground = new Rectangle(DVC_GAUGE_LENGTH,DVC_GAUGE_HEIGHT);
        gaugeForeground.getStyleClass().add("foreground");

        // Use of a property to modify the gauge in function of the percentage of elements remaining
        // In the stock
        gaugeForeground.widthProperty().bind(pctProperty.multiply(DVC_GAUGE_LENGTH).divide(100));

        //The gauge is added as a children of the group, and the group is then added as a part of the button
        buttonGroup.getChildren().addAll(gaugeBackground,gaugeForeground);
        buttonView.setGraphic(buttonGroup);
        return buttonView;
    }

    private static void displayHelpStage(VBox vbox){
        vbox=new VBox();
        vbox.setId("background");
        if(darkModeButton.isSelected()){
            vbox.getStylesheets().add("darkRules.css");
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        ScrollPane scrollPane = new ScrollPane();
        Scene scene = new Scene(scrollPane);
        vbox.getStylesheets().add("rules.css");
        stage.initModality(Modality.WINDOW_MODAL);
        TextFlow textFlow = new TextFlow();
        stage.setTitle("Règles du jeu");

        Text titleIntroText= new Text(TchuRules.TITLE_INTRO_TEXT);
        titleIntroText.setId("mainTitle");
        textFlow.getChildren().add(titleIntroText);

        Text introText= new Text(TchuRules.INTRO_TEXT);
        introText.setId("text");
        textFlow.getChildren().add(introText);

        Text titleTerminology= new Text(TchuRules.TITLE_TERMINOLOGY);
        titleTerminology.setId("title");
        textFlow.getChildren().add(titleTerminology);

        Text terminology= new Text(TchuRules.TERMINOLOGY);
        terminology.setId("text");
        textFlow.getChildren().add(terminology);

        Text titleBeginning= new Text(TchuRules.TITLE_BEGINNING_GAME);
        titleBeginning.setId("title");
        textFlow.getChildren().add(titleBeginning);

        Text beginning= new Text(TchuRules.BEGINNING_GAME);
        beginning.setId("text");
        textFlow.getChildren().add(beginning);

        Text titleTurn= new Text(TchuRules.TITLE_TURN_KIND);
        titleTurn.setId("title");
        textFlow.getChildren().add(titleTurn);

        Text turnKind= new Text(TchuRules.TURN_KIND);
        turnKind.setId("text");
        textFlow.getChildren().add(turnKind);

        Text titleEnd= new Text(TchuRules.TITLE_END_GAME);
        titleEnd.setId("title");
        textFlow.getChildren().add(titleEnd);

        Text end= new Text(TchuRules.END_GAME);
        end.setId("text");
        textFlow.getChildren().add(end);

        stage.setScene(scene);
        vbox.getChildren().addAll(textFlow);
        scrollPane.contentProperty().set(vbox);

        DarkModeButton.changeToDarkMode("darkRules.css", vbox);

        stage.show();
    }

}