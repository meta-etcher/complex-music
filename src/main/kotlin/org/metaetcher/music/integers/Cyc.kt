package org.metaetcher.music.integers

/** Builds an infinite cycle of Int from the provided elements. Optionally adds root to all returned values.
 * @param elements the elements to cycle through
 * @param root the value to add to each element (default 0)
 * @return an Iterable<Int> that will cycle through the elements, adding root to each element
 * */
class Cyc(private val elements: List<Int>, private val root: Int = 0) : Iterable<Int> {
    constructor(vararg items: Int, root: Int = 0) : this(items.asList(), root)
    constructor(items: Iterable<Int>, root: Int = 0) : this(items.toList(), root)

    private var i = 0  // will preserve the cycle across takes(), etc
    override fun iterator() = object : Iter {
        override fun hasNext() = true
        override fun next() = root + elements[i++ % elements.size]
    }
}