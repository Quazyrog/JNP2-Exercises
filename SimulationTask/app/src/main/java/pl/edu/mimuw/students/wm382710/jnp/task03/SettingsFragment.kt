package pl.edu.mimuw.students.wm382710.jnp.task03

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ui = inflater.inflate(R.layout.fragment_settings, container, false)
        ui.findViewById<Button>(R.id.startButton).setOnClickListener {
            val params = buildParams(ui)
            if (params !== null)
                (activity as SimulationActivity).startSimulation(params)
        }
        return ui
    }

    private fun buildParams(ui: View): SimulationConstants? {
        try {
            return SimulationConstants()
            val initPop = ui.findViewById<EditText>(R.id.inputInitialNumber).text.toString().toInt()
            val initSick = ui.findViewById<EditText>(R.id.inputInitialSick).text.toString().toInt()
            val dur = ui.findViewById<EditText>(R.id.inputDuration).text.toString().toLong()
            val infP = ui.findViewById<EditText>(R.id.inputInfectionProbability).text.toString().toDouble()
            val dP = ui.findViewById<EditText>(R.id.inputDeathProbability).text.toString().toDouble()
            return SimulationConstants(
                initialAliveBeings = initPop,
                initialSickBeings = initSick,
                durationSeconds = dur,
                infectionProbability = infP,
                deathProbability = dP)
        } catch (e: IllegalArgumentException) {
            val builder = AlertDialog.Builder(context!!)
            builder
                .setMessage("Invalid input: ${e.message}")
            val dialog = builder.create()
            dialog.show()
        }
        return null
    }

}
