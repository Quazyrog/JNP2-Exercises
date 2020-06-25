package pl.edu.mimuw.students.wm382710.appa

import android.graphics.Bitmap
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.Exception
import java.util.zip.ZipFile

data class VignetteChoice(
    val text: String,
    val outcome: Vignette
)

data class Vignette(
    val title: String?,
    val image: Bitmap?,
    val description: String,
    val choices: List<VignetteChoice>
)

class InvalidAdventureFileException: IOException {
    constructor(msg: String): super(msg)
    constructor(line: Int, msg: String): super("Main.xml:$line:  $msg")
    constructor(cause: Exception): super(cause)
}

class AdventureReader {
    companion object {
        val namespace: String? = null
    }

    val zip: ZipFile
    val parser = Xml.newPullParser()

    val undefinedLocations = HashMap<String, Vignette>()
    val definedLocations = HashMap<String, Vignette>()

    var result: Vignette? = null

    constructor(input: ZipFile) {
        zip = input

    }

    fun adventure(): Vignette {
        if (result !== null)
            return result as Vignette
        val entry = zip.getEntry("Main.xml")
        if (entry === null)
            throw InvalidAdventureFileException("No file Main.xml in zip")
        try {
            zip.getInputStream(entry).use {
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(it, null)
                parser.nextTag()
                return result as Vignette
            }
        } catch (err: IOException) {
            throw InvalidAdventureFileException(err)
        } catch (err: XmlPullParserException) {
            throw InvalidAdventureFileException(err)
        }
    }

    private fun readAdventure() {
        parser.require(START_TAG, namespace, "adventure")
        while (parser.next() != END_TAG) {
            if (result === null)
                result = readLocation()
            else
                readLocation()
        }
    }

    private fun readLocation(): Vignette {
        parser.require(START_TAG, namespace, "location")
        val id = parser.getAttributeValue(namespace, "id")
        if (id === null)
            throw InvalidAdventureFileException(parser.lineNumber, "location has no id")
        val title = parser.getAttributeValue(namespace, "title")

        if (definedLocations.containsKey(id))
            throw InvalidAdventureFileException(parser.lineNumber, "location '$id' redefined")
        val

        while (parser.next() != END_TAG) {

        }
    }

    private fun readText(): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}
