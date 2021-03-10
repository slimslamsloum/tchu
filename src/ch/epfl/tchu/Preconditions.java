package ch.epfl.tchu;

/**
 * Preconditions
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Preconditions {
    private Preconditions(){};

    /**
     * @param shouldBeTrue
     * @throws IllegalArgumentException
     * throws error if statement given as parameter is false
     **/
    public static void checkArgument (boolean shouldBeTrue){
        if (shouldBeTrue == false){
            throw new IllegalArgumentException();
        }
    }
}
