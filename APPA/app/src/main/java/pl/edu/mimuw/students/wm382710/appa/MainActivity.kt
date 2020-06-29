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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
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


class MainActivity : AppCompatActivity() {
    val adapter = HeroesListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.createHeroButton).setOnClickListener { onHeroCreationRequested() }
        findViewById<RecyclerView>(R.id.heroesRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        GlobalScope.launch {
            val db = AppaDatabase.getDatabase(applicationContext)
            adapter.data = db.heroes().selectAllHeroes()
        }
    }

    fun onHeroCreationRequested() {
        GlobalScope.launch {
            val hero = Hero(0, "X the D")
            val db = AppaDatabase.getDatabase(applicationContext)
            db.heroes().insertHero(hero)
            adapter.data = db.heroes().selectAllHeroes()
        }
    }

    fun onHeroDeleted(hero: Hero) {

    }

    fun onHeroSelected(hero: Hero) {
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { openFile(it) }
        }.launch("application/zip")
    }

    private fun openFile(uri: Uri) {
        val copyName = "test.zip"
        val inStream = contentResolver.openInputStream(uri)
        val zipFile = File(cacheDir.path, copyName)
        if (inStream === null)
            return

        val outStream = FileOutputStream(zipFile)
        val buffer = ByteArray(1000)
        var len = 0
        while (inStream.read(buffer).also { len = it } != -1)
            outStream.write(buffer, 0, len)

        outStream.close()
        inStream.close()

        startAdventure(zipFile.path)
    }

    private fun startAdventure(zipFileName: String) {
        val intent = Intent(this, AdventureActivity::class.java)
        intent.putExtra("zipFileName", zipFileName)
        startActivity(intent)
        finish()
    }
}
