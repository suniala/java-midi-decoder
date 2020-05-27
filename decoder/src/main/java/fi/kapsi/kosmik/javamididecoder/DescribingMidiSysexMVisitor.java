package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiDescribedSysexM;
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiSysexMVisitor;

import static java.lang.String.format;

public class DescribingMidiSysexMVisitor implements MidiSysexMVisitor<String> {
    @Override
    public String visit(MidiDescribedSysexM m) {
        return format("sysex: %s", m.getDescription().orElse("no description available"));
    }
}
