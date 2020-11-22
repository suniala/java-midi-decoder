package fi.kapsi.kosmik.javamididecoder;

import javax.sound.midi.MetaMessage;

import static java.lang.String.format;

public abstract sealed class MidiMetaM extends MidiM<MetaMessage> {
    /**
     * A copy of the data to be used instead of {@link MetaMessage#getData()}, that
     * always returns a new copy.
     */
    protected final byte[] dataCopy;

    protected MidiMetaM(MetaMessage m) {
        super(m);
        dataCopy = m.getData();
    }

    public abstract <T> T accept(MidiMetaMVisitor<T> visitor);

    public interface MidiMetaMVisitor<T> {
        T visit(MidiSequenceNumberM m);

        T visit(MidiMetaTextM m);

        T visit(MidiUnsupportedMetaM m);

        T visit(MidiChannelPrefixM m);

        T visit(MidiEndOfTrackM m);

        T visit(MidiTempoM m);

        T visit(MidiSMTPEOffsetM m);

        T visit(MidiTimeSignatureM m);

        T visit(MidiKeySignatureM m);

        T visit(MidiSequencerSpecificMetaM m);
    }

    public static final class MidiSequenceNumberM extends MidiMetaM {
        public MidiSequenceNumberM(MetaMessage m) {
            super(m);
        }

        public int getNumber() {
            return ((dataCopy[0] & 0xFF) << 8) | (dataCopy[1] & 0xFF);
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiMetaTextM extends MidiMetaM {
        public enum MetaTextType {
            TextEvent("Text Event"),
            CopyrightNotice("Copyright Notice"),
            SequenceTrackName("Sequence/Track Name"),
            InstrumentName("Instrument Name"),
            Lyric("Lyric"),
            Marker("Marker"),
            CuePoint("Cue Point");

            public final String label;

            MetaTextType(String label) {
                this.label = label;
            }
        }

        private final MetaTextType type;

        public MidiMetaTextM(MetaMessage m, MetaTextType type) {
            super(m);
            this.type = type;
        }

        public MetaTextType getType() {
            return type;
        }

        public String getText() {
            return new String(dataCopy);
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiChannelPrefixM extends MidiMetaM {
        public MidiChannelPrefixM(MetaMessage m) {
            super(m);
        }

        public int getPrefix() {
            return dataCopy[0] & 0xFF;
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiEndOfTrackM extends MidiMetaM {
        public MidiEndOfTrackM(MetaMessage m) {
            super(m);
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiTempoM extends MidiMetaM {
        public MidiTempoM(MetaMessage m) {
            super(m);
        }

        public int getMicrosecondsPerBeat() {
            return ((dataCopy[0] & 0xFF) << 16)
                    | ((dataCopy[1] & 0xFF) << 8)
                    | (dataCopy[2] & 0xFF);
        }

        public float getBpm() {
            float bpm = microsecondsPerBeatToBpm(getMicrosecondsPerBeat());
            return Math.round(bpm * 100.0f) / 100.0f;
        }

        private static float microsecondsPerBeatToBpm(float value) {
            if (value <= 0) {
                value = 0.1f;
            }
            return 60000000.0f / value;
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiSMTPEOffsetM extends MidiMetaM {
        public MidiSMTPEOffsetM(MetaMessage m) {
            super(m);
        }

        public int getPart1() {
            return (dataCopy[0] & 0xFF);
        }

        public int getPart2() {
            return (dataCopy[1] & 0xFF);
        }

        public int getPart3() {
            return (dataCopy[2] & 0xFF);
        }

        public int getPart4() {
            return (dataCopy[3] & 0xFF);
        }

        public int getPart5() {
            return (dataCopy[4] & 0xFF);
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiTimeSignatureM extends MidiMetaM {
        public static final class TimeSignature {
            private final int beats;

            private final int unit;

            public TimeSignature(int beats, int unit) {
                this.beats = beats;
                this.unit = unit;
            }

            public int getBeats() {
                return beats;
            }

            public int getUnit() {
                return unit;
            }
        }

        public MidiTimeSignatureM(MetaMessage m) {
            super(m);
        }

        public TimeSignature getTimeSignature() {
            return new TimeSignature((dataCopy[0] & 0xFF), (1 << (dataCopy[1] & 0xFF)));
        }

        public int getMidiClocksPerMetronomeTick() {
            return (dataCopy[2] & 0xFF);
        }

        public int getDemisemiquaversPer24MidiClocks() {
            return (dataCopy[3] & 0xFF);
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiKeySignatureM extends MidiMetaM {
        private static final String[] keySignatureNames =
                {"Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#"};

        public MidiKeySignatureM(MetaMessage m) {
            super(m);
        }

        public String getDescription() {
            String type = (dataCopy[1] == 1) ? "minor" : "major";
            return format("%s %s", keySignatureNames[dataCopy[0] + 7], type);
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiSequencerSpecificMetaM extends MidiMetaM {
        public MidiSequencerSpecificMetaM(MetaMessage m) {
            super(m);
        }

        public byte[] getData() {
            // Return a copy of the data.
            return m.getData();
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static final class MidiUnsupportedMetaM extends MidiMetaM {
        public MidiUnsupportedMetaM(MetaMessage m) {
            super(m);
        }

        public byte[] getData() {
            // Return a copy of the data.
            return m.getData();
        }

        @Override
        public <T> T accept(MidiMetaMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}