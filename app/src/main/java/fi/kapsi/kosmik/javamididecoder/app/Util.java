package fi.kapsi.kosmik.javamididecoder.app;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Util {
    public static <T> Stream<Tuple2<Integer, T>> zipWithIndex(T[] array) {
        return IntStream.range(0, array.length)
                .mapToObj(i -> new Tuple2<>(i, array[i]));
    }

    public static <T> Stream<Tuple2<Integer, T>> produceWithIndex(Function<Integer, T> producer) {
        return IntStream.range(0, Integer.MAX_VALUE)
                .mapToObj(i -> new Tuple2<>(i, producer.apply(i)));
    }

    public static class Tuple2<A, B> {
        public final A _1;

        public final B _2;

        public Tuple2(A _1, B _2) {
            this._1 = _1;
            this._2 = _2;
        }
    }
}
