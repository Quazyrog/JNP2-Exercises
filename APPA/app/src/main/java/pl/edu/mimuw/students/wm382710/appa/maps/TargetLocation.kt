package pl.edu.mimuw.students.wm382710.appa.maps

import pl.edu.mimuw.students.wm382710.appa.maps.TangentPlaneProjection.Companion.EARTH_EQUATORIAL_RADIUS
import pl.edu.mimuw.students.wm382710.appa.maps.TangentPlaneProjection.Companion.EARTH_POLAR_RADIUS
import kotlin.math.abs
import kotlin.math.asin

data class Navigation(val azimuth: Double, val distance: Double)

interface TargetLocation {
    fun navigate(longitude: Double, latitude: Double): Navigation
}

class EarthPoint(longitudeDeg: Double, latitudeDeg: Double): TargetLocation {
    val lon = longitudeDeg * RAD
    val lat = latitudeDeg * RAD

    init {
        require(-180 < longitudeDeg && longitudeDeg <= 180)
        require(-90 < latitudeDeg && latitudeDeg <= 90)
    }

    override fun navigate(longitudeDeg: Double, latitudeDeg: Double): Navigation {
        val redius = if (abs(latitudeDeg) > 45.0) EARTH_POLAR_RADIUS else EARTH_EQUATORIAL_RADIUS
        val proj = TangentPlaneProjection(longitudeDeg * RAD, latitudeDeg * RAD)

        val here = proj(lon, lat)
        val there = proj(longitudeDeg * RAD, latitudeDeg * RAD)
        val dir = (here - there).normalize
        return Navigation (asin(proj.north.x * dir.y - proj.north.y * dir.x), (here - there).len)
    }

    override fun toString(): String {
        val ns = if (lat > 0) "%.4fN".format(lat * DEG) else "%.4fS".format(-lat * DEG)
        val ew = if (lon > 0) "%.4fW".format(lon * DEG) else "%.4fE".format(-lon * DEG)
        return "($ns $ew)"
    }
}
