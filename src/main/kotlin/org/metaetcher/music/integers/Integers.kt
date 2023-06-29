package org.metaetcher.music.integers

import org.metaetcher.music.notes.Note
import org.metaetcher.music.notes.isRest
import java.util.*

/**Encoding of musical information.*/
typealias Iter = Iterator<Int>

/** Index into (and outside) the Collection supporting positive and negative wraparound,
 * as well as optional octave aware movement.
 * @param idx the index to wrap
 * @param octaves if true, the index is wrapped in an octave aware way (defaults to false)
 * @param root the root to add to each result (defaults to 0)
 * @return the wrapped value
 */
fun Collection<Int>.wrapped(idx: Int, octaves: Boolean = false, root: Int = 0): Int {
    var newIdx = idx % size     // keeps index within range
    var octave = idx / size     // num times wrapped
    if (idx < 0 && newIdx < 0)
        octave -= 1             // strange adjustments for negative but it works
    if (newIdx < 0) newIdx += size
    return if (octaves) 12 * octave + root + elementAt(newIdx)
    else root + elementAt(newIdx)
}

/** Index into (and outside) the Iterable, supporting positive and negative wraparound,
 * as well as optional octave aware movement.
 * @param idx the index to wrap
 * @param octave if true, the index is wrapped in an octave aware way (defaults to false)
 * @param root the root to add to each result (defaults to 0)
 * @return the wrapped value
 */
fun Iterable<Int>.wrapped(idx: Int, octave: Boolean = false, root: Int = 0) =
    if (this is Collection) wrapped(idx, octave, root)
    else toList().wrapped(idx, octave, root)

/** relative indices for each provided step
 * @param skips the series of steps to take
 * @param octaves if true, the index is wrapped in an octave aware way (defaults to false)
 */
fun Iterable<Int>.skipStep(skips: Iterable<Int>, octaves:Boolean=true) : Iterable<Int> {
    var accumulate = 0
    val absIdxs = skips.map { if (it.isRest()) Note.R else { accumulate += it; accumulate } }  // build absolute indices
    return absIdxs.map { if (it.isRest()) Note.R else this.wrapped(it, octaves) }
}
/** relative indices for each provided step
 * @param skips the series of steps to take
 * @param octaves if true, the index is wrapped in an octave aware way (defaults to false)
 */
fun Iterable<Int>.skipStep(vararg skips:Int, octaves:Boolean=true) = skipStep(skips.toList(), octaves)


/** Transpose to zero based on the first element, returning pitch classes that
 * respect the original order (e.g. not sorted)
 * @return the transposed collection
 */
fun Iterable<Int>.transposeToZero() = map { it.addMod12(12 - first()) }

/** Add mod 12, e.g. 11.addMod12(1)==0
 * @param x the value to add
 * @return the result
 */
fun Int.addMod12(x: Int) = (this + x) % 12

/** Invert mod 12, e.g. 11.invMod12()==1
 * @return the result
 */
fun Int.invMod12() = (12 - (this % 12)) % 12

/**move the collection up or down by t semitones*/
fun Iterable<Int>.transpose(t: Int) = this.map { t + it }

/** return the mth mode of the collection, where m follows music mode conventions so m=1 is the root mode
 * @param m the mode to return
 * @param octaves if true, the mode is generated in an octave aware way (defaults to false)
 * @return the mode
 */
fun Iterable<Int>.mode(m: Int, octaves: Boolean = true): Iterable<Int> =
    mapIndexed { i, _ -> this.wrapped((m - 1) + i, octaves) }

/** Return all modes of the collection in a map keyed by the mode number, where 1 is the root mode
 * @param octaves if true, the modes are generated in an octave aware way (defaults to false)
 * @return an Iterable (list) of modes
 */
fun Iterable<Int>.modesAll(octaves: Boolean = true): Iterable<Iterable<Int>> =
    this.toList().indices.map { this.mode(it + 1, octaves) }

/**ordered note names for midi pitch numbers with sharps or flats for accidentals*/
private val namesSharp = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
private val namesFlat = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

/**the midi octave number for this midi pitch number*/
fun Int.midiOctave() = this / 12 - 1

fun String.midiOctave(): Int {
    val pitchIndex = this.indexOfFirst { it.isDigit() }
    return substring(pitchIndex).toInt()
}

/**the midi pitch class for this midi pitch number*/
fun Int.midiPitchClass() = this % 12

/**a string representation of the midi pitch with octave*/
fun Int.midiPitchNameAndOctave() = "${namesSharp[midiPitchClass()]}${midiOctave()}"

/**the midi pitch class for this String note name*/
fun String.midiPitchClass() =
    if (this.contains("b")) namesFlat.indexOf(this[0].uppercaseChar() +"b")
    else namesSharp.indexOf(this.uppercase(Locale.getDefault()))

fun String.midiPitch():Int {
    val pitchIndex = this.indexOfFirst { it.isDigit() }
    val substring = substring(0, pitchIndex)
    val pitchClass = substring.midiPitchClass()
    return (this.midiOctave()+1) * 12 + pitchClass
}

fun Iterable<Int>.validMidiPitches() = belowHighMidi() && aboveLowMidi()
fun Iterable<Int>.belowHighMidi() = maxOrNull()!! <= Note.PITCH_HIGH
fun Iterable<Int>.aboveLowMidi() = maxOrNull()!! >= Note.PITCH_LOW
