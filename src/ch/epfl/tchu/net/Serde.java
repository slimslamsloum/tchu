package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface Serde <T>{

    String serialize(T t);

    T deserialize(String string);

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

    static <T> Serde<T> oneOf(List<T> ValEnum){
        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return String.valueOf(ValEnum.indexOf(t)); }

            @Override
            public T deserialize(String s) {
                return ValEnum.get(Integer.parseInt(s));
            }
        };
    }


    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> list) {
                List<String> l = new ArrayList<>();
                for (T t : list){ l.add(serde.serialize(t)); }
                return String.join(separator, l);
            }

            @Override
            public List<T> deserialize(String string) {
                String[] noSeparator = string.split(separator, -1);
                List<T> tList = new ArrayList<>();
                for (String toDeserialize : noSeparator) {
                    tList.add(serde.deserialize(toDeserialize));
                }
                return tList;
            }
        };
    }


    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator){
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> SB) {
                List<String> l = new ArrayList<>();
                for (T t : SB){ l.add(serde.serialize(t)); }
                return String.join(separator, l);
            }

            @Override
            public SortedBag<T> deserialize(String string) {
                String[] noSeparator = string.split(separator, -1);
                SortedBag.Builder<T> builder = new SortedBag.Builder<>();
                for (String toDeserialize: noSeparator){
                    builder.add(serde.deserialize(toDeserialize));
                }
                return builder.build();
            }
        };
    }

}