package pl.edu.mimuw.students.wm382710.appa.maps

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random.Default.nextDouble

class Point3DTest {
    @Test
    fun crossCanonicalBase() {
        val e1 = Point3D(1.0, 0.0, 0.0)
        val e2 = Point3D(0.0, 1.0, 0.0)
        val e3 = Point3D(0.0, 0.0, 1.0)
        assertTrue((e3 - (e1 cross e2)).len < TOLERANCE)
        assertTrue((e1 - (e2 cross e3)).len < TOLERANCE)
        assertTrue((e2 - (e3 cross e1)).len < TOLERANCE)
    }

    @Test
    fun projection() {
        val v1 = randomPoint3D()
        val v2 = Point3D(v1.y, -v1.x, 0.0)
        val v3 = v1 cross v2

        for (i in 1 .. 1000) {
            val p = randomPoint3D()
            val pp = (v1.projectionCoefficient(p) * v1
                    + v2.projectionCoefficient(p) * v2
                    + v3.projectionCoefficient(p) * v3)
            assertTrue((pp - p).len < TOLERANCE)
        }
    }

    companion object {
        const val TOLERANCE = 1E-9
        fun randomPoint3D() = Point3D(2 * nextDouble() - 1, 2 * nextDouble() - 1, 2 * nextDouble() - 1)
    }
}