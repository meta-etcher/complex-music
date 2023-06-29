package org.metaetcher.music.examples

import org.apache.commons.math3.complex.Complex
import org.metaetcher.music.complex.Result
import org.metaetcher.music.complex.algoACircle
import org.metaetcher.music.complex.ceilAbs
import org.metaetcher.music.complex.modPhase
import org.metaetcher.music.integers.transpose
import org.metaetcher.music.integers.wrapped
import org.metaetcher.music.midi.Part
import org.metaetcher.music.midi.SequencePlayer
import org.metaetcher.music.midi.keySignatureC
import org.metaetcher.music.notes.MultiNote
import org.metaetcher.music.notes.Note
import kotlin.math.abs

// data class with parameters for a block of material
data class Block(val scale: Iterable<Int>, val range: Int, val num: Int, val function: (Complex, Complex) -> Complex)

// complex functions
fun f1(zn: Complex, zc: Complex): Complex = Complex(1.0, 0.0).divide(zn.multiply(zn)).subtract(zc)
fun f2(zn: Complex, zc: Complex): Complex = zn.multiply(zn).add(zc)

// generate a block of music
fun processBlock(block: Block, initialComplex: Complex, part: Part) {
    val indicesLists = listOf(listOf(4), listOf(5, 7), listOf(2, 9))  // indexes of notes from results to select
    val maxIterations = listOf(10, 20)                                // max depths
    var count = 0

    indicesLists.forEach { indices ->
        maxIterations.forEach { max ->
            count++
            // traverse a circle of points returning results from each
            algoACircle(block.num, initialComplex, 0.7885, max, indices, null, block.function).forEach { result ->
                val notes = createNotes(result, block)              // map the results to notes
                if (notes.isNotEmpty()) part.add(MultiNote(notes))  // add the notes as a chord
            }
        }
    }

    println("count: $count")
}

// create notes from the list of complex results
fun createNotes(result: Result, block: Block): MutableList<Note> {
    val notes = mutableListOf<Note>()
    result.results.forEach { complex ->
        val pitch = block.scale.transpose(40).wrapped(complex.modPhase(block.range), true)
        val duration = complex.ceilAbs(3.5, 16.0)
        if (notes.isEmpty() || notes.map { n -> abs(n.pitch - pitch) }.minOrNull()?.let { it > 1 } == true)
            notes.add(Note(pitch, duration))
    }
    return notes
}

fun main() {
    val initialComplex = Complex(0.0, 0.0)
    val part = Part(0, "piano")   // one part with multi-timbrel notes

    // structure of the composition
    val blocks = listOf(
        Block(MajorMinorScales.natMinor, 21, 12, ::f1),
        Block(MajorMinorScales.natMinor, 24, 24, ::f2),
        Block(Messiaen.mode2, 21, 12, ::f1)
    )

    // generate the music
    blocks.forEach { block -> processBlock(block, initialComplex, part) }

    // play via synthesizer and/or save to a midi file
    // if a high quality sound-bank is available, hqSounds can be set to true for playback, see SequencePlayer.kt
    SequencePlayer(hqSounds=false).apply {
        register(keySignatureC())
        register(part)
        play(75)
        //write("frac3_3.mid", 75)
        close()
    }
}
