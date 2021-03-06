package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * State of public cards (face up)
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class PublicCardState{

    //attributes: the list of face up cards, size of deck, and size of discard deck
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * PublicCardState constructor
     * @param faceUpCards the visible cards
     * @param deckSize the size of the deck
     * @param discardsSize the size of the discard pile
     * @throws IllegalArgumentException if discard pile size isn't a positive number or if number of face up cards
     * isn't between 0 (included) and 5 (excluded)
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size()==Constants.FACE_UP_CARDS_COUNT && deckSize >= 0 && discardsSize >= 0);
        this.faceUpCards= List.copyOf(faceUpCards);
        this.deckSize=deckSize;
        this.discardsSize=discardsSize;
    }

    /**
     * Methods that returns face up card at a certain index
     * @param slot of the desired card
     * @return the card at index slot
     * @throws IndexOutOfBoundsException if slot isn't between 0 (included) and 5 (excluded)
     */
    public Card faceUpCard(int slot){
        slot = Objects.checkIndex(slot,Constants.FACE_UP_CARDS_COUNT );
        return faceUpCards.get(slot);
    }

    /**
     * Asks if deck is empty or not
     * @return true if deck is empty, else returns false
     */
    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    /**
     * Discard size getter
     * @return discard size
     */
    public int discardsSize(){ return discardsSize; }

    /**
     * Face up cards getter
     * @return face up cards
     */
    public List<Card>faceUpCards(){ return faceUpCards; }

    /**
     * Deck size getter
     * @return deck size
     */
    public int deckSize(){ return deckSize; }
}