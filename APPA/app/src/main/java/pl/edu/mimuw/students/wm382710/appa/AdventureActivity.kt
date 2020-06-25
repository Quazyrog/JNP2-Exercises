package pl.edu.mimuw.students.wm382710.appa

import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import java.util.zip.ZipFile

class AdventureActivity : AppCompatActivity() {
    lateinit var location: Vignette
    lateinit var vignetteFragment: VignetteFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure)
        setSupportActionBar(findViewById(R.id.toolbar))
        loadAdventure(intent.getStringExtra("zipFileName")!!)
    }

    private fun loadAdventure(zipFilePath: String) {
        val reader = AdventureReader(ZipFile(zipFilePath))
        location = reader.adventure()
        vignetteFragment = supportFragmentManager.findFragmentById(R.id.vignetteFragment) as VignetteFragment
        showVignette(location)
    }

    private fun showVignette(vignette: Vignette) {
        vignetteFragment.onSelect { index -> showVignette(vignette.choices[index].outcome) }
        vignetteFragment.showVignette(vignette)
        location = vignette
    }
}