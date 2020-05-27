package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiShortMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiSysexMVisitor;

import javax.sound.midi.MidiMessage;

public abstract class MidiM<T extends MidiMessage> {
    protected final T m;

    public MidiM(T m) {
        this.m = m;
    }

    public T getRawMessage() {
        return m;
    }

    public <V> V accept(MidiMVisitor<V> visitor) {
        if (this instanceof MidiShortM) {
            return ((MidiShortM) this).accept(visitor.gShortMVisitor());
        } else if (this instanceof MidiSysexM) {
            return ((MidiSysexM) this).accept(visitor.getSysexMVisitor());
        } else if (this instanceof MidiMetaM) {
            return ((MidiMetaM) this).accept(visitor.getMetaMVisitor());
        }
        throw new IllegalArgumentException("Not supported: " + m.getClass());
    }

    public interface MidiMVisitor<V> {
        MidiShortMVisitor<V> gShortMVisitor();

        MidiSysexMVisitor<V> getSysexMVisitor();

        MidiMetaMVisitor<V> getMetaMVisitor();
    }
}
