package org.metaetcher.music.midi

import javax.sound.midi.InvalidMidiDataException
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiEvent
import javax.sound.midi.ShortMessage

// low level events

fun noteOn(tick: Long, channel: Int, pitch: Int, velocity: Int) =
    midiEvent(tick, channel, ShortMessage.NOTE_ON, pitch, velocity)

fun noteOff(tick: Long, channel: Int, pitch: Int, velocity: Int) =
    midiEvent(tick, channel, ShortMessage.NOTE_OFF, pitch, velocity)

fun programChange(tick: Long, chan: Int, patch: Int) =
    midiEvent(tick, chan, ShortMessage.PROGRAM_CHANGE, patch, 0)

fun expression(tick: Long, chan: Int, volume: Int) =  // use during playback for volume
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 11, volume)

fun volume(tick: Long, chan: Int, volume: Int) =  // use before playback
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 7, volume)

fun pan(tick: Long, chan: Int, pan: Int) =  // left 0, 127 right
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 10, pan)

fun balance(tick: Long, chan: Int, pan: Int) =  // left 0, 127 right, for stereo patches
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 8, pan)

fun reverb(tick: Long, chan: Int, rev: Int) =
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 91, rev)

fun portamentoOnOff(tick: Long, chan: Int, onOff: Int) =
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 65, onOff)

fun portamentoTime(tick: Long, chan: Int, time: Int) =
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 5, time)

fun sustain(tick: Long, chan: Int, sus: Int) =
    midiEvent(tick, chan, ShortMessage.CONTROL_CHANGE, 64, sus)

fun timeSignature(tick: Long, numerator: Int, denominator: Int) =
    metaEvent(0x58, byteArrayOf(numerator.toByte(), denominator.toByte(), 24, 8), tick)

fun tempo(tick: Long, bpm: Int): MidiEvent {
    val mpqn: Long =  60000000 / bpm.toLong()
    val array = byteArrayOf(0, 0, 0)
    for (i in 0..2) {
        val shift = (3 - 1 - i) * 8
        array[i] = (mpqn shr shift).toByte()
    }
    return metaEvent(81, array, tick)
}

fun text(tick: Long, text:String) =
        metaEvent(0x01, text.toByteArray(), tick)

fun keySignatureC() =
   metaEvent(0x59, ByteArray(2) { 0 }, 0)

fun midiEvent(tick: Long, chan: Int, cmd: Int, data1: Int, data2: Int): MidiEvent {
    val message = ShortMessage()
    try {
        message.setMessage(cmd, chan, data1, data2)
    } catch (e: InvalidMidiDataException) {
        e.printStackTrace()
    }
    return MidiEvent(message, tick)
}

fun metaEvent(type: Int, bytes: ByteArray, tick: Long): MidiEvent {
    val message = MetaMessage()
    try {
        message.setMessage(type, bytes, bytes.size)
    } catch (e: InvalidMidiDataException) {
        e.printStackTrace()
    }
    return MidiEvent(message, tick)
}
