package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiChannelPrefixM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiEndOfTrackM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiKeySignatureM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaTextM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiSMTPEOffsetM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiSequenceNumberM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiSequencerSpecificMetaM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiTempoM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiTimeSignatureM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiUnsupportedMetaM;

import static fi.kapsi.kosmik.javamididecoder.Util.getHexString;
import static java.lang.String.format;

public class DescribingMidiMetaMVisitor implements MidiMetaMVisitor<String> {
    @Override
    public String visit(MidiSequenceNumberM m) {
        return format("sequence number %d", m.getNumber());
    }

    @Override
    public String visit(MidiMetaTextM m) {
        return format("%s: %s", m.getType().label.toLowerCase(), m.getText());
    }

    @Override
    public String visit(MidiChannelPrefixM m) {
        return format("channel prefix %d", m.getPrefix());
    }

    @Override
    public String visit(MidiEndOfTrackM m) {
        return "end of track";
    }

    @Override
    public String visit(MidiTempoM m) {
        return format("tempo %.2f bpm, %s microseconds per beat", m.getBpm(), m.getMicrosecondsPerBeat());
    }

    @Override
    public String visit(MidiSMTPEOffsetM m) {
        return format("%d:%d:%d.%d.%d", m.getPart1(), m.getPart2(), m.getPart3(), m.getPart4(), m.getPart5());
    }

    @Override
    public String visit(MidiTimeSignatureM m) {
        return format("time signature %d/%d", m.getTimeSignature().beats(), m.getTimeSignature().unit());
    }

    @Override
    public String visit(MidiKeySignatureM m) {
        return format("key signature %s", m.getDescription());
    }

    @Override
    public String visit(MidiSequencerSpecificMetaM m) {
        return format("sequencer specific meta: %s", getHexString(m.getData()));
    }

    @Override
    public String visit(MidiUnsupportedMetaM m) {
        return format("unsupported meta message: [%s]", getHexString(m.getData()));
    }
}
