package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiControlChangeM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiKeyPressureM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiMTCQuarterFrameM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiNoteM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiOtherSystemMessageM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiPitchWheelChangeM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiPolyphonicKeyPressureM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiProgramChangeM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiShortMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiSongPositionM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiSongSelectM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiUnsupportedShortM;

import static fi.kapsi.kosmik.javamididecoder.MidiDecoder.getHexString;
import static java.lang.String.format;

public class StringMidiShortMVisitor implements MidiShortMVisitor<String> {
    @Override
    public String visit(MidiNoteM m) {
        return format("channel %d, note %s %s, velocity %d",
                m.channel(), m.keyName(), m.onOff, m.velocity());
    }

    @Override
    public String visit(MidiPolyphonicKeyPressureM m) {
        return format("channel %d, note %s, polyphonic key pressure %d",
                m.channel(), m.keyName(), m.pressure());
    }

    @Override
    public String visit(MidiControlChangeM m) {
        return format("channel %d, control change %d, value %d",
                m.channel(), m.controlChange(), m.value());
    }

    @Override
    public String visit(MidiProgramChangeM m) {
        return format("channel %d, program change %d",
                m.channel(), m.programChange());
    }

    @Override
    public String visit(MidiKeyPressureM m) {
        return format("channel %d, note %s, key pressure %d",
                m.channel(), m.keyName(), m.pressure());
    }

    @Override
    public String visit(MidiPitchWheelChangeM m) {
        return format("channel %d, pitch wheel change %d",
                m.channel(), m.value());
    }

    @Override
    public String visit(MidiMTCQuarterFrameM m) {
        return format("mtc quarter frame, quarter frame type \"%s\", frame type \"%s\"",
                m.quarterFrameDescription(), m.frameTypeDescription());
    }

    @Override
    public String visit(MidiSongPositionM m) {
        return format("song position %d",
                m.value());
    }

    @Override
    public String visit(MidiSongSelectM m) {
        return format("song select %d",
                m.value());
    }

    @Override
    public String visit(MidiOtherSystemMessageM m) {
        return format("%s",
                m.description());
    }

    @Override
    public String visit(MidiUnsupportedShortM m) {
        return String.format("unsupported message: [%s]", getHexString(m.m));
    }
}
