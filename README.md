# Java Midi Decoder

This library makes working with javax.sound.midi events easier.

I don't have the means or time to test this comprehensively so there probably
are some errors. Also, some message types are not as structured as they could
be. Having said that, I do use this library in [midi-tuutti](https://github.com/suniala/midi-tuutti)

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

## Related Projects
* https://github.com/suniala/midi-tuutti uses this library
* http://www.jfugue.org enables "Music Programming for Java™ and JVM Languages"
