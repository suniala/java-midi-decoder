package fi.kapsi.kosmik.javamididecoder;

import javax.sound.midi.SysexMessage;
import java.util.Optional;

import static fi.kapsi.kosmik.javamididecoder.Util.getHexString;

public abstract class MidiSysexM extends MidiM<SysexMessage> {
    protected MidiSysexM(SysexMessage m) {
        super(m);
    }

    public abstract <T> T accept(MidiSysexMVisitor<T> visitor);

    public interface MidiSysexMVisitor<T> {
        T visit(MidiDescribedSysexM m);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class MidiDescribedSysexM extends MidiSysexM {
        private final Optional<String> description;

        public MidiDescribedSysexM(SysexMessage m) {
            super(m);

            var abData = m.getData();
            String description = null;
            if (m.getStatus() == SysexMessage.SYSTEM_EXCLUSIVE) {
                description = "F0" + getHexString(abData);
            } else if (m.getStatus() == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
                description = "(continued sysex message) F7" + getHexString(abData);
            }
            this.description = Optional.ofNullable(description);
        }

        public Optional<String> getDescription() {
            return description;
        }

        @Override
        public <T> T accept(MidiSysexMVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}