package pl.edu.mimuw.students.wm382710.jnp.task01

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import androidx.room.*

@Entity(indices = [Index(value = ["modificationDate"])])
data class NoteMetadata(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var title: String,
    var summary: String,
    var modificationDate: Long
)

@Entity(foreignKeys = [ForeignKey(entity = NoteMetadata::class, parentColumns = ["id"], childColumns = ["noteId"], onDelete = ForeignKey.CASCADE)])
data class NoteContents(
    @PrimaryKey val noteId: Int,
    val text: String
)


data class Note(
    val id: Int?,
    var title: String,
    var summary: String,
    var modificationDate: Date,
    var content: String
) {
    val isNotEmpty: Boolean
        get() = title.isNotEmpty() || summary.isNotEmpty() || content.isNotEmpty()
}

@Dao
abstract class NoteDao {
    @Query("SELECT * FROM notemetadata WHERE id = :id")
    abstract fun loadMetadata(id: Int): NoteMetadata

    @Query("SELECT * FROM notemetadata")
    abstract suspend fun loadAllMetadata(): List<NoteMetadata>

    @Transaction
    open fun loadNote(id: Int): Note {
        require(id > 0)
        val metadata = loadMetadata(id)
        val contents = loadContents(id)
        return Note(id, metadata.title, metadata.summary, Date(metadata.modificationDate), contents.text)
    }

    @Transaction
    open fun saveNote(note: Note): Note {
        note.modificationDate = Date()
        val id = saveMetadata(NoteMetadata(note.id ?: 0, note.title, note.summary, note.modificationDate.time)).toInt()
        saveContents(NoteContents(id, note.content))
        return note.copy(id = id)
    }

    fun deleteNote(note: Note) {
        require(note.id !== null)
        deleteMetadata(NoteMetadata(note.id!!, note.title, note.summary, note.modificationDate.time))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveMetadata(metadata: NoteMetadata): Long

    @Delete
    protected abstract fun deleteMetadata(metadata: NoteMetadata)

    @Query("SELECT * FROM notecontents WHERE noteId = :id")
    protected abstract fun loadContents(id: Int): NoteContents

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveContents(metadata: NoteContents)
}

@Database(entities = [NoteMetadata::class, NoteContents::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
