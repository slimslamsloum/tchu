package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * A train station
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Station {

    //attributes needed for a station: its number and its name
    private final int id;
    private final String name;

    /**
     * Station constructor
     * @param id number of the station
     * @param name name of the station
     * @throws IllegalArgumentException
     * throws exception if the number of the station is smaller than 0
     */
    public Station(int id, String name){
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * Station id getter
     * @return Station id
     */
    public int id(){
        return this.id;
    }

    /**
     * Name getter
     * @return station name
     */
    public String name(){
        return this.name;
    }

    /**
     * Textual representation of station getter
     * @return textual representation of station
     */
    @Override
    public String toString(){
        return this.name;
    }
}
