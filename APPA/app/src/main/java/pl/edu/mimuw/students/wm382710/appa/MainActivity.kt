package pl.edu.mimuw.students.wm382710.appa

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile


const val PICK_ADVENTURE_ZIP = 1

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { openFile(it) }
        }.launch("application/zip")
    }

    fun openFile(uri: Uri) {
        val copyName = "test.zip"
        val inStream = contentResolver.openInputStream(uri)
        val zipFile = File(cacheDir.path, copyName)
        if (inStream === null)
            return

        val outStream = FileOutputStream(zipFile)
        val buffer = ByteArray(1000)
        var len = 0
        while (inStream.read(buffer).also { len = it } != -1)
            outStream.write(buffer, 0, len)

        outStream.close()
        inStream.close()

        startAdventure(ZipFile(zipFile))
    }

    fun startAdventure(zip: ZipFile) {

    }

}
