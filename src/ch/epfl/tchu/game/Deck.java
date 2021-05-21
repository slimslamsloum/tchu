package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A deck of cards / tickets
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Deck <C extends Comparable<C>> {

    // Only attribute of a deck : list of all cards that compose a deck
    private final List<C> cardsInDeck;

    /**
     * Deck constructor
     * @param cardsInDeck all cards composing / elements the deck
     */
    private Deck(List<C> cardsInDeck){
        this.cardsInDeck=cardsInDeck;
    }

    /**
     * Deck generator
     * @param cards all cards initially sorted
     * @param rng a randomizer
     * @param <C> The kind of element composing the deck, can be a card, a ticket or any comparable element
     * @return a new randomly shuffled deck
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> rand_List = new ArrayList<>(cards.toList());
        Collections.shuffle(rand_List, rng);
        return new Deck<>(rand_List);
    }

    /**
     * Methods that returns the size of the deck
     * @return the size of a deck
     */
    public int size(){return cardsInDeck.size();}

    /**
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty(){
        return this.size() == 0;
    }

    /**
     * Method that returns the card at the top of the deck
     * @return the card at the top of a deck
     * @throws IllegalArgumentException if deck is empty
     */
    public C topCard(){
        Preconditions.checkArgument(!isEmpty());
        return this.cardsInDeck.get(0);
    }

    /**
     * Method that returns a deck without its top card
     * @return the deck without its top card
     * @throws IllegalArgumentException if deck is empty
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());
        List<C> WithoutTopCard_List = new ArrayList<>(this.cardsInDeck);
        WithoutTopCard_List = WithoutTopCard_List.subList(1, WithoutTopCard_List.size());
        return new Deck<>(WithoutTopCard_List);
    }

    /**
     * Method that returns the first "count" top cards of the deck, "count" a chosen value
     * @param count the number of topCards we want to obtain
     * @return a sorted bag of the decks "count" top cards
     * @throws IllegalArgumentException if deck is empty, or count is smaller than 0, or bigger than the deck size
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(count <= this.size() && count >=0);
        List<C> topCards_List = new ArrayList<>(this.cardsInDeck);
        topCards_List = topCards_List.subList(0,count);
        SortedBag.Builder<C> topCards_SB = new SortedBag.Builder<>();
        for (C content : topCards_List){
            topCards_SB.add(content);
        }
        return topCards_SB.build();
    }

    /**
     * Method that creates a new deck without its previous "count" top cards, "count" a chosen value
     * @param count the number of topCards we want to exclude from the new deck
     * @return the new deck without the "count" top cards
     * @throws IllegalArgumentException if deck is empty, or count is smaller than 0, or bigger than the deck size
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(count <= this.size() && count >=0);
        List<C> WithoutTopCards = new ArrayList<>(this.cardsInDeck);
        WithoutTopCards = WithoutTopCards.subList(count, WithoutTopCards.size());
        return new Deck<>(WithoutTopCards);
    }

}