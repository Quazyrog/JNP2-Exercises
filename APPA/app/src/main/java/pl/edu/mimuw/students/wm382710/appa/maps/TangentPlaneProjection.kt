package pl.edu.mimuw.students.wm382710.appa.maps

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class TangentPlaneProjection(
    centerLongitude: Double,
    centerLatitude: Double,
    private val sphereRadius: Double = EARTH_EQUATORIAL_RADIUS
) {
    private val up = Point3D.fromPolar(1.0, rad(centerLongitude), rad(centerLatitude))
    private val north = tangentNorth(rad(centerLongitude), rad(centerLatitude))
    private val west = north cross up

    operator fun invoke(p: Point3D) = sphereRadius * transform(p / sphereRadius)

    operator fun invoke(longitude: Double, latitude: Double) =
        sphereRadius * transform(Point3D.fromPolar(1.0, rad(longitude), rad(latitude)))

    private fun transform(p: Point3D): Point2D {
        val dp = p - up
        val x = west.projectionCoefficient(dp)
        val y = north.projectionCoefficient(dp)
        return Point2D(x, y)
    }

    companion object {
        const val EARTH_EQUATORIAL_RADIUS = 6_378_000.0
        const val EARTH_POLAR_RADIUS = 6_357_000.0

        private fun tangentNorth(longitudeRad: Double, latitudeRad: Double): Point3D {
            val x = -sin(latitudeRad) * cos(longitudeRad)
            val y = -sin(latitudeRad) * sin(longitudeRad)
            val z = cos(latitudeRad)
            return Point3D(x, y, z)
        }

        private fun rad(deg: Double) = deg / 180.0 * PI
    }
}
