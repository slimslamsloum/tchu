package ch.epfl.tchu.net;

import java.util.List;
import java.util.function.Function;

public interface Serde <T>{

    String serialize(T JavaObj);

    T deserialize(String string);

    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize){
        return new Serde<T>() {
            @Override
            public String serialize(T JavaObj) {
                return serialize(JavaObj);
            }

            @Override
            public T deserialize(String string) {
                return deserialize(string);
            }
        };
    }

    static <T> Serde<T> OneOf(List<T> ValEnum){

    }


    static <T> Serde<T> ListOf(Serde serde, char separator){

    }


    static <T> Serde<T> BagOf(Serde serde, char separator){

    }

}
