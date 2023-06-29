package org.metaetcher.music.notes

/** A musical note, with pitch, duration, and velocity.
 * Pitch is an integer in the range [21, 128], with 60 being middle C.
 * Duration is an integer
 * Velocity is an integer in the range [0, 127], with 64 being the default.
 * A note is treated as a rest if its pitch is very high (which allows rests to survive transposition).
 */
data class Note(val pitch: Int, val duration: Int, val velocity: Int = 64) {
    fun isRest() = pitch.isRest()

    companion object {
        const val R = 20000                 // a constant for pitch that defines a rest
        const val REST_TEST = 2000  // treat anything way out of range as a rest, allowing for transpositions, etc
        const val PITCH_HIGH = 128
        const val PITCH_LOW = 21
        fun rest(dur: Int) = Note(R, dur)
    }
}

/** extension function to test if an Int is to be treated as a Rest, e.g. greater than 2000.
 *  This means a rest can be transposed along with some collection of Notes and will still be a rest.
 */
fun Int.isRest() = this > Note.REST_TEST


/** Multiple notes played simultaneously, for example a chord.
 * A [MultiNote] is a collection of [Note]s.
 * a collection of notes can be used to create the [MultiNote].
 * p can be used to specify the pitches.
 * d can be used to specify the duration of the entire chord.
 * v can be used to specify the velocity of the entire chord.
 */
data class MultiNote(val notes: Iterable<Note>) {
    constructor(p: Iterable<Int>, d: Int, v: Int) : this(p.map { Note(it, d, v) })
    constructor(p: Iterable<Int>, d: Int) : this(p.map { Note(it, d, 64) })
    constructor(vararg n: Note) : this(n.toList())

    fun duration(): Int = notes.maxOf { it.duration }
    /** The normalized duration is the maximum duration of the notes. */
    fun normalizeDur(): MultiNote =
        MultiNote(notes.map { Note (it.pitch, duration(), it.velocity) })
}
