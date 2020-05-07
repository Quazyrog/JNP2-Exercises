package pl.edu.mimuw.students.wm382710.jnp.task01

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

class Note(var title: String = "", var summary: String = "", var content: String = "") {

    companion object {
        public fun readFromStream(stream : DataInputStream) =
            Note(readString(stream), readString(stream), readString(stream))

        private fun readString(stream : DataInputStream) : String {
            val length = stream.readInt()
            val stringData = ByteArray(length)
            if (stream.read(stringData, 0, length) < length)
                throw IOException("Failed to read ${length}-bytes string")
            return String(stringData, StandardCharsets.UTF_8)
        }

        private fun saveString(stream: DataOutputStream, string: String) {
            val stringData = string.toByteArray(StandardCharsets.UTF_8)
            stream.writeInt(stringData.size)
            stream.write(stringData)
        }
    }

    val isNotEmpty : Boolean
        get() = title.isNotEmpty() || summary.isNotEmpty() || content.isNotEmpty()

    public fun saveToStream(stream: DataOutputStream) {
        saveString(stream, title)
        saveString(stream, summary)
        saveString(stream, content)
    }

}