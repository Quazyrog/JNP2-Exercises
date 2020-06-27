package pl.edu.mimuw.students.wm382710.appa.maps

import pl.edu.mimuw.students.wm382710.appa.maps.TangentPlaneProjection.Companion.EARTH_EQUATORIAL_RADIUS
import pl.edu.mimuw.students.wm382710.appa.maps.TangentPlaneProjection.Companion.EARTH_POLAR_RADIUS
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin

data class Navigation(val azimuth: Double, val distance: Double)

interface TargetLocation {
    val name: String
    fun navigate(longitude: Double, latitude: Double) = navigate(EarthPoint(longitude, latitude))
    fun navigate(point: EarthPoint): Navigation
}

class PointLocation(private val locationName: String, longitudeDeg: Double, latitudeDeg: Double):
    EarthPoint(longitudeDeg, latitudeDeg),
    TargetLocation
{
    override val name: String
        get() = locationName

    override fun navigate(p: EarthPoint): Navigation {
        val redius = if (p.lat > PI / 4) EARTH_POLAR_RADIUS else EARTH_EQUATORIAL_RADIUS
        val proj = TangentPlaneProjection(p.lon, p.lat)

        val here = proj(lon, lat)
        val there = proj(p.lon, p.lat)
        val dir = (here - there).normalize
        return Navigation (asin(dir.x), (here - there).len)
    }
}
