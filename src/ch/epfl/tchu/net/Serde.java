package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.function.Function;

public interface Serde <T>{

    String serialize(T t);

    T deserialize(String string);

    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize){
        return new Serde<T>() {
            @Override
            public String serialize(T t) {
                return serialize.apply(t);
            }

            @Override
            public T deserialize(String s) {
                return deserialize.apply(s) ;
            }
        };
    }

    static <T> Serde<T> OneOf(List<T> ValEnum){

    }


    static <T> Serde<List<T>> ListOf(Serde serde, String separator){

    }


    static <T extends Comparable<T>> Serde<SortedBag<T>> BagOf(Serde serde, String separator){

    }

}
