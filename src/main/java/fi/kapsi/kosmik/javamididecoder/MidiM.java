package fi.kapsi.kosmik.javamididecoder;

import javax.sound.midi.MidiMessage;

public abstract class MidiM<T extends MidiMessage> {
    protected final T m;

    public MidiM(T m) {
        this.m = m;
    }

    public T rawMessage() {
        return m;
    }
}
