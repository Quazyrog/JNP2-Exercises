package pl.edu.mimuw.students.wm382710.appa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class StatEditFragment(
    private val statName: String,
    private val minVal: Int = 0,
    private val maxVal: Int = 999): Fragment() {

    private var statValue: Int = minVal
    var acceptChange: (String, Int, Int) -> Boolean = { _, _, _ -> true }
    var onChanged: (String, Int, Int) -> Unit = { _, _, _ -> }

    var value: Int
        get() = statValue
        set(v) {
            val nextVal = min(maxVal, max(minVal, v))
            if (nextVal == statValue || !acceptChange(statName, statValue, nextVal))
                return
            MainScope().launch { view?.findViewById<TextView>(R.id.statValueText)?.text = statValue.toString() }
            onChanged(statName, statValue, nextVal)
            statValue = nextVal
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stat_edit, container, false).apply {
            findViewById<TextView>(R.id.editStatName).text = statName
            findViewById<TextView>(R.id.statValueText).text = statValue.toString()
            findViewById<Button>(R.id.incrementButton).setOnClickListener { ++value }
            findViewById<Button>(R.id.decrementButton).setOnClickListener { --value }
        }
    }
}
