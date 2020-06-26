package pl.edu.mimuw.students.wm382710.appa.maps

import kotlin.math.asin
import kotlin.math.sqrt

data class Point2D(val x: Double, val y: Double): TargetLocation {
    operator fun unaryMinus() = Point2D(-x, -y)

    operator fun plus(b: Point2D) = Point2D(x + b.x, y + b.y)

    operator fun minus(b: Point2D) = Point2D(x - b.x, y - b.y)

    operator fun times(a: Double) = Point2D(x * a, y * a)

    operator fun div(a: Double) = Point2D(x / a, y / a)

    infix fun dot(b: Point2D) = x * b.x + y * b.y

    private val normalize: Point2D
        get() = this / len

    val norm: Double
        get() = x * x + y * y

    val len: Double
        get() = sqrt(norm)

    override fun navigate(from: Point2D): Instruction {
        val n = Point2D(0.0, 1.0)
        val d = (this - from).normalize
        return Instruction (asin(n.x * d.y - n.y * d.x), (this - from).len)
    }

    companion object {
        fun det(p: Point2D, q: Point2D, r: Point2D) =
            p.x * q.y + q.x * r.y + r.x * p.y - p.y * q.x - q.y * r.x - r.y * p.x
    }
}

operator fun Double.times(p: Point2D): Point2D = Point2D(this * p.x, this * p.y)
