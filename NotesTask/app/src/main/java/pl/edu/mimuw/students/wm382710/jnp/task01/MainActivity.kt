package pl.edu.mimuw.students.wm382710.jnp.task01

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import kotlin.collections.ArrayList

private enum class NoteSwitchDirection {
    PREVIOUS,
    NEXT
}

class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTES_FILE_NAME = "Notes.dat";
    }

    private var disableSaveNotes = false
    private lateinit var notes: ArrayList<Note>
    private var position = -1
    private lateinit var currentNote: Note

    private lateinit var titleEdit : EditText
    private lateinit var summaryEdit : EditText
    private lateinit var contentEdit : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        titleEdit = findViewById(R.id.editName)
        summaryEdit = findViewById(R.id.editSummary)
        contentEdit = findViewById(R.id.editText)
        findViewById<Button>(R.id.btnNext).setOnClickListener { _ -> switchNote(NoteSwitchDirection.NEXT)}
        findViewById<Button>(R.id.btnPrevious).setOnClickListener { _ -> switchNote(NoteSwitchDirection.PREVIOUS)}

        loadNotes()
        position = when (notes.size) {
            0 -> { notes.add(Note()); 0 }
            else -> { notes.add(0, Note()); notes.add(notes.size, Note()); 1 }
        }
        showNote()
    }

    override fun onPause() {
        super.onPause()
        saveNotes()
    }

    private fun switchNote(direction: NoteSwitchDirection) {
        currentNote.title = titleEdit.text.toString()
        currentNote.summary = summaryEdit.text.toString()
        currentNote.content = contentEdit.text.toString()

        if (currentNote === notes.first() && currentNote.isNotEmpty)
            notes.add(0, Note())
        if (currentNote === notes.last() && currentNote.isNotEmpty)
            notes.add(notes.size, Note())
        if (currentNote !== notes.first() && currentNote !== notes.last() && !currentNote.isNotEmpty) {
            notes.removeAt(position)
            position = min(notes.size - 1, max(0, position))
        }

        position += if (direction == NoteSwitchDirection.NEXT) 1 else -1
        position = min(notes.size - 1, max(0, position))

        showNote()
    }

    private fun showNote() {
        currentNote = notes[position]
        titleEdit.setText(currentNote.title)
        summaryEdit.setText(currentNote.summary)
        contentEdit.setText(currentNote.content)
        println("Showing note $position: '${currentNote.title}'")
    }

    private fun loadNotes() {
        notes = ArrayList<Note>()
        try {
            val file = openFileInput(NOTES_FILE_NAME)
            val inputStream = DataInputStream(file)
            val notesToLoad = inputStream.readInt()
            for (n in 1 .. notesToLoad)
                notes.add(Note.readFromStream(inputStream))
            println("Loaded $notesToLoad from file")
            inputStream.close()
        } catch (ignored : FileNotFoundException) {
        } catch (e : IOException) {
            val builder = AlertDialog.Builder(this)
            builder
                .setMessage("The application failed to read notes from data file. You can either continue with empty " +
                        "notes list (and loose previous ones) or exit the app.")
                .setNegativeButton("Exit", DialogInterface.OnClickListener { _, _ -> finishAffinity()})
                .setPositiveButton("Continue", DialogInterface.OnClickListener { _, _ ->
                    notes.clear();
                    disableSaveNotes = true
                })
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun saveNotes() {
        try {
            val outputStream = DataOutputStream(openFileOutput(NOTES_FILE_NAME, Context.MODE_PRIVATE))
            var notesToSave = 0
            for (note in notes) {
                if (note.isNotEmpty)
                    ++notesToSave
            }
            outputStream.writeInt(notesToSave)
            for (note in notes) {
                if (note.isNotEmpty)
                    note.saveToStream(outputStream)
            }
            println("Saved $notesToSave notes to file")
            outputStream.close()
        } catch (e: IOException) {
            println("Error when saving notes: ${e.toString()}")
        }
    }

}
