package org.metaetcher.music.midi

import org.metaetcher.music.notes.MultiNote
import org.metaetcher.music.notes.Note
import javax.sound.midi.MidiEvent

/** a musical part, which is a collection of notes, chords, and midi events
 * @constructor
 * @param channel the midi channel of the part
 * @param instr the midi instrument of the part
 * @param ticksPerMeas the number of ticks per measure, which defaults to 16
 * */
class Part(private val channel: Int, private val instr: Int, var ticksPerMeas:Int=16) : Event {
    /** @constructor
     * @param channel the midi channel of the part
     * @param instr the midi instrument of the part
     * @param ticksPerMeas the number of ticks per measure, which defaults to 16
     * */
    constructor(channel: Int, instr:String, ticksPerMeas: Int=16) : this(channel, instr.gmPatch(), ticksPerMeas)

    var ticks = 0L
    var tickOffset = 0L // used to offset the time when converting to midi (treats part as movable module)
    val notes = mutableMapOf<Long, Note>().toSortedMap()
    val chords = mutableMapOf<Long, MultiNote>().toSortedMap()

    fun add(t: Long, n: Iterable<Note>) {
        ticks = t
        n.forEach { notes[ticks] = it ; ticks += it.duration.toLong() }
    }
    fun add(n: Iterable<Note>) = add(ticks, n)
    fun add(t: Long, n: Note) = add(t, listOf(n))
    fun add(n: Note) = add(ticks, n)
    fun add(tick: Long, mn: MultiNote, normalizeDur: Boolean = true) {
        val multiNote = if(normalizeDur) mn.normalizeDur() else mn
        ticks = tick
        chords[ticks] = multiNote
        ticks += multiNote.duration()
    }
    fun add(mn: MultiNote, normalizeDur: Boolean = true) = add(ticks, mn, normalizeDur)
    fun addAtMeas(meas: Int, n: Iterable<Note>) = add(meas*ticksPerMeas.toLong(), n)

    fun concurrentNotes(tick: Long) = notes.filter {
        (it.key <= tick && (it.key + it.value.duration > tick))
    }

    /** @return the midi events of the part */
    override fun midi(): Iterable<MidiEvent> {
        val events = mutableListOf(Event{ listOf(programChange(tickOffset+0, channel, instr)) })
        chords.forEach { events.add(ChordEvent(tickOffset+it.key, channel, it.value)) }
        val n = notes.filterNot { it.value.isRest() }
        n.forEach { events.add(NoteEvent(tickOffset+it.key, channel, it.value)) }
        return events.reduce { a, b -> a + b }.midi()
    }
}
