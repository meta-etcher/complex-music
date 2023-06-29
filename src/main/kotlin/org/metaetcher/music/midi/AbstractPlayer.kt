package org.metaetcher.music.midi


import org.metaetcher.music.notes.MultiNote
import java.io.File
import javax.sound.midi.MidiEvent
import javax.sound.midi.MidiSystem
import javax.sound.midi.Synthesizer
import org.metaetcher.music.notes.Note

/**Base class for midi
 * To install high quality sounds:
 * "sudo apt-get install fluid-soundfont-gm"*/
abstract class AbstractPlayer(val synthesizer: Synthesizer) {
    var t0 = System.currentTimeMillis()
    val ticks = LongArray(16) { 0 } // time indexed by chan num

    // these are the core functions for midi events
    abstract fun register(e: Event)
    fun register(e: MidiEvent) = register { listOf(e) }

    // ** the add/accumulate functions below might eventually be handled a diff way and removed from this class **
    // specialized way to add Notes and increment the time
    fun add(tick: Long, chan: Int, notes: Iterable<Note>) {
        ticks[chan] = tick
        notes.forEach {
            if (!it.isRest())
                register(NoteEvent(ticks[chan], chan, it))
            ticks[chan] += it.duration.toLong()
        }
    }

    fun add(notes: Iterable<Note>) = add(ticks[0], 0, notes)
    fun add(note: Note, channel: Int = 0) = add(ticks[0], channel, listOf(note))

    open fun setInstr(chan: Int, instr: Int) = synthesizer.channels[chan].programChange(instr)

    open fun close() = synthesizer.close()

    companion object {
        fun initSynth(
            hqSounds: Boolean, hqSoundBank: String = "/usr/share/sounds/sf2/FluidR3_GM.sf2"
        ): Synthesizer =
            MidiSystem.getSynthesizer().apply {
                open()
                if (hqSounds) {
                    try {
                        // for example, to install sounds: "sudo apt-get install fluid-soundfont-gm"
                        println("loading instr from $hqSoundBank...")
                        unloadAllInstruments(this.defaultSoundbank)
                        loadAllInstruments(MidiSystem.getSoundbank(File(hqSoundBank)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                println("synth num voices: ${this.voiceStatus.size}")
                defaultSoundbank.instruments
                    .map { "(${it.patch.program} ${it.name})" }
                    .chunked(10)
                    .forEach { println(it) }
            }
    }
}
