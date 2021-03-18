package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StationPartitionTest {

    private static final Station BAD = new Station(0, "Baden");
    private static final Station BAL = new Station(1, "Bâle");
    private static final Station BEL = new Station(2, "Bellinzone");
    private static final Station BER = new Station(3, "Berne");
    private static final Station BRI = new Station(4, "Brigue");
    private static final Station BRU = new Station(5, "Brusio");
    private static final Station COI = new Station(6, "Coire");
    private static final Station DAV = new Station(7, "Davos");
    private static final Station DEL = new Station(8, "Delémont");
    private static final Station FRI = new Station(9, "Fribourg");
    private static final Station GEN = new Station(10, "Genève");

    @Test
    void connectedWorksWhenStationsAreConnected(){
        StationPartition.Builder SP_Builder = new StationPartition.Builder(11);
        SP_Builder.connect(BAD, BAL);
        SP_Builder.connect(BRU,COI);
        StationPartition SP = SP_Builder.build();
        assert(SP.connected(BAD,BAL));
        assert(SP.connected(BRU,COI));
        assert(SP.connected(BAD, BAD));
    }

    @Test
    void connectedReturnsFalseIfNotConnected(){
        StationPartition.Builder SP_Builder2 = new StationPartition.Builder(11);
        StationPartition SP2 = SP_Builder2.build();
        assertFalse(SP2.connected(BAL,BRU));
        assertFalse(SP2.connected(DAV, DEL));
    }

    @Test
    void connectedWorkswhenOutofBounds(){
        StationPartition.Builder SP_Builder3 = new StationPartition.Builder(10);
        StationPartition SP3 = SP_Builder3.build();
        assert(SP3.connected(GEN,GEN));
        assertFalse(SP3.connected(GEN, DAV));
    }

}
