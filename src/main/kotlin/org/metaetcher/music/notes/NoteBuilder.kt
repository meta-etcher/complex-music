package org.metaetcher.music.notes

import org.metaetcher.music.integers.Cyc
import org.metaetcher.music.integers.Iter

/**Builds [Note]s from the provided pitch, duration, and velocity patterns.
 * These patterns are given as [Iterable]s of [Int]s and are often cyclical and infinite.
 * The [NoteBuilder] itself is an [Iterable] of [Note]s.
 * If any of the input [Iterable]s are finite, the [NoteBuilder] will be finite.
 * The [NoteBuilder] is designed to be iterated through one time only.
 */
class NoteBuilder(private val p: Iter, private val d: Iter, private val v: Iter = Cyc(64).iterator()) : Iterable<Note> {
    constructor(p: Iterable<Int>, d: Iterable<Int>, v: Iterable<Int>) : this(p.iterator(), d.iterator(), v.iterator())
    constructor(p: Iterable<Int>, d: Iterable<Int>) : this(p.iterator(), d.iterator())
    constructor(p: Iterable<Int>, d: Int) : this(p, Cyc(d))
    constructor(p: Iterable<Int>, d: Int, v: Int) : this(p, Cyc(d), Cyc(v))
    constructor(p: Iterable<Int>, d: Iterable<Int>, v: Int) : this(p, d, Cyc(v))

    override fun iterator() = object : Iterator<Note> {
        override fun hasNext() = (p.hasNext() && d.hasNext() && v.hasNext())
        override fun next() = Note(p.next(), d.next(), v.next())
    }
}

/**Builds [MultiNote]s by integrating the provided note patterns.
 * These patterns are given as [Iterable]s of [Note]s and are often cyclical (infinite).
 * The [MultiNoteBuilder] itself is an [Iterable] of [MultiNote]s.
 */
class MultiNoteBuilder(iters: Iterable<Iterable<Note>>) : Iterable<MultiNote> {
    constructor(vararg iters: Iterable<Note>) : this(iters.toList())

    private val iterators = iters.map { it.iterator() }
    override fun iterator() = object : Iterator<MultiNote> {
        override fun hasNext(): Boolean = iterators.all { it.hasNext() }
        override fun next(): MultiNote = MultiNote(iterators.map { it.next() })
    }
}