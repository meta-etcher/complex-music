package org.metaetcher.music.tests

import org.junit.jupiter.api.Test
import org.metaetcher.music.integers.Cyc
import org.metaetcher.music.notes.MultiNoteBuilder
import org.metaetcher.music.notes.NoteBuilder
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestNoteBuilder {
    @Test
    fun testMultiNoteBuilder() {
        val nb1 = NoteBuilder(Cyc(45, 47), 100, 90)
        val nb2 = NoteBuilder(Cyc(45, 49), Cyc(100, 70), 90)
        val mn1 = MultiNoteBuilder(nb1, nb2).take(4).toList()
        assertEquals(4, mn1.size)
        assertEquals(mn1[0].notes.map { it.pitch }, listOf(45, 45))
        assertEquals(mn1[1].notes.map { it.pitch }, listOf(47, 49))
        assertEquals(mn1[2].notes.map { it.pitch }, listOf(45, 45))
        assertEquals(mn1[0].notes.map { it.duration }, listOf(100, 100))
        assertEquals(mn1[1].notes.map { it.duration }, listOf(100, 70))
        assertEquals(mn1[2].notes.map { it.duration }, listOf(100, 100))
        assertEquals(mn1[0].notes.map { it.velocity }, listOf(90, 90))
    }
    @Test
    fun testNoteBuilder1() {
        val nb1 = NoteBuilder(Cyc(40, 44), 200)
        val notes = nb1.take(4).toList()
        assertEquals(4, notes.size)
        assertEquals(40, notes[0].pitch)
        assertEquals(44, notes[1].pitch)
        assertEquals(40, notes[2].pitch)
        assertEquals(44, notes[3].pitch)
        assertEquals(200, notes[0].duration)
    }

    @Test
    fun testNoteBuilder2() {
        val nb1 = NoteBuilder(Cyc(40, 44), Cyc(200, 300))
        val notes = nb1.take(4).toList()
        assertEquals(4, notes.size)
        assertEquals(40, notes[0].pitch)
        assertEquals(44, notes[1].pitch)
        assertEquals(40, notes[2].pitch)
        assertEquals(44, notes[3].pitch)
        assertEquals(200, notes[0].duration)
        assertEquals(300, notes[1].duration)
        assertEquals(200, notes[2].duration)
        assertEquals(300, notes[3].duration)
    }

    @Test
    fun testNoteBuilder3() {
        val nb1 = NoteBuilder(Cyc(40, 44), Cyc(200, 300), Cyc(90, 100))
        val notes = nb1.take(3).toList()
        assertEquals(3, notes.size)
        assertEquals(40, notes[0].pitch)
        assertEquals(44, notes[1].pitch)
        assertEquals(40, notes[2].pitch)
        assertEquals(200, notes[0].duration)
        assertEquals(300, notes[1].duration)
        assertEquals(200, notes[2].duration)
        assertEquals(90, notes[0].velocity)
        assertEquals(100, notes[1].velocity)
        assertEquals(90, notes[2].velocity)
    }

    @Test
    fun testNoteBuilder4() {
        val nb1 = NoteBuilder(Cyc(45, 47), 100, 90)
        val nb2 = NoteBuilder(Cyc(45, 47), Cyc(100, 70), 90)
        assertEquals(nb1.take(1), nb2.take(1))
        val notes1 = nb1.take(2).toList()
        val notes2 = nb2.take(2).toList()
        assertEquals(notes1[0].pitch, notes2[0].pitch)
        assertEquals(notes1[1].pitch, notes2[1].pitch)
        assertNotEquals(notes1[0].duration, notes2[0].duration)
        assertEquals(notes1[1].duration, notes2[1].duration)
        assertEquals(notes1[0].velocity, notes2[0].velocity)
        assertEquals(notes1[1].velocity, notes2[1].velocity)
    }

}