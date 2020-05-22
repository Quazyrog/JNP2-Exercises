package pl.edu.mimuw.students.wm382710.jnp.task01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val EXTRA_NOTE = "pl.edu.mimuw.students.wm382710.jnp.task01.NOTE"

class EditActivity : AppCompatActivity() {
    lateinit var note: Note
    var noteChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val rcvNote: Note? = intent.getParcelableExtra(EXTRA_NOTE)
        requireNotNull(rcvNote)
        note = rcvNote

        editTitle.setText(note.title)
        editSummary.setText(note.summary)
        editContentText.setText(note.content)
    }

    override fun onPause() {
        super.onPause()
        flushNote()
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

    private fun flushNote() {
        val title = editTitle.text.toString()
        val summary = editSummary.text.toString()
        val content = editContentText.text.toString()
        noteChanged = title != note.title || summary != note.summary || content != note.content
        note.title = title
        note.summary = summary
        note.content = content
    }
}
