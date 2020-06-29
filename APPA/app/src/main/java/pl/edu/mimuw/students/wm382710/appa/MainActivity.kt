package pl.edu.mimuw.students.wm382710.appa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.util.zip.ZipFile


class HeroesListAdapter(private val activity: MainActivity): RecyclerView.Adapter<HeroesListAdapter.ViewHolder>() {

    var dataset: List<Hero>? = null

    class ViewHolder(root: View, private val activity: MainActivity): RecyclerView.ViewHolder(root) {
        private var myHero: Hero? = null
        private val nameView: TextView = root.findViewById(R.id.heroNameText)
        private val strengthView: TextView = root.findViewById(R.id.strengthText)
        private val intelligenceView: TextView = root.findViewById(R.id.intelligenceText)
        private val dexterityView: TextView = root.findViewById(R.id.dexterityText)
        private val constitutionView: TextView = root.findViewById(R.id.constitutionText)

        init {
            root.findViewById<ImageButton>(R.id.deleteHeroButton).setOnClickListener { activity.onHeroDeleted(hero) }
            nameView.setOnClickListener { activity.onHeroSelected(hero) }
        }

        var hero: Hero
            get() = myHero ?: throw IllegalStateException("No hero was assigned yet to this view")
            set(h) {
                myHero = h
                nameView.text = h.heroName
                strengthView.text = h.strength.toString()
                intelligenceView.text = h.intelligence.toString()
                dexterityView.text = h.dexterity.toString()
                constitutionView.text = h.constitution.toString()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hero_summary, parent, false)
        return ViewHolder(view, activity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hero = dataset!![position]
    }

    override fun getItemCount() = if (dataset !== null) dataset!!.size else 0

    var data: List<Hero>
        get() = dataset ?: ArrayList()
        set(value) {
            dataset = value
            MainScope().launch { notifyDataSetChanged() }
        }
}


class AdventuresListAdapter(private val activity: MainActivity): RecyclerView.Adapter<AdventuresListAdapter.ViewHolder>() {

    var dataset: List<AdventureMetadata>? = null

    class ViewHolder(root: View, private val activity: MainActivity): RecyclerView.ViewHolder(root) {
        private var myAdventure: AdventureMetadata? = null
        private val nameView: TextView = root.findViewById(R.id.adventureTitle)
        private val descriptionView: TextView = root.findViewById(R.id.adventureDescription)

        init {
            root.setOnClickListener { activity.startAdventure(adventure) }
        }

        var adventure: AdventureMetadata
            get() = myAdventure ?: throw IllegalStateException("No hero was assigned yet to this view")
            set(h) {
                myAdventure = h
                nameView.text = h.title
                h.description.let { descriptionView.text = it }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adventure_summary, parent, false)
        return ViewHolder(view, activity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.adventure = dataset!![position]
    }

    override fun getItemCount() = if (dataset !== null) dataset!!.size else 0

    var data: List<AdventureMetadata>
        get() = dataset ?: ArrayList()
        set(value) {
            dataset = value
            MainScope().launch { notifyDataSetChanged() }
        }
}


class MainActivity : AppCompatActivity() {
    private val heroesAdapter = HeroesListAdapter(this)
    private val adventuresAdapter = AdventuresListAdapter(this)
    private var selectedHero: Hero? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHeroSelection()
    }

    fun onHeroCreationRequested() {
        val intent = Intent(this, CreateHeroActivity::class.java)
        startActivityForResult(intent, 1)
        println("Hero created or not, refreshing the list!")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        GlobalScope.launch {
            val db = AppaDatabase.getDatabase(applicationContext)
            heroesAdapter.data = db.heroes().selectAllHeroes()
        }
    }

    fun onHeroDeleted(hero: Hero) {
        GlobalScope.launch {
            val db = AppaDatabase.getDatabase(applicationContext)
            db.heroes().deleteHero(hero)
            heroesAdapter.data = db.heroes().selectAllHeroes()
        }
    }

    fun onHeroSelected(hero: Hero) {
        selectedHero = hero
        showAdventureSelection()
    }

    private fun showHeroSelection() {
        setContentView(R.layout.hero_selection)
        findViewById<Button>(R.id.createHeroButton).setOnClickListener { onHeroCreationRequested() }
        findViewById<RecyclerView>(R.id.heroesRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.heroesAdapter
        }

        GlobalScope.launch {
            val db = AppaDatabase.getDatabase(applicationContext)
            heroesAdapter.data = db.heroes().selectAllHeroes()
        }
    }

    private fun showAdventureSelection() {
        setContentView(R.layout.adventure_selection)
        findViewById<Button>(R.id.importButton).setOnClickListener {
            val req = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            importFile(it)
                        } catch (e: Exception) {
                            MainScope().launch {
                                Toast.makeText(this@MainActivity, "Invalid zip archive", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }.launch("application/zip")
        }
        findViewById<RecyclerView>(R.id.adventuresRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adventuresAdapter
        }
        GlobalScope.launch {
            val db = AppaDatabase.getDatabase(applicationContext)
            adventuresAdapter.data = db.adventures().selectAllMetadata()
        }
    }

    suspend fun importFile(uri: Uri) {
        val db = AppaDatabase.getDatabase(this@MainActivity)
        val metaId = db.adventures().saveMetadata(AdventureMetadata(0, "", null)).toInt()

        val copyName = "Adv_%02d.zip".format(metaId)
        val inStream = contentResolver.openInputStream(uri)
        val zipFile = File(cacheDir.path, copyName)
        if (inStream === null)
            throw RuntimeException("Unable to open stream")

        val outStream = FileOutputStream(zipFile)
        val buffer = ByteArray(1000)
        var len = 0
        while (inStream.read(buffer).also { len = it } != -1)
            outStream.write(buffer, 0, len)

        outStream.close()
        inStream.close()

        val zip = ZipFile(zipFile)
        val metaEntry = zip.getEntry("Meta.txt")
        if (metaEntry === null)
            throw RuntimeException("Unable to open Meta.txt")
        val metaReader = BufferedReader(InputStreamReader(zip.getInputStream(metaEntry)))
        val title = metaReader.readLine()
        val description = metaReader.readLine()
        metaReader.close()

        db.adventures().saveMetadata(AdventureMetadata(metaId, title, description))
        adventuresAdapter.data = db.adventures().selectAllMetadata()
    }

    fun startAdventure(adventure: AdventureMetadata) {
        val intent = Intent(this, AdventureActivity::class.java)
        val copyName = "Adv_%02d.zip".format(adventure.adventureId)
        val zipFile = File(cacheDir.path, copyName)
        intent.putExtra("zipFileName", zipFile.toString())
        intent.putExtra("heroId", selectedHero!!.heroId)
        startActivity(intent)
        finish()
    }
}
