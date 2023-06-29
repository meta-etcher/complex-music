package org.metaetcher.music.complex

import org.apache.commons.math3.complex.Complex

/** a data class for the coordinates of a point */
data class Point(val x: Double, val y: Double)

/** a data class for a coordinate system
 * @param xO the x coordinate of the origin
 * @param yO the y coordinate of the origin
 * @param xNum the number of points in the x direction
 * @param yNum the number of points in the y direction
 * @param xRadius the radius in the x direction
 */
data class Coords(val xO: Double, val yO: Double, val xNum: Int, val yNum: Int, val xRadius: Double) {
    constructor(origin: Complex, xNum: Int, yNum: Int, xRadius: Double) :
            this(origin.real, origin.imaginary, xNum, yNum, xRadius)

    val origin = Complex(xO, yO)
    val xMin = xO - xRadius
    val xMax = xO + xRadius
    private val resolution = xRadius * 2.0 / xNum
    val yRadius = yNum / 2 * resolution
    val yMin = yO - yRadius
    val yMax = yO + yRadius

    /** convert a discreet point in the grid to a point in the complex plane
     * @param x the x coordinate of the discreet point in the grid
     * @param y the y coordinate of the discreet point in the grid
     * @return the point at the given coordinates within the complex plane
     */
    fun point(x: Int, y: Int) = Point(xMin + x * resolution, yMin + y * resolution)

    /** convert a discreet point in the grid to a complex number
     * @param x the x coordinate of the discreet point in the grid
     * @param y the y coordinate of the discreet point in the grid
     * @return the complex number at the given coordinates within the complex plane
     */
    fun complex(x: Int, y: Int): Complex {
        val p = point(x, y); return Complex(p.x, p.y)
    }

    /** scale the coordinate system by the given factor
     * @param factor the scale factor
     * @return a new coordinate system with the given scale factor
     */
    fun scale(factor: Double) = Coords(xO, yO, xNum, yNum, xRadius * factor)

    /** offset the coordinate system by the given amount
     * @param xOff the x offset
     * @param yOff the y offset
     * @return a new coordinate system with the given offset
     */
    fun offset(xOff: Double, yOff: Double) = Coords(xO + xOff, yO + yOff, xNum, yNum, xRadius)
}