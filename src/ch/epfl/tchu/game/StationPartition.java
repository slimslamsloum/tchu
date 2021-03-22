package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Station partition
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class StationPartition implements StationConnectivity{

    //attribute: array of integers where each index represents a station's id, and its value is interpreted as its
    //representant's id
    private final int[] partition;

    /**
     * StationPartition constructor
     * @param station_partition
     */
    private StationPartition(int[] station_partition){
        this.partition = station_partition;
    }

    /**
     * Method that checks if two stations are connected or not
     * @param s1 a station
     * @param s2 another station
     * @return if stations' id are within partition length, returns true iff stations' representants are the same. If stations'
     * aren't both within partition length, returns true iff stations have same id.
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= partition.length || s2.id() >= partition.length){
            if (s1.id() == s2.id()){
                return true;
            }
            else return false;
        }
        else{
            if (partition[s1.id()] == partition[s2.id()]){
                return true;
            }
            else return false;
        }
    }

    //Subclass of StationPartition: Builder
    public final static class Builder {

        //attribute of Builder: a deep partition that will need to be edited so that stations that are connected
        //have same representants
        private final int[] deep_partition;

        /**
         * Builder constructor
         * @param stationCount number of stations in Builder
         * @throws IllegalArgumentException if station count is negative
         */
        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount >= 0);
            deep_partition = new int[stationCount];
            for (int i=0; i<stationCount; i++){
                deep_partition[i]=i;
            }
        }

        /**
         * Method that connects two stations
         * @param s1 a station
         * @param s2 another station
         * @return a partition where both stations given as argument now have same representant
         */
        public Builder connect (Station s1, Station s2){
            deep_partition[representative(s2.id())] = representative(s1.id());
            return this;
        }

        /**
         * Method that turns partition builder into a finished station partition
         * @return final station partition
         */
        public StationPartition build(){
            for(int i=0; i< deep_partition.length; i++){
                if(deep_partition[i]!=i){
                    int j=i;
                    while(deep_partition[j]!=j){
                        j=deep_partition[j];
                    }
                    deep_partition[i]=deep_partition[j];
                }
            }
            return new StationPartition(deep_partition);
        }

        /**
         * Computes the station the represents the station at index id
         * @param id id of the desired station
         * @return representative station of the station that has the id given as argument
         */
        private int representative (int id){
            while (deep_partition[id]!=id){
                id = deep_partition[id];
            }
            return id;
        }
    }
}