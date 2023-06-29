package org.metaetcher.music.tests

import org.apache.commons.math3.complex.Complex
import org.junit.jupiter.api.Test
import org.metaetcher.music.complex.ceilAbs
import org.metaetcher.music.complex.fromPolar
import org.metaetcher.music.complex.modPhase
import org.metaetcher.music.complex.radToCycle
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestComplex {
    @Test
    fun testRadToCycle() {
        assertEquals(0.0, 0.0.radToCycle())
        assertEquals(0.25, (PI/2).radToCycle())
        assertEquals(0.5, PI.radToCycle())
        assertEquals(1.0, (2*PI).radToCycle())
    }

    @Test
    fun testModPhase() {
        assertEquals(0, Complex(1.0, 0.0).modPhase(4))
        assertEquals(1, Complex(0.0, 1.0).modPhase(4))
        assertEquals(2, Complex(-1.0, 0.0).modPhase(4))
        assertEquals(3, Complex(0.0, -1.0).modPhase(4))
        println(Complex(1.0, 0.0).modPhase())  // default is 12 sectors
        println(Complex(0.0, 1.0).modPhase())
        println( Complex(0.0, -1.0).modPhase(5)) // uses 5 sectors
    }

    @Test
    fun testFromPolar() {
        val z = fromPolar(1.0, 0.0)
        assertTrue(Complex.equals(z, Complex(1.0, 0.0), 0.00001))
    }

    @Test
    fun ceilAbs() {
        assertEquals(1, Complex(1.0, 0.0).ceilAbs())
        assertEquals(1, Complex(0.0, -0.5).ceilAbs())
        assertEquals(2, Complex(1.1, -1.1).ceilAbs())
    }
}