package pl.edu.mimuw.students.wm382710.appa.maps

import kotlin.math.PI

class RadiansType
operator fun Double.times(@Suppress("UNUSED_PARAMETER") rad: RadiansType) = this / 180.0 * PI
val RAD = RadiansType()

class DegreesType
operator fun Double.times(@Suppress("UNUSED_PARAMETER") rad: DegreesType) = this / PI * 180.0
val DEG = DegreesType()

open class EarthPoint(longitudeDeg: Double, latitudeDeg: Double) {
    val lon = longitudeDeg * RAD
    val lat = latitudeDeg * RAD

    init {
        require(-180 < longitudeDeg && longitudeDeg <= 180)
        require(-90 < latitudeDeg && latitudeDeg <= 90)
    }

    override fun toString(): String {
        val ns = if (lat > 0) "%.4fN".format(lat * DEG) else "%.4fS".format(-lat * DEG)
        val ew = if (lon > 0) "%.4fE".format(lon * DEG) else "%.4fW".format(-lon * DEG)
        return "($ns $ew)"
    }
}
