package pl.edu.mimuw.students.wm382710.jnp.task03

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity(), SimulationActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frag = SettingsFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.centralWidget, frag)
        fragmentTransaction.commit()
    }

    override fun startSimulation(params: SimulationConstants) {
        val s = Simulation(params)
        val frag = SimulationFragment(s)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.centralWidget, frag)
        fragmentTransaction.commit()
        s.start()
    }
}
