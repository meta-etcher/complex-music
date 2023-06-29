package org.metaetcher.music.midi

import java.io.File
import javax.sound.midi.*
import javax.sound.midi.Sequence.PPQ

/**Used to play as a midi sequence and/or write to type 0 midi file.*/
class SequencePlayer(synthesizer: Synthesizer, ppq: Int = 4) : AbstractPlayer(synthesizer) {
    /** @constructor
     * @param hqSounds whether to use high quality sounds
     * @param hqSoundBank the path to the high quality sound bank
     * @param ppq the number of ticks per quarter note, which defaults to 4
     * */
    constructor(
        hqSounds: Boolean = false, hqSoundBank: String = "/usr/share/sounds/sf2/FluidR3_GM.sf2", ppq: Int = 4
    ) : this(initSynth(hqSounds, hqSoundBank), ppq)

    private val sequencer = initSequencer()
    private val seq = javax.sound.midi.Sequence(PPQ, ppq)
    private val track = seq.createTrack()!!

    override fun register(e: Event) {
        e.midi().forEach { track.add(it) }
    }

    private fun initSequencer(): javax.sound.midi.Sequencer {
        val s = MidiSystem.getSequencer(false)
        s.open()
        s.transmitter.receiver = synthesizer.receiver
        return s
    }

    override fun setInstr(chan: Int, instr: Int) {
        println("PROGRAM_CHANGE $instr chan:$chan tick:${ticks[chan]}")
        track.add(midiEvent(ticks[chan], chan, ShortMessage.PROGRAM_CHANGE, instr, 0))
    }

    fun play(bpm: Int = 120) {
        sequencer.sequence = seq
        sequencer.tempoInBPM = bpm.toFloat()
        sequencer.start()
        while (sequencer.isRunning) Thread.sleep(200)
        Thread.sleep(200)
    }

    fun write(file: String, bpm: Int = 155) {
        track.add(tempo(0, bpm))
        MidiSystem.write(seq, 0, File(file))  // type 0 midi file
    }

    override fun close() {
        sequencer.close()
        synthesizer.close()
    }
}
