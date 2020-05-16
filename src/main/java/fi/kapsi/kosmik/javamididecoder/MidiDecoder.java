package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiChannelPrefixM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiEndOfTrackM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiKeySignatureM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaTextM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaTextM.MetaTextType;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiSMTPEOffsetM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiSequenceNumberM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiSequencerSpecificMetaM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiTempoM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiTimeSignatureM;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiUnsupportedMetaM;
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
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiDescribedSysexM;
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiSysexMVisitor;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public class MidiDecoder {
    public static final MidiShortMVisitor<String> SHORT_M_DUMP_VISITOR = new DescribingMidiShortMVisitor();

    private static final MidiSysexMVisitor<String> SYSEX_M_DUMP_VISITOR = new DescribingMidiSysexMVisitor();

    private static final MidiMetaMVisitor<String> META_M_DUMP_VISITOR = new DescribingMidiMetaMVisitor();

    public String decodeMessage(MidiMessage message) {
        String strMessage;
        if (message instanceof ShortMessage) {
            var m = decodeMessage((ShortMessage) message);
            strMessage = m.accept(SHORT_M_DUMP_VISITOR);
        } else if (message instanceof SysexMessage) {
            var m = decodeMessage((SysexMessage) message);
            strMessage = m.accept(SYSEX_M_DUMP_VISITOR);
        } else if (message instanceof MetaMessage) {
            var m = decodeMessage((MetaMessage) message);
            strMessage = m.accept(META_M_DUMP_VISITOR);
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
        }
        return strMessage;
    }

    public MidiShortM decodeMessage(ShortMessage message) {
        switch (message.getCommand()) {
            case 0x80:
                return new MidiNoteM(message, MidiNoteM.OnOff.off);
            case 0x90:
                return new MidiNoteM(message, MidiNoteM.OnOff.on);
            case 0xa0:
                return new MidiPolyphonicKeyPressureM(message);
            case 0xb0:
                return new MidiControlChangeM(message);
            case 0xc0:
                return new MidiProgramChangeM(message);
            case 0xd0:
                return new MidiKeyPressureM(message);
            case 0xe0:
                return new MidiPitchWheelChangeM(message);
            case 0xF0:
                switch (message.getChannel()) {
                    case 0x1:
                        return new MidiMTCQuarterFrameM(message);
                    case 0x2:
                        return new MidiSongPositionM(message);
                    case 0x3:
                        return new MidiSongSelectM(message);
                    default:
                        return new MidiOtherSystemMessageM(message);
                }
            default:
                return new MidiUnsupportedShortM(message);
        }
    }

    public MidiSysexM decodeMessage(SysexMessage message) {
        return new MidiDescribedSysexM(message);
    }

    public MidiMetaM decodeMessage(MetaMessage message) {
        switch (message.getType()) {
            case 0:
                return new MidiSequenceNumberM(message);
            case 1:
                return new MidiMetaTextM(message, MetaTextType.TextEvent);
            case 2:
                return new MidiMetaTextM(message, MetaTextType.CopyrightNotice);
            case 3:
                return new MidiMetaTextM(message, MetaTextType.SequenceTrackName);
            case 4:
                return new MidiMetaTextM(message, MetaTextType.InstrumentName);
            case 5:
                return new MidiMetaTextM(message, MetaTextType.Lyric);
            case 6:
                return new MidiMetaTextM(message, MetaTextType.Marker);
            case 7:
                return new MidiMetaTextM(message, MetaTextType.CuePoint);
            case 0x20:
                return new MidiChannelPrefixM(message);
            case 0x2F:
                return new MidiEndOfTrackM(message);
            case 0x51:
                return new MidiTempoM(message);
            case 0x54:
                return new MidiSMTPEOffsetM(message);
            case 0x58:
                return new MidiTimeSignatureM(message);
            case 0x59:
                return new MidiKeySignatureM(message);
            case 0x7F:
                return new MidiSequencerSpecificMetaM(message);
            default:
                return new MidiUnsupportedMetaM(message);
        }
    }
}
