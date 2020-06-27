package pl.edu.mimuw.students.wm382710.appa.maps

import kotlin.math.PI

class RadiansType
operator fun Double.times(@Suppress("UNUSED_PARAMETER") rad: RadiansType) = this / 180.0 * PI
val RAD = RadiansType()

class DegreesType
operator fun Double.times(@Suppress("UNUSED_PARAMETER") rad: DegreesType) = this / PI * 180.0
val DEG = DegreesType()
