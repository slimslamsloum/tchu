package ch.epfl.tchu.game;

import java.util.List;

/**
 * Different colors
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public enum Color {
    //the different constants for the enum Color
    BLACK, VIOLET, BLUE , GREEN, YELLOW, ORANGE, RED, WHITE;

    //List of these different constants
    public final static List<Color> ALL = List.of(values());

    //number of constants in Color
    public final static int COUNT= ALL.size();
}
