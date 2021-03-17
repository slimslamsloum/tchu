package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class StationPartition implements StationConnectivity{

    private final int[] partition;

    private StationPartition(int[] station_partition){
        this.partition = station_partition;
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
        private final int[] deep_partition;

        Builder(int stationCount){
            Preconditions.checkArgument(stationCount >= 0);
            deep_partition = new int[stationCount];
        }

        public Builder connect (Station s1, Station s2){
            deep_partition[s2.id()] = representative(s1.id());
            return this;
        }

        public StationPartition build(){
            for(int i=0; i< deep_partition.length; i++){
                if(representative(i)!=i){
                    int j=i;
                    while(representative(j)!=j){
                        j=representative(j);
                    }
                    deep_partition[i]=deep_partition[j];
                }
            }
            return new StationPartition(deep_partition);
        }

        private int representative (int id){
            return deep_partition[id];
        }
    }
}