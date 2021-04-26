package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.PlayerId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An object able to de/serialize
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public interface Serde <T>{

    /**
     * Abstract method, will be defined to serialize certain objects
     * @param t parameter of generic type t
     * @return string representation of object
     */
    String serialize(T t);

    /**
     * Abstract method, will be defined to deserialize a string
     * @param string textual representation of an object
     * @return the actual object
     */
    T deserialize(String string);

    /**
     * Creates an object (Serde) able to serialize an object of type T
     * and deserialize a string into a T
     * @param serialize function that serializes
     * @param deserialize function the deserializes
     * @param <T> type of the object
     * @return the Serde
     */
    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize){
        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return serialize.apply(t); }

            @Override
            public T deserialize(String s) {
                return deserialize.apply(s) ;
            }
        };
    }

    /**
     * Creates serde capable of serializing a certain tChu object (a turnkind, a player id, a route...)
     * also called an enumerated value
     * @param ValEnum list of values of the tChu enumerated value
     * @param <T> type of the enumerated value
     * @return the serde capable of de/serializing an enumerated value
     */
    static <T> Serde<T> oneOf(List<T> ValEnum){
        return new Serde<>() {
            @Override
            public String serialize(T t) {
                if (t == null){ return ""; }
                else return String.valueOf(ValEnum.indexOf(t)); }

            @Override
            public T deserialize(String s) {
                if (s.equals("")){ return null; }
                else return ValEnum.get(Integer.parseInt(s));
            }
        };
    }

    /**
     * Creates a Serde that when serializing a list, separates the elements in it with a separator. When deserializing,
     * returns a list of objects of type T
     * @param serde serde capable of de/serializing objects of type T
     * @param separator character that will separate different objects' textual representation in string
     * @param <T> type of object that serde can de/serializing
     * @return the serde capable of de/serializing a list
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> list) {
                if(list.isEmpty()){ return ""; }
                else {
                    List<String> l = new ArrayList<>();
                    for (T t : list) {
                        l.add(serde.serialize(t));
                    }
                    return String.join(separator, l);
                }
            }

            @Override
            public List<T> deserialize(String string) {
                if (string.equals("")){return List.of();}
                else {
                    String[] noSeparator = string.split(Pattern.quote(separator), -1);
                    List<T> tList = new ArrayList<>();
                    for (String toDeserialize : noSeparator) {
                        tList.add(serde.deserialize(toDeserialize));
                    }
                    return tList;
                }
            }
        };
    }

    /**
     * Creates a Serde that when serializing a SortedBag, separates the elements in it with a separator. When deserializing,
     * returns a SortedBag of objects of type T
     * @param serde serde capable of de/serializing objects of type T
     * @param separator character that will separate different objects' textual representation in string
     * @param <T> type of object that serde can de/serializing
     * @return the serde capable of de/serializing a SortedBag
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator){
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> SB) {
                if (SB.isEmpty()){ return ""; }
                else {
                    List<String> l = new ArrayList<>();
                    for (T t : SB) {
                        l.add(serde.serialize(t));
                    }
                    return String.join(separator, l);
                }
            }

            @Override
            public SortedBag<T> deserialize(String string) {
                if (string.equals("")){return SortedBag.of();}
                else{
                    String[] noSeparator = string.split(Pattern.quote(separator), -1);
                    SortedBag.Builder<T> builder = new SortedBag.Builder<>();
                    for (String toDeserialize : noSeparator) {
                        builder.add(serde.deserialize(toDeserialize));
                    }
                    return builder.build();
                }
            }
        };
    }
}