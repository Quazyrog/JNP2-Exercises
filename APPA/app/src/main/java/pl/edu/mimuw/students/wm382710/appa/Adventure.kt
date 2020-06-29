package pl.edu.mimuw.students.wm382710.appa

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.*
import org.xmlpull.v1.XmlPullParserException
import pl.edu.mimuw.students.wm382710.appa.maps.KmlReader
import pl.edu.mimuw.students.wm382710.appa.maps.TargetLocation
import java.io.IOException
import java.lang.Exception
import java.util.zip.ZipFile

data class Requirements(
    val intelligence: Int = 0,
    val strength: Int = 0,
    val dexterity: Int = 0,
    val constitution: Int = 0,
    val item: String? = null
) {
    fun check(h: HeroWithInventory): Boolean {
        if (h.hero.intelligence < intelligence)
            return false
        if (h.hero.strength < strength)
            return false
        if (h.hero.dexterity < dexterity)
            return false
        if (h.hero.constitution < constitution)
            return false
        if (item !== null && !h.hasItem(item))
            return false
        return true
    }
}

data class VignetteChoice(
    val text: String,
    val outcome: Vignette,
    val requirements: Requirements = Requirements()
)

data class Vignette(
    var title: String,
    var targetLocation: TargetLocation?,
    var image: Bitmap?,
    var description: String,
    var choices: ArrayList<VignetteChoice>,
    var grantItem: String? = null
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
    var mapLocations: Map<String, TargetLocation> = HashMap()

    constructor(input: ZipFile) {
        zip = input
    }

    fun adventure(): Vignette {
        if (result !== null)
            return result as Vignette

        val mapEntry = zip.getEntry("Map.kml")
        if (mapEntry !== null) {
            val p = KmlReader(zip.getInputStream(mapEntry))
            p.read()
            mapLocations = p.locations
        }

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
        val loc = undefinedLocations.getOrDefault(id, Vignette("", null, null, "", ArrayList()))
        loc.title = parser.getAttributeValue(namespace, "title") ?: ""

        // Load image
        val imagePath = parser.getAttributeValue(namespace, "image")
        if (imagePath !== null) {
            val zipEntry = zip.getEntry(imagePath)
            if (zipEntry === null)
                throw InvalidAdventureFileException(parser.lineNumber, "missing zip entry '$imagePath'")
            loc.image = BitmapFactory.decodeStream(zip.getInputStream(zipEntry))
        }

        // Set target map location
        val mapLoc = parser.getAttributeValue(namespace, "mapLocation")
        if (mapLoc !== null) {
            if (!mapLocations.containsKey(mapLoc))
                throw InvalidAdventureFileException(parser.lineNumber, "Undefined map location '$mapLoc'")
            loc.targetLocation = mapLocations[mapLoc]
        }

        // Item maybe?
        parser.getAttributeValue(namespace, "item")?.let { loc.grantItem = it }
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
                val loc = Vignette("", null, null, "", ArrayList())
                undefinedLocations[outcome] = loc
                loc
            }
        }

        // Requirements
        var requirements = Requirements()
        parser.getAttributeValue(namespace, "reqInt")?.let { requirements = requirements.copy(intelligence = it.toInt()) }
        parser.getAttributeValue(namespace, "reqDex")?.let { requirements = requirements.copy(dexterity = it.toInt()) }
        parser.getAttributeValue(namespace, "reqStr")?.let { requirements = requirements.copy(strength = it.toInt()) }
        parser.getAttributeValue(namespace, "reqCon")?.let { requirements = requirements.copy(constitution = it.toInt()) }
        parser.getAttributeValue(namespace, "reqItem")?.let { requirements = requirements.copy(item = it) }

        parser.next()
        val text = readText()

        parser.require(END_TAG, namespace, "choice")
        parser.next()
        return VignetteChoice(text, outcomeLoc, requirements)
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
