package pl.edu.mimuw.students.wm382710.jnp.task02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

class TableFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    val attributes = arrayOf<Pair<String, Int>>(
        Pair("Strength", 14), Pair("Constitution", 16),
        Pair("Dexterity", 13), Pair("Intelligence", 10),
        Pair("Wisdom", 12), Pair("Charisma", 11)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ui = inflater.inflate(R.layout.fragment_table, container, false)
        val matrix = ui.findViewById<TableLayout>(R.id.attributeMatrix)
        val c = context
        if (c === null)
            return null
        for ((attribute, value) in attributes) {
            val row = TableRow(c)

            val t1 = TextView(c)
            t1.setText(attribute)
            row.addView(t1)

            val t2 = TextView(c)
            t2.setText(value.toString())
            row.addView(t2)

            val t3 = TextView(c)
            val motifier = value / 2 - 5
            t3.setText(motifier.toString())
            row.addView(t3)

            val t4 = TextView(c)
            t4.setText(when (value) {
                in 0 .. 12 -> 1
                in 13 .. 15 -> 2
                else -> 3
            }.toString())
            row.addView(t4)

            matrix.addView(row)
        }
        return ui
    }

}
