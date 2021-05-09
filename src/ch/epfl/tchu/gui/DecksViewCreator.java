package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import static ch.epfl.tchu.gui.ActionHandlers.*;

/**
 * Creation of the visual and playable interface of the games deck
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

class DecksViewCreator {
    //all needed dimensions for the shapes on the map
    private static final int OUTSIDE_RECTANGLE_LENGTH = 60;
    private static final int OUTSIDE_RECTANGLE_HEIGHT = 90;
    private static final int INSIDE_RECTANGLE_LENGTH = 40;
    private static final int INSIDE_RECTANGLE_HEIGHT = 70;
    private static final int GAUGE_RECTANGLE_LENGTH = 50;
    private static final int GAUGE_RECTANGLE_HEIGHT = 5;

    /**
     * private constructor
     */
    private DecksViewCreator() {
    }

    /**
     * Method that creates the player's hand view
     * @param obsGameState a game observer
     * @return the view of the player's hand
     */
    public static HBox createHandView(ObservableGameState obsGameState){
        //Creation of the visual representation of the hand
        HBox handView = new HBox();
        handView.getStylesheets().addAll("deck.css", "colors.css");

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
            Rectangle outsideRectangle = new Rectangle(OUTSIDE_RECTANGLE_LENGTH,OUTSIDE_RECTANGLE_HEIGHT);
            outsideRectangle.getStyleClass().add("outside");

            Rectangle insideRectangle = new Rectangle(INSIDE_RECTANGLE_LENGTH,INSIDE_RECTANGLE_HEIGHT);
            insideRectangle.getStyleClass().addAll("inside", "filled");

            Rectangle imageRectangle = new Rectangle(INSIDE_RECTANGLE_LENGTH,INSIDE_RECTANGLE_HEIGHT);
            imageRectangle.getStyleClass().add("train-image");

            //Creation of the text that represents the number of cards of a single type
            //the player has in his possession
            Text countText = new Text();
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
        deckView.getStylesheets().addAll("deck.css", "colors.css");
        deckView.setId("card-pane");

        //Creation of the button to draw cards from the card's stock
        Button cardDeckButton = new Button();
        cardDeckButton.getStyleClass().add("gauged");

        //Creation of the group that contains the gauge which indicates the number of cards remaining in the stock
        Group cardDBGroup = new Group();

        //Creation of the shapes that make up the gauge
        Rectangle cardGaugeBackground = new Rectangle(GAUGE_RECTANGLE_LENGTH,GAUGE_RECTANGLE_HEIGHT);
        cardGaugeBackground.getStyleClass().add("background");

        Rectangle cardGaugeForeground = new Rectangle(GAUGE_RECTANGLE_LENGTH,GAUGE_RECTANGLE_HEIGHT);
        cardGaugeForeground.getStyleClass().add("foreground");

        // Use of a property to modify the view of the gauge in function of the percentage of cards remaining
        // In the stock
        ReadOnlyIntegerProperty cardPctProperty = obsGameState.percentageCards();
        cardGaugeForeground.widthProperty().bind(cardPctProperty.multiply(50).divide(100));

        //The gauge is added as a children of the cards group, and the group is then added as a part of the button
        //Then the Button is added as a component of the Deck's view
        cardDBGroup.getChildren().addAll(cardGaugeForeground,cardGaugeBackground);
        cardDeckButton.setGraphic(cardDBGroup);
        deckView.getChildren().add(cardDeckButton);

        //Creation of the button to draw tickets from the card's stock
        Button ticketDeckButton = new Button();
        ticketDeckButton.getStyleClass().add("gauged");

        //Creation of the group that contains the gauge which indicates the number of tickets remaining in the stock
        Group ticketDBGroup = new Group();

        //Creation of the shapes that make up the gauge
        Rectangle ticketGaugeBackground = new Rectangle(GAUGE_RECTANGLE_LENGTH,GAUGE_RECTANGLE_HEIGHT);
        ticketGaugeBackground.getStyleClass().add("background");

        Rectangle ticketGaugeForeground = new Rectangle(GAUGE_RECTANGLE_LENGTH,GAUGE_RECTANGLE_HEIGHT);
        ticketGaugeForeground.getStyleClass().add("foreground");

        // Use of a property to modify the view of the gauge in function of the percentage of tickets remaining
        // In the stock
        ReadOnlyIntegerProperty ticketPctProperty = obsGameState.percentageTickets();
        ticketGaugeForeground.widthProperty().bind(ticketPctProperty.multiply(50).divide(100));

        //The gauge is added as a children of the tickets group, and the group is then added as a part of the button
        //Then the Button is added as a component of the Deck's view
        ticketDBGroup.getChildren().addAll(ticketGaugeForeground,ticketGaugeBackground);
        ticketDeckButton.setGraphic(ticketDBGroup);
        deckView.getChildren().add(ticketDeckButton);

        //In case the player doesn't want to draw a card or a ticket, the use of the buttons is disabled
        cardDeckButton.disableProperty().bind(drawCardHP.isNull());
        ticketDBGroup.disableProperty().bind(drawTicketsHP.isNull());

        //Handles the distribution of cards / tickets in case the player does press on one of the button
        cardDeckButton.setOnMouseClicked(event -> drawCardHP.get().onDrawCard(Constants.DECK_SLOT));
        cardDeckButton.setOnMouseClicked(event -> drawTicketsHP.get().onDrawTickets());

        //iteration on all the slots from which cards facing up can be drawn
        for(int slot : Constants.FACE_UP_CARD_SLOTS){

            //Creation of the view of all faceUpCards
            StackPane faceUpCardView = new StackPane();

            //According to the slot, the card is obtained via the observer
            Card faceUpCard = obsGameState.faceUpCard(slot).getValue();

            // Applies the right visual representation of the card according to its color
            if (faceUpCard.equals(Card.LOCOMOTIVE)){
                faceUpCardView.getStyleClass().addAll("NEUTRAL","card");
            }
            else {
                faceUpCardView.getStyleClass().addAll(faceUpCard.color().name(),"card");
            }

            //Creation of the shapes that make up the view of the card
            Rectangle outsideRectangle = new Rectangle(OUTSIDE_RECTANGLE_LENGTH,OUTSIDE_RECTANGLE_HEIGHT);
            outsideRectangle.getStyleClass().add("outside");

            Rectangle insideRectangle = new Rectangle(INSIDE_RECTANGLE_LENGTH,INSIDE_RECTANGLE_HEIGHT);
            insideRectangle.getStyleClass().addAll("inside", "filled");

            Rectangle imageRectangle = new Rectangle(INSIDE_RECTANGLE_LENGTH,INSIDE_RECTANGLE_HEIGHT);
            imageRectangle.getStyleClass().add("train-image");

            //The shapes are added as children of the card, then the view of the card is added to the deck
            faceUpCardView.getChildren().addAll(outsideRectangle,insideRectangle,imageRectangle);
            deckView.getChildren().add(faceUpCardView);

            //Adds the new card on the deck if ever a card is drawn by a player
            obsGameState.faceUpCard(slot).addListener((prop,oldVal,newVal) -> faceUpCardView.getStyleClass().add(newVal.color().name()));

            //If the player doesn't want to draw cards, the use of the faceUpCard is disabled
            faceUpCardView.disableProperty().bind(drawCardHP.isNull());

            //Calls the action handler in case the players clicks on a card with his mice
            faceUpCardView.setOnMouseClicked(event -> drawCardHP.get().onDrawCard(slot));
        }
        return deckView;
    }
}