package ch.epfl.tchu.game;

import java.util.List;

/**
 * Different cards
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public enum Card {

    //different constants for the enum Card
    BLACK (Color.BLACK), VIOLET(Color.VIOLET), BLUE(Color.BLUE), GREEN(Color.GREEN), YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE), RED(Color.RED), WHITE(Color.WHITE), LOCOMOTIVE(null);

    /**
     * List of card values
     */
    public final static List<Card> ALL = List.of(Card.values());

    /**
     * Number of cards values
     */
    public final static int COUNT= ALL.size();

    private final Color color;

    /**
     * Card constructor
     * @param color
     */
    private Card(Color color) {
        this.color = color;
    }

    /**
     * List of cars (not the same as cards)
     */
    public final static List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    /**
     * Card generator
     * @param color color of a card
     * @return returns a certain card depending on the color given as a parameter
     * @throws NullPointerException
     * throws exception if color is null
     */
    public static Card of (Color color){
        if(color == null){
            throw new NullPointerException("color is null");
        }
        for (int i=0; i<= Card.COUNT; ++i) {
            Card card = CARS.get(i);
            if (color == card.color()) {
                return card;
            }
        }
        return null;
    }

    /**
     * Color Getter
     * @return color
     */
    public Color color() {
        return color;
    }
}