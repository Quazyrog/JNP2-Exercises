package pl.edu.mimuw.students.wm382710.jnp.task02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    val fragments = arrayOf<Fragment>(
        TableFragment(),
        RadioFragment(),
        ImageFragment()
    )
    var fragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchFragment(0)
        findViewById<Button>(R.id.button_prev).setOnClickListener { _ -> switchFragment(-1) }
        findViewById<Button>(R.id.button_next).setOnClickListener { _ -> switchFragment(+1) }
    }

    private fun switchFragment(d: Int) {
        if (d < 0 && fragmentIndex == 0)
            return
        if (d > 0 && fragmentIndex + 1 >= fragments.size)
            return
        fragmentIndex += if (d < 0) -1 else if (d > 0) 1 else 0
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.display, fragments[fragmentIndex])
        fragmentTransaction.commit()
    }
}
