package pl.edu.mimuw.students.wm382710.appa

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.Exception
import java.util.zip.ZipFile

data class VignetteChoice(
    val text: String,
    val outcome: Vignette
)

data class Vignette(
    var title: String,
    var image: Bitmap?,
    var description: String,
    var choices: ArrayList<VignetteChoice>
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
                readAdventure()
                return result as Vignette
            }
        } catch (err: InvalidAdventureFileException) {
            throw err
        } catch (err: IOException) {
            throw InvalidAdventureFileException(err)
        } catch (err: XmlPullParserException) {
            throw InvalidAdventureFileException(err)
        }
    }

    private fun readAdventure() {
        parser.require(START_TAG, namespace, "adventure")
        parser.next()
        while (parser.eventType != END_TAG) {
            if (result === null)
                result = readLocation()
            else
                readLocation()
            ignoreSpace()
        }
    }

    private fun readLocation(): Vignette {
        ignoreSpace()
        parser.require(START_TAG, namespace, "location")
        val id = parser.getAttributeValue(namespace, "id")
        if (id === null)
            throw InvalidAdventureFileException(parser.lineNumber, "location has no id")

        // Create or find location object
        if (definedLocations.containsKey(id))
            throw InvalidAdventureFileException(parser.lineNumber, "location '$id' redefined")
        val loc = undefinedLocations.getOrDefault(id, Vignette("", null, "", ArrayList()))
        loc.title = parser.getAttributeValue(namespace, "title") ?: ""

        // Load image
        val imagePath = parser.getAttributeValue(namespace, "image")
        if (imagePath !== null) {
            val zipEntry = zip.getEntry(imagePath)
            if (zipEntry === null)
                throw InvalidAdventureFileException(parser.lineNumber, "missing zip entry '$imagePath'")
            loc.image = BitmapFactory.decodeStream(zip.getInputStream(zipEntry))
        }
        parser.next()

        loc.description = readText()
        while (parser.eventType != END_TAG) {
            loc.choices.add(readChoice())
            ignoreSpace()
        }

        parser.require(END_TAG, namespace, "location")
        parser.next()

        undefinedLocations.remove(id)
        definedLocations[id] = loc
        println("Read location $id")
        return loc
    }

    private fun readChoice(): VignetteChoice {
        ignoreSpace()
        parser.require(START_TAG, namespace, "choice")
        val outcome = parser.getAttributeValue(namespace, "outcome")
        val outcomeLoc: Vignette = when {
            outcome === null ->
                throw InvalidAdventureFileException(parser.lineNumber, "Choice without outcome")
            definedLocations.containsKey(outcome) ->
                definedLocations.getValue(outcome)
            undefinedLocations.containsKey(outcome) ->
                undefinedLocations.getValue(outcome)
            else -> {
                val loc = Vignette("", null, "", ArrayList())
                undefinedLocations[outcome] = loc
                loc
            }
        }
        parser.next()

        val text = readText()

        parser.require(END_TAG, namespace, "choice")
        parser.next()
        return VignetteChoice(text, outcomeLoc)
    }

    private fun readText(): String {
        var result = ""
        if (parser.eventType == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result.trim()
    }

    private fun ignoreSpace() {
        if (parser.eventType == TEXT && parser.isWhitespace)
            parser.next()
    }
}
