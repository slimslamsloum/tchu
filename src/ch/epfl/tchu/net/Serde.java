package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface Serde <T>{

    String serialize(T t);

    T deserialize(String string);

    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize){
        return new Serde<T>() {
            @Override
            public String serialize(T t) {
                return serialize.apply(t); }

            @Override
            public T deserialize(String s) {
                return deserialize.apply(s) ;
            }
        };
    }

    static <T> Serde<T> oneOf(List<T> ValEnum){



    }


    static <T> Serde<List<T>> listOf(Serde serde, char separator){
        return new Serde<List<T>>() {
            @Override
            public String serialize(List<T> list) {
                String string = "";
                int i=0;
                for (T t : list){
                    i++;
                    string += serde.serialize(t);
                    if (i != list.size()){
                        string += separator;
                    }
                }
                return string;
            }

            @Override
            public List<T> deserialize(String string) {
                return null;
            }
        }

    }


    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde serde, char separator){

    }

}
