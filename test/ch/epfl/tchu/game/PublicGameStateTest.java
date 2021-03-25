package ch.epfl.tchu.game;


import static org.junit.jupiter.api.Assertions.*;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class PublicGameStateTest {
    @Test
    void PublicGameStateConstructorWorks() {
        var cardDeck = Constants.ALL_CARDS.toList().subList(0, 5);
        var cardState = new PublicCardState(cardDeck, cardDeck.size(), 0);
        Map<PlayerId,PublicPlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
        Map<PlayerId,PublicPlayerState> playerStateMap2 = new EnumMap<>(PlayerId.class);
        var currentPlayerId=PlayerId.PLAYER_2;
        var lastPlayerId = PlayerId.PLAYER_1;
        PlayerId finalPlayerID=null;
        var tickets = SortedBag.of(ChMap.tickets().subList(0, 5));
        PlayerState playerState= PlayerState.initial(SortedBag.of(cardDeck.subList(0,4)));
        playerStateMap.put(currentPlayerId,playerState);
        playerStateMap.put(lastPlayerId,playerState);

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(-1,cardState,currentPlayerId,playerStateMap,finalPlayerID);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(tickets.size(), cardState,currentPlayerId,playerStateMap2,finalPlayerID);
        });
        playerStateMap2.put(lastPlayerId,playerState);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(tickets.size(), cardState,currentPlayerId,playerStateMap2,finalPlayerID);
        });
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(tickets.size(), cardState,null,playerStateMap,finalPlayerID);
        });
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(tickets.size(), null,currentPlayerId,playerStateMap,finalPlayerID);
        });
    }

}