package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class StationPartition implements StationConnectivity{

    private int[] partition;

    private StationPartition(int[] station_partition){
        this.partition = partition;
    }

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

    public final static class Builder {
        private int[] flat_partition;

        Builder(int stationCount){
            Preconditions.checkArgument(stationCount >= 0);
            flat_partition = new int[stationCount];
        }

        public Builder connect (Station s1, Station s2){
            flat_partition[s2.id()] = representative(s1.id());
            return this;
        }

        public StationPartition build(){
            return new StationPartition(flat_partition);
        }

        private int representative (int id){
            return flat_partition[id];
        }
    }
}
