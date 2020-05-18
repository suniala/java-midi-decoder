package fi.kapsi.kosmik.javamididecoder;

import javax.sound.midi.ShortMessage;

import java.util.Optional;

import static fi.kapsi.kosmik.javamididecoder.util.Util.get14bitValue;

public abstract class MidiShortM extends MidiM<ShortMessage> {
    private static final String[] keyNames =
            {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private static String keyName(int nKeyNumber) {
        if (nKeyNumber > 127) {
            throw new IllegalArgumentException("" + nKeyNumber);
        } else {
            var nNote = nKeyNumber % 12;
            var nOctave = nKeyNumber / 12;
            return keyNames[nNote] + (nOctave - 1);
        }
    }

    protected MidiShortM(ShortMessage m) {
        super(m);
    }

    public abstract <T> T accept(MidiShortMVisitor<T> visitor);

    public interface MidiShortMVisitor<T> {
        T visit(MidiNoteOnM m);

        T visit(MidiNoteOffM m);

        T visit(MidiPolyphonicKeyPressureM m);

        T visit(MidiControlChangeM m);

        T visit(MidiProgramChangeM m);

        T visit(MidiKeyPressureM m);

        T visit(MidiPitchWheelChangeM m);

        T visit(MidiMTCQuarterFrameM m);

        T visit(MidiSongPositionM m);

        T visit(MidiSongSelectM m);

        T visit(MidiOtherSystemMessageM m);

        T visit(MidiUnsupportedShortM m);
    }

    public static abstract class MidiChannelM extends MidiShortM {
        protected MidiChannelM(ShortMessage m) {
            super(m);
        }

        public int getChannel() {
            return m.getChannel() + 1;
        }
    }

    public abstract static class MidiNoteM extends MidiChannelM {
        public MidiNoteM(ShortMessage m) {
            super(m);
        }

        public abstract boolean isOn();

        public boolean isOff() {
            return !isOn();
        }

        public int getNote() {
            return m.getData1();
        }

        public String getNoteDescription() {
            return keyName(m.getData1());
        }

        public int getVelocity() {
            return m.getData2();
        }
    }

    public static class MidiNoteOnM extends MidiNoteM {
        public MidiNoteOnM(ShortMessage m) {
            super(m);
        }

        @Override
        public boolean isOn() {
            return true;
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiNoteOffM extends MidiNoteM {
        public MidiNoteOffM(ShortMessage m) {
            super(m);
        }

        @Override
        public boolean isOn() {
            return false;
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiPolyphonicKeyPressureM extends MidiChannelM {
        public MidiPolyphonicKeyPressureM(ShortMessage m) {
            super(m);
        }

        public String getKeyName() {
            return keyName(m.getData1());
        }

        public int getPressure() {
            return m.getData2();
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiControlChangeM extends MidiChannelM {
        public MidiControlChangeM(ShortMessage m) {
            super(m);
        }

        public int getControlChange() {
            return m.getData1();
        }

        public int getValue() {
            return m.getData2();
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiProgramChangeM extends MidiChannelM {
        public MidiProgramChangeM(ShortMessage m) {
            super(m);
        }

        public int getProgramChange() {
            return m.getData1();
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiKeyPressureM extends MidiChannelM {
        public MidiKeyPressureM(ShortMessage m) {
            super(m);
        }

        public String getKeyName() {
            return keyName(m.getData1());
        }

        public int getPressure() {
            return m.getData2();
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiPitchWheelChangeM extends MidiChannelM {
        public MidiPitchWheelChangeM(ShortMessage m) {
            super(m);
        }

        public int getValue() {
            return get14bitValue(m.getData1(), m.getData2());
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public abstract static class MidiSystemMessageM extends MidiShortM {
        protected static final String[] SYSTEM_MESSAGE_TEXT = {
                "System Exclusive (should not be in ShortMessage!)",
                "MTC Quarter Frame: ",
                "Song Position: ",
                "Song Select: ",
                "Undefined",
                "Undefined",
                "Tune Request",
                "End of SysEx (should not be in ShortMessage!)",
                "Timing clock",
                "Undefined",
                "Start",
                "Continue",
                "Stop",
                "Undefined",
                "Active Sensing",
                "System Reset"
        };

        protected MidiSystemMessageM(ShortMessage m) {
            super(m);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class MidiMTCQuarterFrameM extends MidiSystemMessageM {
        private static final String[] QUARTER_FRAME_MESSAGE_TEXT = {
                "frame count LS: ",
                "frame count MS: ",
                "seconds count LS: ",
                "seconds count MS: ",
                "minutes count LS: ",
                "minutes count MS: ",
                "hours count LS: ",
                "hours count MS: "
        };

        private static final String[] FRAME_TYPE_TEXT = {
                "24 frames/second",
                "25 frames/second",
                "30 frames/second (drop)",
                "30 frames/second (non-drop)",
        };

        private final String quarterFrameDescription;

        private final Optional<String> frameTypeDescription;

        public MidiMTCQuarterFrameM(ShortMessage m) {
            super(m);

            var nQType = (m.getData1() & 0x70) >> 4;
            var nQData = m.getData1() & 0x0F;
            if (nQType == 7) {
                nQData = nQData & 0x1;
            }
            quarterFrameDescription = QUARTER_FRAME_MESSAGE_TEXT[nQType] + nQData;
            frameTypeDescription = nQType == 7 ?
                    Optional.of(FRAME_TYPE_TEXT[(m.getData1() & 0x06) >> 1])
                    : Optional.empty();
        }

        public String getQuarterFrameDescription() {
            return this.quarterFrameDescription;
        }

        public Optional<String> getFrameTypeDescription() {
            return frameTypeDescription;
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiSongPositionM extends MidiSystemMessageM {
        public MidiSongPositionM(ShortMessage m) {
            super(m);
        }

        public int getValue() {
            return get14bitValue(m.getData1(), m.getData2());
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiSongSelectM extends MidiSystemMessageM {
        public MidiSongSelectM(ShortMessage m) {
            super(m);
        }

        public int getValue() {
            return m.getData1();
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiOtherSystemMessageM extends MidiSystemMessageM {
        public MidiOtherSystemMessageM(ShortMessage m) {
            super(m);
        }

        public String getDescription() {
            return SYSTEM_MESSAGE_TEXT[m.getChannel()];
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class MidiUnsupportedShortM extends MidiShortM {
        public MidiUnsupportedShortM(ShortMessage m) {
            super(m);
        }

        @Override
        public <T> T accept(MidiShortMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}
