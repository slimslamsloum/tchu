package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * State of cards (discard, face up and face down)
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class CardState extends PublicCardState {

    //more attributes on top of the ones in PublicCardState: list of face down cards and list of discard cards
    private final Deck<Card> faceDownCards;
    private final SortedBag<Card> discardCards;

    /**
     * CardState constructor
     * @param faceUpCards cards that are visible
     * @param deckSize size of deck containing face down cards
     * @param discardsSize size of discard pile
     * @param faceDownCards cards that are face down
     * @param discardCards discard cards
     */
    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> faceDownCards, SortedBag<Card> discardCards) {
        super(faceUpCards, deckSize, discardsSize);
        this.discardCards=discardCards;
        this.faceDownCards=faceDownCards;
    }

    /**
     * CardState generator
     * @param deck takes a deck as input, takes 5 first cards of deck for face up cards, uses rest of deck for face down cards
     * @return returns a Cardstate containing face up, face down cards and an empty SortedBag of discard cards
     * @throws IllegalArgumentException if deck size isn't bigger or equal than 5
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>= Constants.FACE_UP_CARDS_COUNT);
        SortedBag<Card> empty_discard = SortedBag.of();
        return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(), deck.size()-Constants.FACE_UP_CARDS_COUNT,
                0, deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT) , empty_discard);
    }

    /**
     *
     * @param slot desired index of the face up cards
     * @return a cardstate where the face up card at index slot has been replaced by the top card of the deck
     * @throws IndexOutOfBoundsException if slot isn't between 0 (included) and 5 (excluded)
     * @throws IllegalArgumentException if deck is empty
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!this.isDeckEmpty());
        if (!(slot >= 0 && slot <5)){
            throw new IndexOutOfBoundsException("slot is out of bounds");
        }
        ArrayList<Card> faceUp = new ArrayList<>(this.faceUpCards());
        faceUp.set(slot, this.faceDownCards.topCard());
        return new CardState(faceUp, deckSize()-1, discardsSize(), this.faceDownCards.withoutTopCard(), discardCards());
    }

    /**
     *
     * @return card at the top of the deck
     * @throws IllegalArgumentException if deck is empty
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!this.isDeckEmpty());
        return this.faceDownCards.topCard();
    }

    /**
     *
     * @return cardstate where the card at the top of the deck has been removed
     * @throws IllegalArgumentException if deck is empty
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!this.isDeckEmpty());
        Deck<Card> without_top = this.faceDownCards.withoutTopCard();
        return new CardState(faceUpCards(), without_top.size(), discardsSize(), without_top, discardCards());
    }

    /**
     *
     * @param rng
     * @return new cardstate where discard cards were used to form a new deck
     * @throws IllegalArgumentException if deck isn't empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(this.isDeckEmpty());
        SortedBag<Card> empty_discard = SortedBag.of();
        Deck<Card> newDeck = Deck.of(this.discardCards, rng);
        return new CardState(faceUpCards(), newDeck.size(), 0, newDeck, empty_discard);
    }

    /**
     *
     * @param additionalDiscards cards to be added to discard pile
     * @return cardstate with the additional cards added to the discard pile
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        List<Card> new_discard = discardCards.toList();
        for (Card card : additionalDiscards){
            new_discard.add(card);
        }
        SortedBag.Builder<Card> discard_SB = new SortedBag.Builder<>();
        for (Card card : new_discard){
            discard_SB.add(card);
        }
        return new CardState(faceUpCards(), deckSize(), discardsSize()+additionalDiscards.size(), faceDownCards(), discard_SB.build());
    }

    //getters: return face down cards and discard cards
    public Deck<Card> faceDownCards(){ return faceDownCards; }
    public SortedBag<Card> discardCards(){ return discardCards; }
}
