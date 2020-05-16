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
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiDescribedSysexM;
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiSysexMVisitor;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public class MidiDecoder {
    public static long seByteCount = 0;
    public static long seCount = 0;

    private static final String[] sm_astrKeyNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private static final String[] sm_astrKeySignatures = {"Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#"};

    public static final MidiShortMVisitor<String> SHORT_M_DUMP_VISITOR = new DescribingMidiShortMVisitor();

    private static final MidiSysexMVisitor<String> SYSEX_M_DUMP_VISITOR = new DescribingMidiSysexMVisitor();

    public String decodeMessage(MidiMessage message) {
        String strMessage;
        if (message instanceof ShortMessage) {
            var m = decodeMessage((ShortMessage) message);
            strMessage = m.accept(SHORT_M_DUMP_VISITOR);
        } else if (message instanceof SysexMessage) {
            var m = decodeMessage((SysexMessage) message);
            strMessage = m.accept(SYSEX_M_DUMP_VISITOR);
        } else if (message instanceof MetaMessage) {
            strMessage = decodeMessage((MetaMessage) message);
        } else {
            strMessage = "unknown message type";
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

    public String decodeMessage(MetaMessage message) {
        byte[] abData = message.getData();
        String strMessage;
        switch (message.getType()) {
            case 0:
                int nSequenceNumber = ((abData[0] & 0xFF) << 8) | (abData[1] & 0xFF);
                strMessage = "Sequence Number: " + nSequenceNumber;
                break;

            case 1:
                String strText = new String(abData);
                strMessage = "Text Event: " + strText;
                break;

            case 2:
                String strCopyrightText = new String(abData);
                strMessage = "Copyright Notice: " + strCopyrightText;
                break;

            case 3:
                String strTrackName = new String(abData);
                strMessage = "Sequence/Track Name: " + strTrackName;
                break;

            case 4:
                String strInstrumentName = new String(abData);
                strMessage = "Instrument Name: " + strInstrumentName;
                break;

            case 5:
                String strLyrics = new String(abData);
                strMessage = "Lyric: " + strLyrics;
                break;

            case 6:
                String strMarkerText = new String(abData);
                strMessage = "Marker: " + strMarkerText;
                break;

            case 7:
                String strCuePointText = new String(abData);
                strMessage = "Cue Point: " + strCuePointText;
                break;

            case 0x20:
                int nChannelPrefix = abData[0] & 0xFF;
                strMessage = "MIDI Channel Prefix: " + nChannelPrefix;
                break;

            case 0x2F:
                strMessage = "End of Track";
                break;

            case 0x51:
                int nTempo = ((abData[0] & 0xFF) << 16)
                        | ((abData[1] & 0xFF) << 8)
                        | (abData[2] & 0xFF);           // tempo in microseconds per beat
                float bpm = convertTempo(nTempo);
                // truncate it to 2 digits after dot
                bpm = Math.round(bpm * 100.0f) / 100.0f;
                strMessage = "Set Tempo: " + bpm + " bpm";
                break;

            case 0x54:
                strMessage = "SMTPE Offset: "
                        + (abData[0] & 0xFF) + ":"
                        + (abData[1] & 0xFF) + ":"
                        + (abData[2] & 0xFF) + "."
                        + (abData[3] & 0xFF) + "."
                        + (abData[4] & 0xFF);
                break;

            case 0x58:
                strMessage = "Time Signature: "
                        + (abData[0] & 0xFF) + "/" + (1 << (abData[1] & 0xFF))
                        + ", MIDI clocks per metronome tick: " + (abData[2] & 0xFF)
                        + ", 1/32 per 24 MIDI clocks: " + (abData[3] & 0xFF);
                break;

            case 0x59:
                String strGender = (abData[1] == 1) ? "minor" : "major";
                strMessage = "Key Signature: " + sm_astrKeySignatures[abData[0] + 7] + " " + strGender;
                break;

            case 0x7F:
                // TODO: decode vendor code, dump data in rows
                String strDataDump = getHexString(abData);
                strMessage = "Sequencer-Specific Meta event: " + strDataDump;
                break;

            default:
                String strUnknownDump = getHexString(abData);
                strMessage = "unknown Meta event: " + strUnknownDump;
                break;

        }
        return strMessage;
    }

    public static String getKeyName(int nKeyNumber) {
        if (nKeyNumber > 127) {
            throw new IllegalArgumentException("" + nKeyNumber);
        } else {
            int nNote = nKeyNumber % 12;
            int nOctave = nKeyNumber / 12;
            return sm_astrKeyNames[nNote] + (nOctave - 1);
        }
    }

    public static int get14bitValue(int nLowerPart, int nHigherPart) {
        return (nLowerPart & 0x7F) | ((nHigherPart & 0x7F) << 7);
    }

    // convert from microseconds per quarter note to beats per minute and vice versa
    private static float convertTempo(float value) {
        if (value <= 0) {
            value = 0.1f;
        }
        return 60000000.0f / value;
    }

    private static final char[] hexDigits =
            {'0', '1', '2', '3',
                    '4', '5', '6', '7',
                    '8', '9', 'A', 'B',
                    'C', 'D', 'E', 'F'};

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

    private static String intToHex(int i) {
        return "" + hexDigits[(i & 0xF0) >> 4]
                + hexDigits[i & 0x0F];
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
}
