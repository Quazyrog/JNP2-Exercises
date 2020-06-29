package pl.edu.mimuw.students.wm382710.appa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CreateHeroActivity : AppCompatActivity() {
    private val statMin = 3
    private var statSum = 12
    private val statMaxSum = 24

    private lateinit var str: StatEditFragment
    private lateinit var con: StatEditFragment
    private lateinit var dex: StatEditFragment
    private lateinit var int: StatEditFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_hero)

        str = createStatEdit("Strength")
        con = createStatEdit("Constitution")
        dex = createStatEdit("Dexterity")
        int = createStatEdit("Intelligence")

        findViewById<Button>(R.id.saveHeroButton).setOnClickListener {
            val name = findViewById<EditText>(R.id.editHeroName).text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (statSum < statMaxSum) {
                Toast.makeText(this, "You have unallocated stats points", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            GlobalScope.launch {
                val db = AppaDatabase.getDatabase(applicationContext)
                db.heroes().insertHero(Hero(0, name,
                    strength = str.value,
                    constitution = con.value,
                    dexterity = dex.value,
                    intelligence = int.value))
                println("Hero saved")
                MainScope().launch { finish() }
            }
        }
    }

    private fun createStatEdit(name: String): StatEditFragment {
        val transaction = supportFragmentManager.beginTransaction()
        val edit = StatEditFragment(name, statMin)
        edit.acceptChange = { _, old, new -> statSum + new - old <= statMaxSum }
        edit.onChanged = { _, old, new ->
            statSum += new - old
            findViewById<TextView>(R.id.statsHeader).text = "Unallocated points: ${statMaxSum - statSum}"
        }
        transaction.add(R.id.statContainer, edit)
        transaction.commit()
        return edit
    }
}