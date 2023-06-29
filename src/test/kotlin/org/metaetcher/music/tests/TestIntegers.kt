package org.metaetcher.music.tests

import org.junit.jupiter.api.Test
import org.metaetcher.music.integers.*
import kotlin.test.assertEquals

class TestIntegers {
    @Test
    fun testWrapped1() {
        val list = listOf(0, 1, 2)
        assertEquals(1, list.wrapped(-2))
        assertEquals(2, list.wrapped(-1))
        assertEquals(0, list.wrapped(0))
        assertEquals(1, list.wrapped(1))
        assertEquals(2, list.wrapped(2))
        assertEquals(0, list.wrapped(3))
    }

    @Test
    fun testWrapped2() {
        val set = setOf(0, 1, 2)
        assertEquals(-22, set.wrapped(-4, true))
        assertEquals(-12, set.wrapped(-3, true))
        assertEquals(-11, set.wrapped(-2, true))
        assertEquals(-10, set.wrapped(-1, true))
        assertEquals(0, set.wrapped(0, true))
        assertEquals(1, set.wrapped(1, true))
        assertEquals(2, set.wrapped(2, true))
        assertEquals(12, set.wrapped(3, true))
        assertEquals(13, set.wrapped(4, true))
        assertEquals(14, set.wrapped(5, true))
    }

    @Test
    fun testSkips() {
        val pitches1 = listOf(0, 2, 4, 7).transpose(50)
        val skips1 = listOf(0, 1, 1, 1, 1)
        println(pitches1.skipStep(skips1, true))
        assertEquals(listOf(50, 52, 54, 57, 50), pitches1.skipStep(skips1, false))
        assertEquals(listOf(50, 52, 54, 57, 62), pitches1.skipStep(skips1, true))
    }

    @Test
    fun testCyc1() {
        val cyc = Cyc(0, 1, 2, 3, 4, 5, 6, 7, 8)
        assert(cyc.take(10) == listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 0))
    }

    @Test
    fun testCyc2() {
        val cyc = Cyc(0, 1, 2, 3, root=48)
        assert(cyc.take(10) == listOf(48, 49, 50, 51, 48, 49, 50, 51, 48, 49))
    }

    @Test
    fun testCyc3() {
        val cycIter = Cyc(setOf(0, 1, 2, 3), root=40).iterator()
        assert(cycIter.next() == 40)
        assert(cycIter.next() == 41)
        assert(cycIter.next() == 42)
        assert(cycIter.next() == 43)
        assert(cycIter.hasNext())
        assert(cycIter.next() == 40)
    }

    @Test
    fun testTransposeToZero() {
        assertEquals(listOf(0, 1, 2), listOf(0, 1, 2).transposeToZero())
        assertEquals(listOf(0, 1, 2), listOf(10, 11, 12).transposeToZero())
        assertEquals(listOf(0, 6, 11), listOf(54, 60, 53).transposeToZero())
        assertEquals(listOf(0, 11, 10), listOf(60, 59, 58).transposeToZero())
    }

    @Test
    fun testAddMod12() {
        assertEquals(0, 0.addMod12(0))
        assertEquals(1, 0.addMod12(1))
        assertEquals(2, 0.addMod12(2))
        assertEquals(3, 0.addMod12(3))
        assertEquals(4, 0.addMod12(4))
        assertEquals(5, 0.addMod12(5))
        assertEquals(6, 0.addMod12(6))
        assertEquals(7, 0.addMod12(7))
        assertEquals(8, 0.addMod12(8))
        assertEquals(9, 0.addMod12(9))
        assertEquals(10, 0.addMod12(10))
        assertEquals(11, 0.addMod12(11))
        assertEquals(0, 0.addMod12(12))
    }

    @Test
    fun testInvMod12() {
        assertEquals(0, 0.invMod12())
        assertEquals(11, 1.invMod12())
        assertEquals(10, 2.invMod12())
        assertEquals(9, 3.invMod12())
        assertEquals(8, 4.invMod12())
        assertEquals(7, 5.invMod12())
        assertEquals(6, 6.invMod12())
        assertEquals(5, 7.invMod12())
        assertEquals(4, 8.invMod12())
        assertEquals(3, 9.invMod12())
        assertEquals(2, 10.invMod12())
        assertEquals(1, 11.invMod12())
    }

    @Test
    fun testTranspose() {
        assertEquals(listOf(0, 1, 2), listOf(0, 1, 2).transpose(0))
        assertEquals(listOf(10, 11, 12), listOf(0, 1, 2).transpose(10))
    }

    @Test
    fun testMode1() {
        assertEquals(listOf(0, 1, 2), listOf(0, 1, 2).mode(1))
        assertEquals(listOf(1, 2, 12), listOf(0, 1, 2).mode(2))
        assertEquals(listOf(2, 12, 13), listOf(0, 1, 2).mode(3))
    }

    @Test
    fun testMode2() {
        assertEquals(listOf(0, 1, 2), listOf(0, 1, 2).mode(1, false))
        assertEquals(listOf(1, 2, 0), listOf(0, 1, 2).mode(2, false))
        assertEquals(listOf(2, 0, 1), listOf(0, 1, 2).mode(3, false))
    }

    @Test
    fun testModesAll() {
        assertEquals(listOf(listOf(0, 1, 2), listOf(1, 2, 0), listOf(2, 0, 1)), listOf(0, 1, 2).modesAll(false))
    }

    @Test
    fun testMdiPitchAndOctave() {
        assertEquals("C4", 60.midiPitchNameAndOctave())
        assertEquals("C#4", 61.midiPitchNameAndOctave())
    }

    @Test
    fun testMidiOctave() {
        assertEquals(4, "C4".midiOctave())
        assertEquals(4, "C#4".midiOctave())
        assertEquals(7, "Eb7".midiOctave())
    }

    @Test
    fun testMidiPitch() {
        assertEquals(4, "C4".midiOctave())
        assertEquals(60, "C4".midiPitch())
    }
}

