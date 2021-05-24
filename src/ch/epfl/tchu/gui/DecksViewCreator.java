package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static ch.epfl.tchu.gui.GuiConstants.*;
import ch.epfl.tchu.gui.GraphicalPlayer;

import ch.epfl.tchu.gui.GuiConstants;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

        //Creation of the view of all player's cards
        HBox cardsHBox = new HBox();
        cardsHBox.setId("hand-pane");

        //iteration on all the cards of the game
        for (Card card : Card.ALL) {

            //Creation of the view of the card
            StackPane cardView = new StackPane();

            //Creates the property of a player, which containing the number of cards of a single type
            //he has in his possession
            ReadOnlyIntegerProperty countPpt = obsGameState.numberPerCard(card);

            //verifying that only a number of cards bigger than 0 can be seen on the deck
            cardView.visibleProperty().bind(Bindings.greaterThan(countPpt, 0));

            // Applies the right visual representation of the card according to its color
            if (card.equals(Card.LOCOMOTIVE)){
                cardView.getStyleClass().addAll("NEUTRAL","card");
            }
            else {
                cardView.getStyleClass().addAll(card.color().name(),"card");
            }

            //Creation of all the shapes that make up the card's visual representation
            Rectangle outsideRectangle = new Rectangle(DVC_OUTSIDE_LENGTH,DVC_OUTSIDE_HEIGHT);
            outsideRectangle.getStyleClass().add("outside");

            Rectangle insideRectangle = new Rectangle(DVC_INSIDE_LENGTH,DVC_INSIDE_HEIGHT);
            insideRectangle.getStyleClass().addAll("inside", "filled");

            Rectangle imageRectangle = new Rectangle(DVC_INSIDE_LENGTH,DVC_INSIDE_HEIGHT);
            imageRectangle.getStyleClass().add("train-image");

            //Creation of the text that represents the number of cards of a single type
            //the player has in his possession
            Text countText = new Text();
            countText.getStyleClass().add("count");
            countText.textProperty().bind(Bindings.convert(countPpt));
            //verifying that the number of cards of a certain type can be seen only if there is more than one card
            countText.visibleProperty().bind(Bindings.greaterThan(countPpt, 1));

            //the shapes and the text are defined as children of the view of the card
            //then the view of the card is added to the view of the player's cards hand
            cardView.getChildren().addAll(outsideRectangle,insideRectangle,imageRectangle,countText);
            cardsHBox.getChildren().add(cardView);
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
        //Creation of the global view of the deck
        VBox deckView = new VBox();
        deckView.getStylesheets().addAll("decks.css", "colors.css");
        deckView.setId("card-pane");

        //Creation of the button to draw tickets from the card's stock
        Button ticketDeckButton = new Button("Billets");
        ticketDeckButton.getStyleClass().add("gauged");

        //Creation of help button
        Button helpButton = new Button("Aide");

        //Creation of Dark Mode button
        darkModeButton = new ToggleButton("Dark Mode");

        //Creation of the group that contains the gauge which indicates the number of tickets remaining in the stock
        Group ticketDBGroup = new Group();

        //Creation of the shapes that make up the gauge
        Rectangle ticketGaugeBackground = new Rectangle(DVC_GAUGE_LENGTH,DVC_GAUGE_HEIGHT);
        ticketGaugeBackground.getStyleClass().add("background");

        Rectangle ticketGaugeForeground = new Rectangle(DVC_GAUGE_LENGTH,DVC_GAUGE_HEIGHT);
        ticketGaugeForeground.getStyleClass().add("foreground");

        // Use of a property to modify the view of the gauge in function of the percentage of tickets remaining
        // In the stock
        ReadOnlyIntegerProperty ticketPctProperty = obsGameState.percentageTickets();
        ticketGaugeForeground.widthProperty().bind(ticketPctProperty.multiply(DVC_GAUGE_LENGTH).divide(100));

        //The gauge is added as a children of the tickets group, and the group is then added as a part of the button
        //Then the Button is added as a component of the Deck's view
        ticketDBGroup.getChildren().addAll(ticketGaugeBackground,ticketGaugeForeground);
        ticketDeckButton.setGraphic(ticketDBGroup);
        deckView.getChildren().add(darkModeButton);
        deckView.getChildren().add(ticketDeckButton);

        //iteration on all the slots from which cards facing up can be drawn
        for(int slot : Constants.FACE_UP_CARD_SLOTS){

            //Creation of the view of all faceUpCards
            StackPane faceUpCardView = new StackPane();

            //Creation of the shapes that make up the view of the card
            Rectangle outsideRectangle = new Rectangle(DVC_OUTSIDE_LENGTH,DVC_OUTSIDE_HEIGHT);
            outsideRectangle.getStyleClass().add("outside");

            Rectangle insideRectangle = new Rectangle(DVC_INSIDE_LENGTH,DVC_INSIDE_HEIGHT);
            insideRectangle.getStyleClass().addAll("inside", "filled");

            Rectangle imageRectangle = new Rectangle(DVC_INSIDE_LENGTH,DVC_INSIDE_HEIGHT);
            imageRectangle.getStyleClass().add("train-image");

            //The shapes are added as children of the card, then the view of the card is added to the deck
            faceUpCardView.getChildren().addAll(outsideRectangle,insideRectangle,imageRectangle);
            deckView.getChildren().add(faceUpCardView);
            faceUpCardView.getStyleClass().addAll("", "card");
            //Adds the new card on the deck if ever a card is drawn by a player
            obsGameState.faceUpCard(slot).addListener((prop,oldVal,newVal) -> {
                faceUpCardView.getStyleClass().set(0, newVal.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : newVal.color().name() );
            });

            //If the player doesn't want to draw cards, the use of the faceUpCard is disabled
            faceUpCardView.disableProperty().bind(drawCardHP.isNull());

            //Calls the action handler in case the players clicks on a card with his mouse
            faceUpCardView.setOnMouseClicked(event -> drawCardHP.get().onDrawCard(slot));

            // Applies the right visual representation of the card according to its color

        }
        //Creation of the button to draw cards from the card's stock
        Button cardDeckButton = new Button("Cartes");
        cardDeckButton.getStyleClass().add("gauged");

        //Creation of the group that contains the gauge which indicates the number of cards remaining in the stock
        Group cardGauge = new Group();

        //Creation of the shapes that make up the gauge
        Rectangle cardGaugeBackground = new Rectangle(DVC_GAUGE_LENGTH,DVC_GAUGE_HEIGHT);
        cardGaugeBackground.getStyleClass().add("background");

        Rectangle cardGaugeForeground = new Rectangle(DVC_GAUGE_LENGTH,DVC_GAUGE_HEIGHT);
        cardGaugeForeground.getStyleClass().add("foreground");


        //The gauge is added as a children of the cards group, and the group is then added as a part of the button
        //Then the Button is added as a component of the Deck's view
        cardGauge.getChildren().addAll(cardGaugeBackground, cardGaugeForeground);
        cardDeckButton.setGraphic(cardGauge);
        deckView.getChildren().add(cardDeckButton);
        deckView.getChildren().add(helpButton);

        // Use of a property to modify the view of the gauge in function of the percentage of cards remaining
        // In the stock
        ReadOnlyIntegerProperty cardPctProperty = obsGameState.percentageCards();
        cardGaugeForeground.widthProperty().bind(cardPctProperty.multiply(DVC_GAUGE_LENGTH).divide(100));

        //Handles the distribution of cards / tickets in case the player does press on one of the button
        cardDeckButton.setOnMouseClicked(event -> drawCardHP.get().onDrawCard(Constants.DECK_SLOT));
        ticketDeckButton.setOnMouseClicked(event -> drawTicketsHP.get().onDrawTickets());

        helpButton.setOnMouseClicked(event -> displayHelpStage());

        //In case the player doesn't want to draw a card or a ticket, the use of the buttons is disabled
        cardDeckButton.disableProperty().bind(drawCardHP.isNull());
        ticketDeckButton.disableProperty().bind(drawTicketsHP.isNull());

        return deckView;
    }

    private static void displayHelpStage(){
        Stage stage = new Stage(StageStyle.DECORATED);
        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox();
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
        stage.show();
    }
}