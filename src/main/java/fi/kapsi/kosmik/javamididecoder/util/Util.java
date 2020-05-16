package fi.kapsi.kosmik.javamididecoder.util;

import javax.sound.midi.ShortMessage;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Util {
    private static final char[] hexDigits =
            {'0', '1', '2', '3',
                    '4', '5', '6', '7',
                    '8', '9', 'A', 'B',
                    'C', 'D', 'E', 'F'};

    public static <T> Stream<Tuple2<Integer, T>> zipWithIndex(T[] array) {
        return IntStream.range(0, array.length)
                .mapToObj(i -> new Tuple2<>(i, array[i]));
    }

    public static <T> Stream<Tuple2<Integer, T>> produceWithIndex(Function<Integer, T> producer) {
        return IntStream.range(0, Integer.MAX_VALUE)
                .mapToObj(i -> new Tuple2<>(i, producer.apply(i)));
    }

    public static int get14bitValue(int nLowerPart, int nHigherPart) {
        return (nLowerPart & 0x7F) | ((nHigherPart & 0x7F) << 7);
    }

    public static String getHexString(byte[] aByte) {
        StringBuffer sbuf = new StringBuffer(aByte.length * 3 + 2);
        for (byte b : aByte) {
            sbuf.append(' ');
            sbuf.append(hexDigits[(b & 0xF0) >> 4]);
            sbuf.append(hexDigits[b & 0x0F]);
			/*byte	bhigh = (byte) ((aByte[i] &  0xf0) >> 4);
			sbuf.append((char) (bhigh > 9 ? bhigh + 'A' - 10: bhigh + '0'));
			byte	blow = (byte) (aByte[i] & 0x0f);
			sbuf.append((char) (blow > 9 ? blow + 'A' - 10: blow + '0'));*/
        }
        return new String(sbuf);
    }

    public static String getHexString(ShortMessage sm) {
        // bug in J2SDK 1.4.1
        // return getHexString(sm.getMessage());
        int status = sm.getStatus();
        String res = intToHex(sm.getStatus());
        // if one-byte message, return
        switch (status) {
            case 0xF6:            // Tune Request
            case 0xF7:            // EOX
                // System real-time messages
            case 0xF8:            // Timing Clock
            case 0xF9:            // Undefined
            case 0xFA:            // Start
            case 0xFB:            // Continue
            case 0xFC:            // Stop
            case 0xFD:            // Undefined
            case 0xFE:            // Active Sensing
            case 0xFF:
                return res;
        }
        res += ' ' + intToHex(sm.getData1());
        // if 2-byte message, return
        switch (status) {
            case 0xF1:            // MTC Quarter Frame
            case 0xF3:            // Song Select
                return res;
        }
        switch (sm.getCommand()) {
            case 0xC0:
            case 0xD0:
                return res;
        }
        // 3-byte messages left
        res += ' ' + intToHex(sm.getData2());
        return res;
    }

    private static String intToHex(int i) {
        return "" + hexDigits[(i & 0xF0) >> 4]
                + hexDigits[i & 0x0F];
    }
}
