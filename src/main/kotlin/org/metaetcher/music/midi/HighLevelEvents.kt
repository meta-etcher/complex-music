package org.metaetcher.music.midi

import org.metaetcher.music.notes.MultiNote
import org.metaetcher.music.notes.Note
import javax.sound.midi.MidiEvent

/**A high level midi event composed of other events */
fun interface Event {
    fun midi() : Iterable<MidiEvent>
    fun duration() = (midi().maxOf { it.tick } - midi().minOf { it.tick }).toInt()
    operator fun plus(e: Event) = Event { this.midi() + e.midi() }
}
//fun interface Event { abstract fun midi(track: Track) }

class NoteEvent(val tick: Long, val chan: Int, val note: Note) : Event {
    constructor(tick: Long, chan: Int, p:Int, d:Int) : this(tick, chan, Note(p, d, 64))
    constructor(tick: Long, chan: Int, p:Int, d:Int, v:Int) : this(tick, chan, Note(p, d, v))
    override fun midi(): Iterable<MidiEvent> = listOf(
        noteOn(tick, chan, note.pitch, note.velocity),
        noteOff(tick + note.duration, chan, note.pitch, note.velocity)
    )
}

// a simple chord event
class ChordEvent(private val tick: Long, private val chan: Int, private val notes: Iterable<Note>) : Event {
    constructor(tick: Long, chan: Int, mn: MultiNote) : this(tick, chan, mn.notes)
    constructor(tick: Long, chan: Int, p: Iterable<Int>, d:Int, v:Int) : this(tick, chan, p.map{Note(it, d, v) })
    constructor(tick: Long, chan: Int, p: Iterable<Int>, d:Int) : this(tick, chan, p.map{Note(it, d, 64) })
    override fun midi(): Iterable<MidiEvent> =
        notes.map { NoteEvent(tick, chan, it).midi() }.reduce{a, b -> a + b}
}

