package pl.edu.mimuw.students.wm382710.appa.maps

data class Instruction(val azimuth: Double, val distance: Double)

interface TargetLocation {
    fun navigate(from: Point2D): Instruction
}
