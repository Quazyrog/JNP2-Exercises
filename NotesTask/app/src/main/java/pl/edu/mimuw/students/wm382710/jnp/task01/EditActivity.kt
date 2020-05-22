package pl.edu.mimuw.students.wm382710.jnp.task01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val EXTRA_NOTE = "pl.edu.mimuw.students.wm382710.jnp.task01.NOTE"
const val EXTRA_NOTE_ID = "pl.edu.mimuw.students.wm382710.jnp.task01.NOTE_ID"

class EditActivity : AppCompatActivity() {
    lateinit var note: Note
    var noteChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val rcvNote: Note? = intent.getParcelableExtra(EXTRA_NOTE)
        when {
            rcvNote !== null -> {
                setEditedNote(rcvNote)
            }
            intent.hasExtra(EXTRA_NOTE_ID) -> {
                val id = intent.getIntExtra(EXTRA_NOTE_ID, 0)
                require(id > 0) { "No valid note nor note id in intent" }
                GlobalScope.launch {
                    val dao = NoteDatabase.getDatabase(applicationContext).noteDao()
                    val note = dao.loadNote(id)
                    MainScope().launch { setEditedNote(note) }
                }
            }
            else -> {
                throw IllegalArgumentException("No valid note nor note id in intent")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        flushEditedNote()
        if (noteChanged) {
            GlobalScope.launch {
                val dao = NoteDatabase.getDatabase(applicationContext).noteDao()
                if (note.isNotEmpty)
                    note = dao.saveNote(note)
                else if (note.hasId)
                    dao.deleteNote(note)
            }
        }
    }

    private fun flushEditedNote() {
        val title = editTitle.text.toString()
        val summary = editSummary.text.toString()
        val content = editContentText.text.toString()
        noteChanged = title != note.title || summary != note.summary || content != note.content
        note.title = title
        note.summary = summary
        note.content = content
    }

    private fun setEditedNote(n: Note) {
        note = n
        editTitle.setText(note.title)
        editSummary.setText(note.summary)
        editContentText.setText(note.content)
    }
}
