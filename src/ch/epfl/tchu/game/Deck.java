package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Deck <C extends Comparable<C>> {

    private final SortedBag<C> cardsInDeck;

    private Deck(SortedBag<C> cardsInDeck){
        this.cardsInDeck=cardsInDeck;
    }

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> rand_List = new ArrayList<>(cards.toList());
        Collections.shuffle(rand_List, rng);
        SortedBag.Builder<C> rand_SB = new SortedBag.Builder<C>();
        for (int i=0; i<rand_List.size(); i++){
            rand_SB.add(rand_List.get(i));
        }
        return new Deck(rand_SB.build());
    }

    public int size(){return cardsInDeck.size();};

    boolean isEmpty(){
        if (this.size()==0){ return true; }
        else { return false; }
    };

    public C topCard(){
        Preconditions.checkArgument(!isEmpty());
        return this.cardsInDeck.get(0);
    };

    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());
        List<C> WithoutTopCard_List = new ArrayList<>(this.cardsInDeck.toList());
        WithoutTopCard_List = WithoutTopCard_List.subList(1, WithoutTopCard_List.size());
        SortedBag.Builder<C> WithoutTopCard_SB = new SortedBag.Builder<C>();
        for (int i=0; i<WithoutTopCard_List.size(); i++){
            WithoutTopCard_SB.add(WithoutTopCard_List.get(i));
        }
        return new Deck<C>(WithoutTopCard_SB.build());
    };

    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(!isEmpty() && count <= this.size());
        List<C> TopCards_List = new ArrayList<>(this.cardsInDeck.toList());
        TopCards_List = TopCards_List.subList(0,count);
        SortedBag.Builder<C> TopCards_SB = new SortedBag.Builder<C>();
        for (int i = 0; i<TopCards_List.size(); i++){
            TopCards_SB.add(TopCards_List.get(i));
        }
        return TopCards_SB.build();
    };

    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(!isEmpty() && count <= this.size());
        SortedBag<C> WithoutTopCards = this.cardsInDeck.difference(topCards(count));
        return new Deck<C>(WithoutTopCards);
    }
}
