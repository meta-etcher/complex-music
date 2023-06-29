package org.metaetcher.music.complex

import org.apache.commons.math3.complex.Complex
import kotlin.math.*

/** covert an angle in radians to a cycle, which is the ratio of a complete circle
 * @return the cycle
 */
fun Double.radToCycle(): Double {
    val cycles = this / (2* PI)
    return if (cycles < 0.0) cycles + 1.0 else cycles
}

/** modulus on the phase of a complex number, which splits the phase into [base] discrete sections
 * @param base the number of discrete sections, default 12
 * @return the modulated phase
 */
fun Complex.modPhase(base: Int = 12) = (base * this.argument.radToCycle()).toInt()

/** compute the ceiling of a scaled value from the absolute value of this complex number
 * @param oldMax the maximum of the old range
 * @param newMax the maximum of the new range
 * @return the ceiling of the rescaled value
 */
fun Complex.ceilAbs(oldMax: Double = 2.5, newMax: Double = 8.0): Int {
    val x = this.abs().rescale(0.0, oldMax, 0.0, newMax) * oldMax / newMax
    return ceil(x).toInt()
}

/** a complex number specified by polar coordinates
 * @param radius the radius
 * @param phaseRad the phase in radians
 * @return the complex number
 */
fun fromPolar(radius: Double, phaseRad: Double) =
    Complex(radius * cos(phaseRad), radius * sin(phaseRad))

/** rescale a Double from one range to another
 * @param oldMin the minimum of the old range
 * @param oldMax the maximum of the old range
 * @param newMin the minimum of the new range
 * @param newMax the maximum of the new range
 * @return the rescaled value
 */
fun Double.rescale(oldMin: Double, oldMax: Double, newMin: Double, newMax:Double) =
    (((this - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin

/** function to compare two complex numbers for equality within a tolerance */
fun Complex.approxEqual(z1: Complex, tol: Double = 1e-7) = (this.subtract(z1).abs() < tol)
