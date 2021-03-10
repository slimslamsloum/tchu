package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class CardStateTest {
    private static CardState all;
    private static Deck<Card> deck;
    @BeforeAll
    private static void init(){
        List<Card> myList = new LinkedList<>();;
        for(Card card : Card.ALL){
            int k = TestRandomizer.newRandom().nextInt(50) + 5;

            for(int i = 0 ; i<k;++i){
                myList.add(card);
            }
        }
        SortedBag<Card> cards = SortedBag.of(myList);
        deck = Deck.of(cards,new Random());
        all = CardState.of(deck);
    }

   CardState generate(List<Card> Cards){
       return CardState.of(Deck.of(SortedBag.of(Cards), DeckTest.NON_RANDOM));
   }

    CardState test_cardState =
            generate(Card.ALL);
    //all;

   @Test
    void topDeckCardWorks(){
       assertEquals(Card.ALL.get(5), test_cardState.topDeckCard());
    }

    @Test
    void CardIsReplaced(){
       CardState arbitrary_CS = test_cardState.withDrawnFaceUpCard(3);
       assertEquals(Card.ALL.get(5), arbitrary_CS.faceUpCard(3));
    }

    @Test
    void withoutTopDeckCardWorks(){
       CardState arbitrary_CS = test_cardState.withoutTopDeckCard();
       assertNotEquals(Card.ALL.get(5), arbitrary_CS.faceDownCards().topCard());
       assertEquals(Card.ALL.get(6), arbitrary_CS.faceDownCards().topCard());
    }

    @Test
    void newDiscardCardsAreAdded(){
        SortedBag<Card> discards = SortedBag.of(Card.ALL.subList(0,5));
        CardState arbitrary_CS = test_cardState.withMoreDiscardedCards(discards);
        assertEquals(5, arbitrary_CS.discardsSize());
        assertEquals(Card.ALL.subList(0,5), arbitrary_CS.discardCards().toList());
    }

    @Test
    void newDeckCreatedFromDiscard(){
        SortedBag<Card> discards = SortedBag.of(Card.ALL.subList(0,5));
        SortedBag<Card> FACEUP_SB = SortedBag.of(Card.ALL.subList(1,6));
        Deck empty_deck = Deck.of(FACEUP_SB, new Random(1));
        CardState arbitrary_CS = CardState.of(empty_deck);
        arbitrary_CS = arbitrary_CS.withMoreDiscardedCards(discards);
        arbitrary_CS = arbitrary_CS.withDeckRecreatedFromDiscards(new Random());
        assertEquals(0, arbitrary_CS.discardCards().size());
        assertEquals(5, arbitrary_CS.faceDownCards().size());
        assertEquals(Card.ALL.subList(0,5), arbitrary_CS.faceDownCards().topCards(5).toList());
    }

}
