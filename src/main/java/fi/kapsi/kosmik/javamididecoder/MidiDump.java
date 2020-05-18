package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiM.MidiMVisitor;
import fi.kapsi.kosmik.javamididecoder.util.Util;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

import static fi.kapsi.kosmik.javamididecoder.MidiDecoder.decodeMessage;

public class MidiDump {
    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        if (args == null || args.length < 1) {
            System.err.println("Please give a path to a midi file.");
        } else {
            var sequence = MidiSystem.getSequence(new File(args[0]));
            Util.zipWithIndex(sequence.getTracks())
                    .forEach(it -> {
                        System.out.println("track: " + it._1);
                        Util.produceWithIndex(it._2::get)
                                .limit(it._2.size() - 1)
                                .forEach(im -> {
                                    String formattedMessage = format(im._2.getMessage());
                                    System.out.println("  message " + im._1 + ": " + formattedMessage);
                                });
                    });
        }
    }

    private static final MidiMVisitor<String> SHORT_M_DUMP_VISITOR = new DescribingMidiMVisitor();

    private static String format(MidiMessage message) {
        var decoded = decodeMessage(message);
        return decoded.accept(SHORT_M_DUMP_VISITOR);
    }
}
