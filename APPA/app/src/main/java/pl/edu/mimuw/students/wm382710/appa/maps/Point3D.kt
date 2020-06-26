package pl.edu.mimuw.students.wm382710.appa.maps

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Point3D(val x: Double, val y: Double, val z: Double) {
    operator fun unaryMinus() = Point3D(-this.x, -this.y, -this.z)

    operator fun plus(p: Point3D) = Point3D(this.x + p.x, this.y + p.y, this.z + p.z)

    operator fun minus(p: Point3D) = Point3D(this.x - p.x, this.y - p.y, this.z - p.z)

    operator fun times(a: Double) = Point3D(this.x * a, this.y * a, this.z * a)

    operator fun div(a: Double) = Point3D(this.x / a, this.y / a, this.z / a)

    infix fun dot(b: Point3D) = x * b.x + y * b.y + z * b.z

    infix fun cross(b: Point3D): Point3D {
        val s1 = y * b.z - z * b.y
        val s2 = z * b.x - x * b.z
        val s3 = x * b.y - y * b.x
        return Point3D(s1, s2, s3)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Point3D)
            return abs(x - other.x) + abs(y - other.y) + abs(z - other.z) < 1E-9
        return false
    }

    val norm: Double
        get() = x * x + y * y + z * z

    val len: Double
        get() = sqrt(x * x + y * y + z * z)

    fun projectionCoefficient(b: Point3D) = (this dot b) / this.norm

    companion object {
        fun fromPolar(r: Double, longitude: Double, latitude: Double): Point3D {
            val x = cos(latitude) * cos(longitude) * r
            val y = cos(latitude) * sin(longitude) * r
            val z = sin(latitude) * r
            return Point3D(x, y, z)
        }
    }

    override fun toString() = "Point3D{$x, $y, $z}"
}

operator fun Double.times(p: Point3D) = Point3D(this * p.x, this * p.y, this * p.z)
