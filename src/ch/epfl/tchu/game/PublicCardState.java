package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * State of public cards (face up)
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class PublicCardState {

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
       Preconditions.checkArgument(faceUpCards.size()==5 && deckSize >= 0 && discardsSize >= 0);
       this.faceUpCards=faceUpCards;
       this.deckSize=deckSize;
       this.discardsSize=discardsSize;
    }

    /**
     *
     * @return the sum of the sizes of the face up cards, the deck size, and the discard pile size
     */
    public int totalSize(){ return faceUpCards.size()+deckSize+discardsSize; };

    /**
     *
     * @param slot of the desired card
     * @return the card at index slot
     * @throws IndexOutOfBoundsException if slot isn't between 0 (included) and 5 (excluded)
     */
    public Card faceUpCard(int slot){
        if (slot < 0 || slot >= 5){
            throw new IndexOutOfBoundsException("index is out of bounds");
        }
        return faceUpCards.get(slot);
    };

    /**
     *
     * @return true if deck is empty, else returns fals3e
     */
    public boolean isDeckEmpty(){
        if(deckSize==0){
            return true;
        }
        else return false;
        };

    //getter that returns size of discard pile
    public int discardsSize(){ return discardsSize; };
    //getter that returns a list of the visible cards
    public List<Card>faceUpCards(){ return faceUpCards; };
    //getter that returns size of deck
    public int deckSize(){ return deckSize; };
}
