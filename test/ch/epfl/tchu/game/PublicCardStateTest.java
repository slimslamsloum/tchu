package ch.epfl.tchu.game;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PublicCardStateTest {
    private static final List<Card> FACE_UP_CARDS =
            List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED);

    @Test
    void publicCardStateConstructorFailsWithInvalidNumberOfFaceUpCards() {
        for (int i = 0; i < 10; i++) {
            if (i == FACE_UP_CARDS.size())
                continue;

            var faceUpCards = new ArrayList<>(Collections.nCopies(i, Card.BLACK));
            assertThrows(IllegalArgumentException.class, () -> {
                new PublicCardState(faceUpCards, 0, 0);
            });
        }
    }

    @Test
    void constructorFailsWithNegativeDeckOrDiscardsSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(FACE_UP_CARDS, -1, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(FACE_UP_CARDS, 0, -1);
        });
    }

    @Test
    void constructorCopiesFaceUpCards() {
        var faceUpCards = new ArrayList<>(FACE_UP_CARDS);
        var cardState = new PublicCardState(faceUpCards, 0, 0);
        faceUpCards.clear();
        assertEquals(FACE_UP_CARDS, cardState.faceUpCards());
    }

    @Test
    void faceUpCardsReturnsImmutableListOrCopy() {
        var cardState = new PublicCardState(FACE_UP_CARDS, 0, 0);
        try {
            cardState.faceUpCards().clear();
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        assertEquals(FACE_UP_CARDS, cardState.faceUpCards());
    }

    @Test
    void faceUpCardFailsWithInvalidSlotIndex() {
        var cardState = new PublicCardState(FACE_UP_CARDS, 0, 0);
        for (int i = -20; i < 0; i++) {
            var slot = i;
            assertThrows(IndexOutOfBoundsException.class, () -> {
                cardState.faceUpCard(slot);
            });
        }
        for (int i = 6; i <= 20; i++) {
            var slot = i;
            assertThrows(IndexOutOfBoundsException.class, () -> {
                cardState.faceUpCard(slot);
            });
        }
    }

    @Test
    void faceUpCardReturnsCorrectCard() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var cards = new ArrayList<>(Card.ALL);
            Collections.shuffle(cards, new Random(i * 2021L));
            var faceUpCards = List.copyOf(cards.subList(0, 5));
            var cardState = new PublicCardState(faceUpCards, 0, 0);
            for (int j = 0; j < faceUpCards.size(); j++)
                assertEquals(faceUpCards.get(j), cardState.faceUpCard(j));
        }
    }

    @Test
    void deckSizeReturnsDeckSize() {
        for (int i = 0; i < 100; i++) {
            var cardState = new PublicCardState(FACE_UP_CARDS, i, i + 1);
            assertEquals(i, cardState.deckSize());
        }
    }

    @Test
    void isDeckEmptyReturnsTrueOnlyWhenDeckEmpty() {
        assertTrue(new PublicCardState(FACE_UP_CARDS, 0, 1).isDeckEmpty());
        for (int i = 0; i < 100; i++) {
            var cardState = new PublicCardState(FACE_UP_CARDS, i + 1, i);
            assertFalse(cardState.isDeckEmpty());
        }
    }

    @Test
    void discardsSizeReturnsDiscardsSize() {
        for (int i = 0; i < 100; i++) {
            var cardState = new PublicCardState(FACE_UP_CARDS, i + 1, i);
            assertEquals(i, cardState.discardsSize());
        }
    }
}