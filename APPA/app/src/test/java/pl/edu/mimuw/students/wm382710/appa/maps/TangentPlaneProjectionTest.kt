package pl.edu.mimuw.students.wm382710.appa.maps

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.*
import kotlin.random.Random.Default.nextDouble

class TangentPlaneProjectionTest {
    @Test
    fun xzProjection() {
        val r = 100.0
        val proj = TangentPlaneProjection(-90.0, 0.0, r)
        for (i in 1 .. 1000) {
            val x = nextDouble() * 2.0 * r - r
            val y = nextDouble() * 2.0 * r - r
            val z = nextDouble() * 2.0 * r - r
            val p = proj(Point3D(x, y, z))
            assertTrue(abs(x - p.x) < Companion.TOLERANCE)
            assertTrue(abs(z - p.y) < Companion.TOLERANCE)
        }
    }

    @Test
    fun localDistance() {
        val maxRelativeError = 0.05
        val spanDegInit = 2.0
        val r = TangentPlaneProjection.EARTH_EQUATORIAL_RADIUS
        val outerCases = 10
        val innerCases = 1_000

        for (n in 1..outerCases) {
            val spanDeg = n * spanDegInit / outerCases
            val cLon = nextDouble() * 360.0 - 180.0
            val cLat = nextDouble() * 180.0 - 90.0
            val proj = TangentPlaneProjection(cLon, cLat, r)

            var cumulError = 0.0

            for (i in 1..innerCases) {
                val p0Lon = cLon + spanDeg * nextDouble()
                val p0Lat = cLat + spanDeg * nextDouble()
                val p1Lon = cLon + spanDeg * nextDouble()
                val p1Lat = cLat + spanDeg * nextDouble()

                val p0proj = proj(p0Lon, p0Lat)
                val p1proj = proj(p1Lon, p1Lat)

                val approximatedDistance = (p0proj - p1proj).len
                val realDistance =
                    greatCircleDistance(r, rad(p0Lon), rad(p0Lat), rad(p1Lon), rad(p1Lat))
                cumulError += abs(realDistance - approximatedDistance)

                assertTrue(
                    "Expected $realDistance, got $approximatedDistance",
                    abs(realDistance - approximatedDistance) / realDistance < maxRelativeError
                )
            }
            println("Try $n/$outerCases average error: ${cumulError / innerCases}; span=$spanDeg")
        }
    }

    fun greatCircleDistance(r: Double, lon1: Double, lat1: Double, lon2: Double, lat2: Double) =
        r * acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon1 - lon2))

    private fun rad(deg: Double): Double = deg / 180.0 * PI

    private fun lonLatStr(lon: Double, lat: Double): String {
        val p0 = when {
            lat > 0 -> (round(10_000 * lat) / 10_000).toString() + "N"
            else -> (round(-10_000 * lat) / 10_000).toString() + "S"
        }
        val p1 = when {
            lon > 0 -> (round(10_000 * lon) / 10_000).toString() + "W"
            else -> (round(-10_000 * lon) / 10_000).toString() + "E"
        }
        return "$p0 $p1"
    }

    companion object {
        const val TOLERANCE = 1E-9
    }
}