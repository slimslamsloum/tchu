package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest{
    public static final Random NON_RANDOM = new Random() {
        @Override
        public int nextInt(int i) {
            return i-1;
        }
    };

    @Test
    void topCardWorksForAnyArray() {
        SortedBag<String> cards = SortedBag.of(2, "as de pique", 3, "dame de cœur");
        String topCard = "as de pique";
        Deck<String> deck = Deck.of(cards, NON_RANDOM);
        String card = deck.topCard();
        assertEquals(card, topCard);
    }

    @Test
    void withoutTopCardThrowsExceptionForEmptyArray(){
        SortedBag<String> cards =SortedBag.of(0, "as de pique");
        Deck<String> deck = Deck.of(cards, NON_RANDOM);
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCard();
        });
    }

    @Test
    void withoutTopCardWorksForTrivialArray(){
        SortedBag<String> cards =SortedBag.of(1, "as de pique");
        Deck<String> deck = Deck.of(cards, NON_RANDOM);
        assertTrue(deck.withoutTopCard().isEmpty());
    }
    @Test
    void withoutTopCardWorksForNonTrivialArray(){
        SortedBag<String> cards =SortedBag.of(2, "as de pique", 3, "dame de cœur");
        SortedBag<String> withoutTopCard =SortedBag.of(1, "as de pique", 3, "dame de cœur");
        Deck<String> deck= Deck.of(cards, NON_RANDOM);
        deck = deck.withoutTopCard();
        SortedBag.Builder<String> newCards=new SortedBag.Builder<>();
        while (!deck.isEmpty()) {
            newCards.add(deck.topCard());
            deck = deck.withoutTopCard();
            }
        assertEquals(newCards.build(),withoutTopCard);
    }

    @Test
    void topCardsThrowsExceptionForOutOfBoundsCount(){
        SortedBag<String> cards =SortedBag.of(2, "as de pique", 3, "dame de cœur");
        Deck<String> deck= Deck.of(cards, NON_RANDOM);
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCards(-2);
        });
    }

    @Test
    void topCardsWorksForCountZero(){
        SortedBag<String> cards =SortedBag.of(1, "as de pique");
        Deck<String> deck= Deck.of(cards, NON_RANDOM);
        cards = deck.topCards(0);
        assertTrue(cards.isEmpty());
    }

    @Test
    void topCardsWorksForNonTrivialArray(){
        SortedBag<String> cards =SortedBag.of(2, "as de pique", 3, "dame de cœur");
        SortedBag<String> topCards =SortedBag.of(2, "as de pique", 2, "dame de cœur");
        Deck<String> deck= Deck.of(cards, NON_RANDOM);
        cards = deck.topCards(4);
        assertEquals(cards,topCards);
    }
    @Test
    void topCardsWorksForBiggestPossibleCount(){
        SortedBag<String> cards =SortedBag.of(2, "as de pique", 3, "dame de cœur");
        Deck<String> deck= Deck.of(cards, NON_RANDOM);
        SortedBag<String> topCards = deck.topCards(5);
        assertEquals(cards,topCards);
    }
    @Test
    void withoutTopCardsWorksForNonTrivialArray(){
        SortedBag<String> cards =SortedBag.of(2, "as de pique", 3, "dame de cœur");
        SortedBag<String> withoutTopCards =SortedBag.of(0, "as de pique", 2, "dame de cœur");
        Deck<String> deck= Deck.of(cards, NON_RANDOM);
        deck = deck.withoutTopCards(3);
        SortedBag.Builder<String> newCards=new SortedBag.Builder<>();
        newCards.add(deck.topCards(deck.size()));
        assertEquals(newCards.build(),withoutTopCards);
    }
}
