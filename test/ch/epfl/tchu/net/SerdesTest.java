package ch.epfl.tchu.net;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static ch.epfl.tchu.net.Serdes.routeSerde;
import static org.junit.jupiter.api.Assertions.*;
public class SerdesTest {
    @Test
    void SerdeWorksForIntAndString(){
        assertEquals("2021",Serdes.intSerde.serialize(2021));
        assertEquals(2021,Serdes.intSerde.deserialize("2021"));
        assertEquals("Q2hhcmxlcw==", Serdes.stringSerde.serialize("Charles"));
        assertEquals("Charles", Serdes.stringSerde.deserialize("Q2hhcmxlcw=="));
    }
    @Test
    void SerdeWorksForEnumValues(){
        assertEquals("0",Serdes.playerIdSerde.serialize(PlayerId.PLAYER_1));
        assertEquals(PlayerId.PLAYER_1,Serdes.playerIdSerde.deserialize("0"));

        assertEquals("2",Serdes.turnKindSerde.serialize(Player.TurnKind.CLAIM_ROUTE));
        assertEquals(Player.TurnKind.CLAIM_ROUTE,Serdes.turnKindSerde.deserialize("2"));

        assertEquals(String.valueOf(Card.ALL.size()-1),Serdes.cardSerde.serialize(Card.LOCOMOTIVE));
        assertEquals(Card.LOCOMOTIVE,Serdes.cardSerde.deserialize(String.valueOf(Card.ALL.size()-1)));

        assertEquals("0",Serdes.routeSerde.serialize(ChMap.routes().get(0)));
        assertEquals(ChMap.routes().get(0),Serdes.routeSerde.deserialize("0"));

        assertEquals("21",Serdes.ticketSerde.serialize(ChMap.tickets().get(21)));
        assertEquals(ChMap.tickets().get(21),Serdes.ticketSerde.deserialize("21"));
    }
    @Test
    void SerdeWorksForLists(){

        assertEquals("0,1,2,3,4,5,6,7,8",Serdes.listCardSerde.serialize(Card.ALL));
        assertEquals(Card.ALL,Serdes.listCardSerde.deserialize("0,1,2,3,4,5,6,7,8"));

        assertEquals("3,4,5,6,7,8,9,10,11,12,13",Serdes.listRouteSerde.serialize(ChMap.routes().subList(3,14)));
        assertEquals(ChMap.routes().subList(3,14),Serdes.listRouteSerde.deserialize("3,4,5,6,7,8,9,10,11,12,13"));
    }

}