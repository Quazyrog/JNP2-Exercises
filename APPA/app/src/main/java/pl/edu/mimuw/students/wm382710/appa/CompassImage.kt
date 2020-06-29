package pl.edu.mimuw.students.wm382710.appa

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class CompassView(context: Context, attrs: AttributeSet) : View(context, attrs)
{
    private lateinit var outerRect: RectF
    private lateinit var innerRect: RectF
    private lateinit var clipPath: Path

    private var facingCcw = 0.0F
    private var naviCcw = 0.0F
    private var highlightThickness = 0F

    private var onRangeStart = 0F
    private var onRangeEnd = 0.0F

    private val stupidAnimationHandler = Handler()

    var facing: Float
        get() = angleInternalToExternal(facingCcw)
        set(value) {
            facingCcw = angleExternalToInternal(value)
            updateRanges()
        }

    var azimuth: Float
        get() = angleInternalToExternal(naviCcw)
        set(value) {
            naviCcw = angleExternalToInternal(value)
            updateRanges()
        }

    var accuracy: Float
        get() = highlightThickness
        set(value) {
            require(value > 0)
            highlightThickness = value
            updateRanges()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val dim = min(w - 2 * MIN_MARG, h - 2 * MIN_MARG)
        val xmarg = (w - dim) / 2.0F
        val ymarg = (h - dim) / 2.0F
        outerRect = RectF(xmarg, ymarg, w - xmarg, h - ymarg)
        innerRect = RectF(xmarg + RING_THICKNESS, ymarg + RING_THICKNESS, w - xmarg - RING_THICKNESS, h - ymarg - RING_THICKNESS)

        clipPath = Path()
        clipPath.addOval(innerRect, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.clipOutPath(clipPath)
        for (i in 0 .. 71) {
            val a0 = 5F * i + 1F
            val paint = if (isBarOn(a0, a0 + BAR_LENGTH)) PAINT_ON else PAINT_OFF
            canvas.drawArc(outerRect, a0, BAR_LENGTH, true, paint)
        }
    }


    private fun isBarOn(a0: Float, a1: Float): Boolean {
        require(0.0 <= a0 && a0 < a1 && a1 <= 360.0)
        val intersectStart = max(a0, onRangeStart)
        val intersectEnd = min(a1, onRangeEnd)
        return intersectStart <= intersectEnd
    }

    private fun updateRanges() {
        onRangeStart = naviCcw - facingCcw - highlightThickness
        onRangeEnd = naviCcw - facingCcw + highlightThickness

        while (onRangeEnd < 0) {
            onRangeStart += 360
            onRangeEnd += 360
        }
//        println("Highlighted range: [$onRangeStart, $onRangeEnd]")

        invalidate()
    }

    fun playStupidAnimation(delay: Long = 100L) {
        var a = 0
        stupidAnimationHandler.postDelayed(object : Runnable {
            override fun run() {
                facing = a++.toFloat()
                stupidAnimationHandler.postDelayed(this, delay)
            }
        }, delay)
    }

    companion object {
        const val MIN_MARG = 60F
        const val BAR_LENGTH = 3.0F
        const val RING_THICKNESS = 50.0F

        val PAINT_ON = Paint().apply {
            color = 0xff00aaff.toInt()
            style = Paint.Style.FILL
        }

        val PAINT_OFF = Paint().apply {
            color = 0xff666666.toInt()
            style = Paint.Style.FILL
        }

        private fun angleInternalToExternal(angle: Float): Float {
            val a = (angle + 90.0F) % 360.0F
            return when {
                a <= 180 -> a
                else -> a - 360
            }
        }

        private fun angleExternalToInternal(angle: Float): Float {
            val a = when {
                angle < 0 -> angle + 360
                else -> angle
            }
            return (a + 270.0F) % 360.0F
        }
    }
}