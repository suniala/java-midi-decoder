package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiControlChangeM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiKeyPressureM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiMTCQuarterFrameM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiNoteM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiNoteOffM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiNoteOnM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiOtherSystemMessageM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiPitchWheelChangeM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiPolyphonicKeyPressureM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiProgramChangeM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiShortMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiSongPositionM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiSongSelectM;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiUnsupportedShortM;

import static fi.kapsi.kosmik.javamididecoder.util.Util.getHexString;
import static java.lang.String.format;

public class DescribingMidiShortMVisitor implements MidiShortMVisitor<String> {
    private static String visitNote(MidiNoteM m) {
        return format("channel %d, note %d (%s) %s, velocity %d",
                m.getChannel(), m.getNote(), m.getNoteDescription(), m.isOn() ? "on" : "off", m.getVelocity());
    }

    @Override
    public String visit(MidiNoteOnM m) {
        return visitNote(m);
    }

    @Override
    public String visit(MidiNoteOffM m) {
        return visitNote(m);
    }

    @Override
    public String visit(MidiPolyphonicKeyPressureM m) {
        return format("channel %d, note %s, polyphonic key pressure %d",
                m.getChannel(), m.getKeyName(), m.getPressure());
    }

    @Override
    public String visit(MidiControlChangeM m) {
        return format("channel %d, control change %d, value %d",
                m.getChannel(), m.getControlChange(), m.getValue());
    }

    @Override
    public String visit(MidiProgramChangeM m) {
        return format("channel %d, program change %d",
                m.getChannel(), m.getProgramChange());
    }

    @Override
    public String visit(MidiKeyPressureM m) {
        return format("channel %d, note %s, key pressure %d",
                m.getChannel(), m.getKeyName(), m.getPressure());
    }

    @Override
    public String visit(MidiPitchWheelChangeM m) {
        return format("channel %d, pitch wheel change %d",
                m.getChannel(), m.getValue());
    }

    @Override
    public String visit(MidiMTCQuarterFrameM m) {
        return format("mtc quarter frame, quarter frame type \"%s\", frame type \"%s\"",
                m.getQuarterFrameDescription(), m.getFrameTypeDescription());
    }

    @Override
    public String visit(MidiSongPositionM m) {
        return format("song position %d",
                m.getValue());
    }

    @Override
    public String visit(MidiSongSelectM m) {
        return format("song select %d",
                m.getValue());
    }

    @Override
    public String visit(MidiOtherSystemMessageM m) {
        return format("%s",
                m.getDescription());
    }

    @Override
    public String visit(MidiUnsupportedShortM m) {
        return String.format("unsupported message: [%s]", getHexString(m.m));
    }
}
