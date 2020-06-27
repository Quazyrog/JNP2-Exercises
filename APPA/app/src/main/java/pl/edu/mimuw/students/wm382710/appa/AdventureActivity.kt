package pl.edu.mimuw.students.wm382710.appa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.zip.ZipFile

class AdventureActivity : AppCompatActivity() {
    private lateinit var loc: Vignette

    var location: Vignette
        get() = loc
        private set(v: Vignette) {
            if (v.targetLocation != null)
                showNavigation(v)
            else
                showVignette(v)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure)
        setSupportActionBar(findViewById(R.id.toolbar))
        loadAdventure(intent.getStringExtra("zipFileName")!!)
    }

    private fun loadAdventure(zipFilePath: String) {
        val reader = AdventureReader(ZipFile(zipFilePath))
        location = reader.adventure()
    }

    private fun showNavigation(vignette: Vignette) {
        require(vignette.targetLocation !== null)
        val transaction = supportFragmentManager.beginTransaction()

        val fragment = NavigationFragment()
        fragment.targetLocation = vignette.targetLocation

        transaction.replace(R.id.coordinator, fragment)
        transaction.commit()

        fragment.onArrive { showVignette(vignette) }
    }

    private fun showVignette(vignette: Vignette) {
        val transition = supportFragmentManager.beginTransaction()

        val fragment = VignetteFragment()
        fragment.vignette = vignette

        transition.replace(R.id.coordinator, fragment)
        transition.commit()

        fragment.onSelect { index -> location = vignette.choices[index].outcome }
    }
}