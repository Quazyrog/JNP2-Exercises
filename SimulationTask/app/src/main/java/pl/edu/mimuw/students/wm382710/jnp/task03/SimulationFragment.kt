package pl.edu.mimuw.students.wm382710.jnp.task03

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 */
class SimulationFragment(val s: Simulation) : Fragment() {
    private var ui: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        s.clockHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                ui?.findViewById<TextView>(R.id.dispTime)!!.setText("Remaining time: " + msg.obj)
            }
        }
        s.stateUpdateHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val t = msg.obj as Triple<*, *, *>
                ui?.findViewById<TextView>(R.id.dispAlive)!!.setText("Alive: " + t.first)
                ui?.findViewById<TextView>(R.id.dispSick)!!.setText("Sick: " + t.second)
                ui?.findViewById<TextView>(R.id.dispDead)!!.setText("Dead: " + t.third)
                println(t)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ui = inflater.inflate(R.layout.fragment_simulation, container, false)
        return ui
    }

}
