package pl.edu.mimuw.students.wm382710.appa.maps

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.lang.Exception

class KmlError: Exception {
    constructor(msg: String): super(msg)
    constructor(line: Int, msg: String): super("Main.xml:$line:  $msg")
    constructor(cause: Exception): super(cause)
}

class KmlReader(private val stream: InputStream) {
    val locations = HashMap<String, TargetLocation>()
    private val parser = Xml.newPullParser()

    fun read() {
        stream.use {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            readRoot()
        }
    }

    private fun readRoot() {
        skipSpace()
        parser.require(XmlPullParser.START_TAG, null, "kml")
        parser.next()

        readDocument()

        skipSpace()
        parser.require(XmlPullParser.END_TAG, null, "kml")
        parser.next()
    }

    private fun readDocument() {
        skipSpace()
        parser.require(XmlPullParser.START_TAG, null, "Document")
        parser.next()

        skipSpace()
        while (parser.eventType != XmlPullParser.END_TAG) {
            when (parser.name) {
                "Placemark" -> readPlacemark()
                else -> skipTag()
            }
            skipSpace()
        }

        parser.require(XmlPullParser.END_TAG, null, "Document")
        parser.next()
    }

    private fun readPlacemark() {
        skipSpace()
        parser.require(XmlPullParser.START_TAG, null, "Placemark")
        parser.next()

        var name: String? = null
        var target: TargetLocation? = null

        skipSpace()
        while (parser.eventType != XmlPullParser.END_TAG) {
            when (parser.name) {
                "name" -> name = readName()
                "Point" -> target = readPoint()
                else -> skipTag()
            }
            skipSpace()
        }
        println("$name=$target")

        skipSpace()
        parser.require(XmlPullParser.END_TAG, null, "Placemark")
        parser.next()
    }

    private fun readPoint(): Point2D {
        skipSpace()
        parser.require(XmlPullParser.START_TAG, null, "Point")
        parser.next()

        skipSpace()
        parser.require(XmlPullParser.START_TAG, null, "coordinates")
        parser.next()

        val coords = readText().split(",").slice(0 .. 1).map { it.toDouble() }

        skipSpace()
        parser.require(XmlPullParser.END_TAG, null, "coordinates")
        parser.next()

        skipSpace()
        parser.require(XmlPullParser.END_TAG, null, "Point")
        parser.next()

        return Point2D(coords[0], coords[1])
    }

    private fun readName(): String {
        skipSpace()
        parser.require(XmlPullParser.START_TAG, null, "name")
        parser.next()

        val name = readText()

        skipSpace()
        parser.require(XmlPullParser.END_TAG, null, "name")
        parser.next()

        return name.trim()
    }

    private fun readText(): String {
        var result = ""
        if (parser.eventType == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result.trim()
    }

    private fun skipSpace() {
        if (parser.eventType == XmlPullParser.TEXT && parser.isWhitespace)
            parser.next()
    }

    private fun skipTag() {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
        parser.next()
    }
}