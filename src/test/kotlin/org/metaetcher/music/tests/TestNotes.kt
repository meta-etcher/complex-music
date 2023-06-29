package org.metaetcher.music.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.metaetcher.music.notes.MultiNote
import org.metaetcher.music.notes.Note
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestNotes {
    @Test
    fun testNote() {
        val note1 = Note(40, 100)
        assertEquals(40, note1.pitch)
        assertEquals(100, note1.duration)
        assertEquals(64, note1.velocity)
        assertFalse(note1.isRest())
    }

    @Test
    fun testRest() {
        val note1 = Note.rest(100)
        assertEquals(20000, note1.pitch)
        assertEquals(100, note1.duration)
        assertTrue(note1.isRest())
    }

    @Test
    fun testMultiNote() {
        val note1 = Note(40, 100)
        val note2 = Note(44, 200)
        val mn1 = MultiNote(note1, note2)
        assertEquals(200, mn1.duration())
        assertEquals(2, mn1.notes.toList().size)
        val mn2 = MultiNote(listOf(40, 44), 200)
        assertEquals(200, mn2.duration())
        assertEquals(2, mn2.notes.toList().size)
        val mn3 = MultiNote(listOf(40, 44), 200, 90)
        assertEquals(200, mn3.duration())
        assertEquals(2, mn3.notes.toList().size)
        assertEquals(90, mn3.notes.toList()[0].velocity)
    }
}