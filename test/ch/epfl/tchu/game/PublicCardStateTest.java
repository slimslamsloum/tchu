package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PublicCardStateTest {
    List<Card> testDeck = Card.ALL.subList(0, 5);
    PublicCardState state = new PublicCardState(testDeck, 5, 5);
    PublicCardState empty_state = new PublicCardState(testDeck, 0, 5);

    @Test
    void constructorErrorsTest() {
        assertThrows(IllegalArgumentException.class, () -> new PublicCardState(testDeck, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new PublicCardState(testDeck, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> new PublicCardState(Card.ALL.subList(0,4), 0, 0));
        assertDoesNotThrow(() -> new PublicCardState(Card.ALL.subList(0,5), 0, 0));
    }

    @Test
    void totalSize() {
        assertEquals(20 + 30 + 5, new PublicCardState(testDeck, 20, 30).totalSize());
    }

    @Test
    void faceUpCard() {
        assertThrows(IndexOutOfBoundsException.class, ()  -> state.faceUpCard(6));
        assertThrows(IndexOutOfBoundsException.class, ()  -> state.faceUpCard(-1));
        assertDoesNotThrow(() -> state.faceUpCard(0));
        assertEquals(Color.ALL.get(0), state.faceUpCard(0).color());
    }

    @Test
    void discardsize(){
        assertEquals(5, state.discardsSize());
    }

    @Test
    void isDeckEmpty(){
        assertEquals(true, empty_state.isDeckEmpty());
        assertEquals(false, state.isDeckEmpty());
    }
}