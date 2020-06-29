package pl.edu.mimuw.students.wm382710.appa

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.zip.ZipFile

class AdventureActivity : AppCompatActivity() {
    private lateinit var loc: Vignette
    var heroId: Int = 0
    private var hero: HeroWithInventory? = null
    private val addedItems: ArrayList<Item> = ArrayList()
    private var arrived = true

    var location: Vignette
        get() = loc
        private set(v: Vignette) {
            loc = v
            if (v.targetLocation != null) {
                arrived = false
                showNavigation()
            } else {
                arrived = true
                showVignette()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<Button>(R.id.heroButton).setOnClickListener { showHero() }
        findViewById<Button>(R.id.navigationButton).setOnClickListener {
            if (!arrived) showNavigation() else showVignette()
        }
    }

    override fun onResume() {
        super.onResume()
        heroId = intent.getIntExtra("heroId", 0)
        if (heroId != 0) {
            GlobalScope.launch {
                val db = AppaDatabase.getDatabase(applicationContext)
                hero = db.heroes().selectHeroWithInventory(heroId)
                MainScope().launch { loadAdventure(intent.getStringExtra("zipFileName")!!) }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        GlobalScope.launch {
            val db = AppaDatabase.getDatabase(applicationContext)
            db.heroes().insertItems(addedItems)
            addedItems.clear()
        }
    }

    private fun loadAdventure(zipFilePath: String) {
        val reader = AdventureReader(ZipFile(zipFilePath))
        location = reader.adventure()
    }

    private fun showNavigation() {
        require(loc.targetLocation !== null)
        if (arrived) {
            showVignette()
            return
        }

        val transaction = supportFragmentManager.beginTransaction()

        val fragment = NavigationFragment()
        fragment.targetLocation = loc.targetLocation

        transaction.replace(R.id.centralWidget, fragment)
        transaction.commit()

        fragment.onArrive { showVignette() }
    }

    private fun showVignette() {
        val transition = supportFragmentManager.beginTransaction()

        val fragment = VignetteFragment()
        fragment.vignette = loc

        transition.replace(R.id.centralWidget, fragment)
        transition.commit()

        fragment.onSelect { index -> location = loc.choices[index].outcome }
    }

    private fun showHero() {
        if (hero === null)
            return

        val transition = supportFragmentManager.beginTransaction()

        val fragment = HeroInfoFragment()
        fragment.hero = hero!!

        transition.replace(R.id.centralWidget, fragment)
        transition.commit()
    }
}