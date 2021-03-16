package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public final class CardStateTest {
    private static final List<Card> ALL_CARDS = List.of(Card.values());
    private static final int FACE_UP_CARDS_COUNT = 5;

    @Test
    void cardStateOfFailsIfDeckIsTooSmall() {
        for (int i = 0; i < FACE_UP_CARDS_COUNT; i++) {
            var deck = Deck.of(SortedBag.of(i, Card.RED), new Random(i));
            assertThrows(IllegalArgumentException.class, () -> {
                CardState.of(deck);
            });
        }
    }

    @Test
    void cardStateOfCorrectlyDrawsFaceUpCards() {
        var cards = allCards();

        for (int i = 0; i < 10; i++) {
            var deck = Deck.of(cards, new Random(i));

            var top5 = new ArrayList<Card>();
            var deck1 = deck;
            for (int j = 0; j < 5; j++) {
                top5.add(deck1.topCard());
                deck1 = deck1.withoutTopCard();
            }

            var cardState = CardState.of(deck);
            var faceUpCards = new ArrayList<>(cardState.faceUpCards());

            // Sort the cards, as the assignment was not explicit about preserving order
            Collections.sort(top5);
            Collections.sort(faceUpCards);

            assertEquals(top5, faceUpCards);
            assertEquals(deck.size() - 5, cardState.deckSize());
            assertEquals(0, cardState.discardsSize());
        }
    }

    @Test
    void cardStateWithDrawnFaceUpCardCorrectlyReplacesIt() {
        var cards = allCards();

        for (int i = 0; i < 10; i++) {
            var deck = Deck.of(cards, new Random(-i));

            var deck1 = deck.withoutTopCards(5);
            var next5 = new ArrayList<Card>();
            for (int j = 0; j < 5; j++) {
                next5.add(deck1.topCard());
                deck1 = deck1.withoutTopCard();
            }

            var cardState = CardState.of(deck);
            var next5It = next5.iterator();
            var slots = new ArrayList<>(List.of(0, 1, 2, 3, 4));
            Collections.shuffle(slots, new Random(i * i));
            for (int slot : slots) {
                cardState = cardState.withDrawnFaceUpCard(slot);
                assertEquals(next5It.next(), cardState.faceUpCard(slot));
            }
        }
    }

    @Test
    void cardStateTopDeckCardFailsWithEmptyDeck() {
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.ORANGE), TestRandomizer.newRandom()));
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.topDeckCard();
        });
    }

    @Test
    void cardStateTopDeckCardReturnsTopDeckCard() {
        var cards = allCards();
        for (int i = 0; i < 10; i++) {
            var deck = Deck.of(cards, new Random((i + 35) * 7));
            var topDeckCard = deck.withoutTopCards(5).topCard();
            var cardState = CardState.of(deck);
            assertEquals(topDeckCard, cardState.topDeckCard());
        }
    }

    @Test
    void cardStateWithoutTopDeckCardFailsWithEmptyDeck() {
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.ORANGE), TestRandomizer.newRandom()));
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withoutTopDeckCard();
        });
    }

    @Test
    void cardStateWithoutTopDeckCardWorks() {
        var cards = allCards();

        for (int i = 0; i < 10; i++) {
            var deck = Deck.of(cards, new Random(2021 - i));

            var expectedCards = new ArrayList<Card>();
            var deck1 = deck.withoutTopCards(5);
            while (!deck1.isEmpty()) {
                expectedCards.add(deck1.topCard());
                deck1 = deck1.withoutTopCard();
            }

            var actualCards = new ArrayList<Card>();
            var cardState = CardState.of(deck);
            while (!cardState.isDeckEmpty()) {
                actualCards.add(cardState.topDeckCard());
                cardState = cardState.withoutTopDeckCard();
            }

            assertEquals(expectedCards, actualCards);
        }
    }

    @Test
    void cardStateWithDeckRecreatedFromDiscardsFailsWhenDeckIsNotEmpty() {
        var deck = Deck.of(SortedBag.of(6, Card.RED), TestRandomizer.newRandom());
        var cardState = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom());
        });
    }

    @Test
    void cardStateWithDeckRecreatedFromDiscardsWorksWithEmptyDiscards() {
        var deck = Deck.of(
                SortedBag.of(FACE_UP_CARDS_COUNT, Card.RED),
                TestRandomizer.newRandom());
        var cardState = CardState.of(deck);
        var cardState1 = cardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom());
        assertEquals(0, cardState1.deckSize());
        assertEquals(0, cardState1.discardsSize());
    }

    @Test
    void cardStateWithDeckRecreatedFromDiscardsWorksWithNonEmptyDiscards() {
        var deck = Deck.of(
                SortedBag.of(FACE_UP_CARDS_COUNT, Card.RED),
                TestRandomizer.newRandom());
        var discardsCount = 10;
        var discards = SortedBag.of(discardsCount, Card.BLUE);
        var cardState = CardState.of(deck)
                .withMoreDiscardedCards(discards)
                .withDeckRecreatedFromDiscards(TestRandomizer.newRandom());
        assertEquals(discardsCount, cardState.deckSize());
        var deckCards = new SortedBag.Builder<Card>();
        for (int i = 0; i < discardsCount; i++) {
            var topDeckCard = cardState.topDeckCard();
            cardState = cardState.withoutTopDeckCard();
            deckCards.add(topDeckCard);
        }
        assertTrue(cardState.isDeckEmpty());
        assertEquals(discards, deckCards.build());
    }

    @Test
    void cardStateWithMoreDiscardedCardsWorks() {
        var rng = TestRandomizer.newRandom();
        var deck = Deck.of(
                SortedBag.of(FACE_UP_CARDS_COUNT, Card.RED),
                TestRandomizer.newRandom());
        var expectedDeckBuilder = new SortedBag.Builder<Card>();
        var cardState = CardState.of(deck);
        for (Card card : ALL_CARDS) {
            var count = rng.nextInt(12);
            var discards = SortedBag.of(count, card);
            cardState = cardState.withMoreDiscardedCards(discards);
            expectedDeckBuilder.add(count, card);
        }
        cardState = cardState.withDeckRecreatedFromDiscards(new Random(rng.nextLong()));
        var expectedDeck = expectedDeckBuilder.build();

        var actualDeck = new SortedBag.Builder<Card>();
        for (int i = 0; i < expectedDeck.size(); i++) {
            var topDeckCard = cardState.topDeckCard();
            cardState = cardState.withoutTopDeckCard();
            actualDeck.add(topDeckCard);
        }
        assertTrue(cardState.isDeckEmpty());
        assertEquals(expectedDeck, actualDeck.build());
    }

    private SortedBag<Card> allCards() {
        var cardsBuilder = new SortedBag.Builder<Card>();
        cardsBuilder.add(14, Card.LOCOMOTIVE);
        for (Card card : Card.CARS)
            cardsBuilder.add(12, card);
        var cards = cardsBuilder.build();
        return cards;
    }
}
