package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiShortMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiSysexMVisitor;

import javax.sound.midi.MidiMessage;

public abstract sealed class MidiM<T extends MidiMessage>
        permits MidiSysexM, MidiShortM, MidiMetaM {
    protected final T m;

    public MidiM(T m) {
        this.m = m;
    }

    public T getRawMessage() {
        return m;
    }

    public <V> V accept(MidiMVisitor<V> visitor) {
        if (this instanceof MidiShortM sm) {
            return (sm).accept(visitor.gShortMVisitor());
        } else if (this instanceof MidiSysexM sm) {
            return (sm).accept(visitor.getSysexMVisitor());
        } else if (this instanceof MidiMetaM mm) {
            return (mm).accept(visitor.getMetaMVisitor());
        }
        throw new IllegalArgumentException("Not supported: " + m.getClass());
    }

    public interface MidiMVisitor<V> {
        MidiShortMVisitor<V> gShortMVisitor();

        MidiSysexMVisitor<V> getSysexMVisitor();

        MidiMetaMVisitor<V> getMetaMVisitor();
    }
}
