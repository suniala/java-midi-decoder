package fi.kapsi.kosmik.javamididecoder;

import fi.kapsi.kosmik.javamididecoder.MidiM.MidiMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiMetaM.MidiMetaMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiShortM.MidiShortMVisitor;
import fi.kapsi.kosmik.javamididecoder.MidiSysexM.MidiSysexMVisitor;

public class DescribingMidiMVisitor implements MidiMVisitor<String> {
    private static final MidiShortMVisitor<String> SHORT_M_DUMP_VISITOR = new DescribingMidiShortMVisitor();

    private static final MidiSysexMVisitor<String> SYSEX_M_DUMP_VISITOR = new DescribingMidiSysexMVisitor();

    private static final MidiMetaMVisitor<String> META_M_DUMP_VISITOR = new DescribingMidiMetaMVisitor();

    @Override
    public MidiShortMVisitor<String> gShortMVisitor() {
        return SHORT_M_DUMP_VISITOR;
    }

    @Override
    public MidiSysexMVisitor<String> getSysexMVisitor() {
        return SYSEX_M_DUMP_VISITOR;
    }

    @Override
    public MidiMetaMVisitor<String> getMetaMVisitor() {
        return META_M_DUMP_VISITOR;
    }
}
