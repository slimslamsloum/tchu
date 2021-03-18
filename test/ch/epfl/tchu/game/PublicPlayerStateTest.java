package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import ch.epfl.tchu.game.ChMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PublicPlayerStateTest {

    int ticketCount= 10;
    int cardCount = 15;
    List<Route> routes = ChMap.routes().subList(0, 9);

    @Test
    void ConstructorWorks(){
        PublicPlayerState PPS =  new PublicPlayerState(ticketCount, cardCount, routes);
        assert(PPS.carCount() == 40 - 17);
        System.out.println(PPS.claimPoints());
        assert(PPS.claimPoints() ==21);
    }

    @Test
    void ConstructorFailsWhenOutOfBounds(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PublicPlayerState PPS =  new PublicPlayerState(-3, cardCount, routes);
        } );
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            PublicPlayerState PPS =  new PublicPlayerState(5, -6, routes);
        } );
    }


}
