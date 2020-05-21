
package pl.edu.mimuw.students.wm382710.jnp.task01

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class MyAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    var dataset: List<NoteMetadata>? = null

    class MyViewHolder(val root: ConstraintLayout): RecyclerView.ViewHolder(root) {
        val titleView: TextView = root.findViewById(R.id.textTitle)
        val dateView: TextView = root.findViewById(R.id.textDate)
        val summaryView: TextView = root.findViewById(R.id.textSummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.metadata_view, parent, false) as ConstraintLayout
        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val md = dataset!![position]
        holder.root.setBackgroundColor(if (position % 2 == 0) Color.GRAY else Color.LTGRAY)
        holder.titleView.text = md.title
        holder.dateView.text = Date(md.modificationDate).toString()
        holder.summaryView.text = md.summary
    }

    override fun getItemCount() = if (dataset !== null) dataset!!.size else 0

    fun setData(data: List<NoteMetadata>) {
        dataset = data
        notifyDataSetChanged()
    }
}


class MetadataActivity: AppCompatActivity() {

    companion object {
        var database: NoteDatabase? = null
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metadata)

        val layout = LinearLayoutManager(this)
        viewAdapter = MyAdapter()
        recyclerView = findViewById<RecyclerView>(R.id.metadataView).apply {
            setHasFixedSize(true)
            layoutManager = layout
            adapter = viewAdapter
        }
        refreshNotes()

        findViewById<View>(R.id.addNote).setOnClickListener {
            GlobalScope.launch {
                val dao = getDatabase().noteDao()
                dao.saveNote(Note(null, "Test note", "Something i need to test", Date(), ""))
            }
            refreshNotes()
        }
    }

    private fun refreshNotes() {
        GlobalScope.launch {
            val dao = getDatabase().noteDao()
            MainScope().launch {
                viewAdapter.setData(dao.loadAllMetadata())
            }
        }
    }

    private fun getDatabase(): NoteDatabase {
        if (database === null)
            database = Room.databaseBuilder(applicationContext, NoteDatabase::class.java, "notes.db").build()
        return database!!
    }

}
