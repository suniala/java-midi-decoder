# Java Midi Decoder

A work in progress... Basically I copied a Midi debugger of sorts called
[DumpReceiver](https://github.com/suniala/java-midi-decoder/commit/73390f23) and
am currently refactoring it into a more useful decoder library.

I don't have the means or time to test this comprehensively so there probably
are some errors. Also, some message types are not as structured as they could
be.

## Rationale

When handling time signatures with plain javax.sound.midi, for example, you have to do:
```
void printTimeSignature(MetaMessage m) {
    if (m.getType() == 0x58) {
        byte[] data = m.getData();
        int beats = (data[0] & 0xFF);
        int unit = (1 << (data[1] & 0xFF));
        System.out.println(beats + "/" + unit);
    }
}
```

With this library you simply do:
```
void printTimeSignature(MidiTimeSignatureM m) {
    System.out.println(m.getTimeSignature().getBeats() + "/" + m.getTimeSignature().getUnit());
}
```
