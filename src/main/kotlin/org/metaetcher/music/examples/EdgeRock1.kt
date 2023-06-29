package org.metaetcher.music.examples

import org.apache.commons.math3.complex.Complex
import org.metaetcher.music.complex.*
import org.metaetcher.music.integers.transpose
import org.metaetcher.music.integers.wrapped
import org.metaetcher.music.midi.Part
import org.metaetcher.music.midi.SequencePlayer
import org.metaetcher.music.midi.keySignatureC
import org.metaetcher.music.midi.volume
import org.metaetcher.music.notes.MultiNote
import org.metaetcher.music.notes.Note
import kotlin.math.abs

// data class with parameters for a block of material
data class C1Block(val scale: Iterable<Int>, val range: Int, val num: Int, val radius: Double,
                   val f: (Complex, Complex) -> Complex)

// Compositional structure A
class PartA(blocks: Iterable<C1Block>, private val z: Complex, private val scaleRoot: Int) {
    val leadA = Part(0, "piano")
    init {
        blocks.forEach { block ->
            listOf(listOf(4), listOf(5, 7), listOf(2, 9)).forEach { noteIndexes ->  // indexes of notes to select
                listOf(10, 50).forEach { max ->                                     // max depths
                    // traverse a circle of points returning results from each
                    algoACircle(block.num, z, block.radius, max, noteIndexes, null, block.f).forEach { res ->
                       val leadNotes = toNotes(res, block)
                       if (leadNotes.isNotEmpty())
                            leadA.add(MultiNote(leadNotes))
                    }
                }
            }
        }
    }

    // create notes from the list of complex results
    private fun toNotes(res: Result, block: C1Block): MutableList<Note> {
        val leadNotes = mutableListOf<Note>()
        res.results.forEach { complex ->
            val pitch = block.scale.transpose(scaleRoot).wrapped(complex.modPhase(block.range), true)
            val duration = complex.ceilAbs(newMax = 16.0)
            if (leadNotes.isEmpty() || leadNotes.minOf { n -> abs(n.pitch - pitch) } > 1)
                leadNotes.add(Note(pitch, duration))
        }
        return leadNotes
    }
}

fun main() {
    val z = Complex(0.0, 0.0)
    val radius = 0.83

    // structure of the composition
    val blocks = listOf(
        C1Block(Messiaen.mode2, 22, 12, radius, ::f1),
        C1Block(Messiaen.mode4, 24, 20, radius, ::f2),
        C1Block(Messiaen.mode6, 24, 12, radius, ::f1),
    )

    // generate part A of the composition  (Part B, etc. not included in this code)
    val partA = PartA(blocks, z,  37)

    // play via synthesizer and/or save to a midi file
    // if a high quality sound-bank is available, hqSounds can be set to true for playback, see SequencePlayer.kt
    SequencePlayer(hqSounds=false).apply {
        register(keySignatureC())
        register(volume(0, 0, 100))
        register(partA.leadA)
        play(83)
        //write("EdgeRock1.mid", 83)
        close()
    }
}