package pl.edu.mimuw.students.wm382710.appa.maps

import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

data class Point2D(val x: Double, val y: Double) {
    operator fun unaryMinus() = Point2D(-x, -y)

    operator fun plus(b: Point2D) = Point2D(x + b.x, y + b.y)

    operator fun minus(b: Point2D) = Point2D(x - b.x, y - b.y)

    operator fun times(a: Double) = Point2D(x * a, y * a)

    operator fun div(a: Double) = Point2D(x / a, y / a)

    infix fun dot(b: Point2D) = x * b.x + y * b.y

    val normalize: Point2D
        get() = this / len

    val norm: Double
        get() = x * x + y * y

    val len: Double
        get() = sqrt(norm)

    fun distanceFromSegment(s1: Point2D, s2: Point2D) = when {
        (this - s1) dot (s2 - s1) > 0 -> (this - s1).norm
        (this - s2) dot (s1 - s2) > 0 -> (this - s2).norm
        else -> {
            val num = abs((s2.y - s1.y) * x - (s2.x - s1.x) * y
                    + s2.x * s1.y - s2.y * s1.x)
            val denom = (s2 - s1).len
            num / denom
        }
    }

    companion object {
        fun det(p: Point2D, q: Point2D, r: Point2D) =
            p.x * q.y + q.x * r.y + r.x * p.y - p.y * q.x - q.y * r.x - r.y * p.x
    }
}

operator fun Double.times(p: Point2D): Point2D = Point2D(this * p.x, this * p.y)
