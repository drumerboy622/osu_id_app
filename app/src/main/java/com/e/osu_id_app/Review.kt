package com.e.osu_id_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import java.io.File
import kotlinx.android.synthetic.main.activity_review.imageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.view.TextureView
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import junit.framework.TestCase
import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.StringUtils
import org.junit.Test
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.concurrent.Executors


class Review : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get Values from last activity
        val barcode: String = intent.getStringExtra("student_barcode") as String
        val fileName: String = intent.getStringExtra("FileName") as String
        var textView2 = findViewById<TextView>(R.id.textView12)

        val savedFileName: String
        try {
            savedFileName = intent.getStringExtra("SavedFileName") as String
            textView2.text = savedFileName
        } catch (e: Exception){
            textView2.text = fileName
        }




        val filePath: String = intent.getStringExtra("FilePath") as String
        val path: String = intent.getStringExtra("Path") as String

        println(filePath)

        // Get Date file last updated
        val file = File(filePath)
        val dateFile = file.lastModified()
        val reformat = SimpleDateFormat("MM/dd/yyyy h:m a ")
        val actual = reformat.format(dateFile)
        var textView3 = findViewById<TextView>(R.id.textView13)
        textView3.text = actual

        //Display Barcode
        var textView = findViewById<TextView>(R.id.textView11)
        textView.text = barcode


        var a: A=A(file)


        // View image
        val bmp =
            BitmapFactory.decodeFile(filePath)
        val rotatedBitmap = bmp.rotate(90)
        imageView.setImageBitmap(rotatedBitmap)


        // Keep Button
        val kButton = findViewById<ImageButton>(R.id.imageButton2)

        kButton.setOnClickListener {
            val intent = Intent(this, barcode_scan::class.java)
            // Create connection parameters

            if(path == "sent") {
                a.start()
            }

            intent.putExtra("FileName", fileName)
            intent.putExtra("Path", path)

            startActivity(intent)
        }
        // Retake Button
        val rButton = findViewById<ImageButton>(R.id.imageButton3)

        rButton.setOnClickListener {

            val builder = AlertDialog.Builder(this@Review)
            builder.setTitle("Retake Photo")
            builder.setMessage("Are you sure you would like to RETAKE this photo?")

            //Yes Button
            builder.setPositiveButton("Yes"){dialog, which -> run {
                // Send intent Extras student_barcode and FileName
                val intent = Intent(this, photo_session::class.java)

                file.delete()

                intent.putExtra("student_barcode", barcode)
                intent.putExtra("FileName", fileName)
                intent.putExtra("Path", path)
                startActivity(intent)
            }}
            // Display a neutral button on alert dialog
            builder.setNeutralButton("Cancel"){_,_ -> }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()

        }
        // Delete Button
        val dButton = findViewById<ImageButton>(R.id.imageButton)

        dButton.setOnClickListener {

            val builder = AlertDialog.Builder(this@Review)

            builder.setTitle("Delete Photo")
            builder.setMessage("Are you sure you would like to DELETE this photo?")

            //Yes Button
            builder.setPositiveButton("Yes"){dialog, which -> run {

                val intent = Intent(this, barcode_scan::class.java)

                //delete file
                file.delete()

                intent.putExtra("FileName", fileName)
                intent.putExtra("Path", path)
                startActivity(intent)
            }}

            // Display a neutral button on alert dialog
            builder.setNeutralButton("Cancel"){_,_ -> }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()

        }
    }



}

fun Bitmap.rotate(degree:Int):Bitmap{
    // Initialize a new matrix
    val matrix = Matrix()

    // Rotate the bitmap
    matrix.postRotate(degree.toFloat())

    // Resize the bitmap
    val scaledBitmap = Bitmap.createScaledBitmap(
        this,
        width,
        height,
        true
    )

    // Create and return the rotated bitmap
    return Bitmap.createBitmap(
        scaledBitmap,
        0,
        0,
        scaledBitmap.width,
        scaledBitmap.height,
        matrix,
        true
    )
}

//threading upload to background
class A(val file: File) : Thread()
{
    override fun run() {
        // Upload file
        sftpClient = SftpClient.create(createConnectionParameters())

        // Upload to SFTP
        sftpClient!!.upload(file.absolutePath, remoteDirectoryForUploads, 300);


    }

    // These environment variables must be defined on your machine
    private val ENVIRONMENT_VARIABLE_HOST = "KSFTP_HOST"
    private val ENVIRONMENT_VARIABLE_PORT = "KSFTP_PORT"
    private val ENVIRONMENT_VARIABLE_USERNAME = "KSFTP_USERNAME"
    private val ENVIRONMENT_VARIABLE_PASSWORD = "KSFTP_PASSWORD"

    // Remote directory for upload - a folder at the user's root level on SFTP server
// Directory will be created if it does not exist
    private val remoteDirectoryForUploads = "Folder2new/"

    private var testFiles: Array<File>? = null
    private var sftpClient: SftpClient? = null


    /**
     * Gets a file from the test resources package, or throws an exception if the test file doesn't exist.
     * @param relativeFilePath the file path, relative to "src/test/resources"
     */
    @Throws(Exception::class)
    private fun getImagesDirectory(relativeFilePath: String): File {
        var theRelativeFilePath = relativeFilePath
        val url = photo_session::class.java.getResource(theRelativeFilePath)

        val testFile = File(url!!.file)
        TestCase.assertTrue("No test file exists for relative path '$theRelativeFilePath'", testFile.exists())
        return testFile
    }

    /**
     * Ensures that a directory exists for the specified path, and returns the [File],
     * or `null` if it could not be created.

     * @param directoryPath the directory path to ensure
     */
    @Throws(IOException::class)
    private fun ensureDirectory(directoryPath: String): File {
        val errorMessage = "Could not create directory for path '$directoryPath'"
        if (StringUtils.isEmpty(directoryPath)) {
            throw IOException(errorMessage)
        }

        val directory = File(directoryPath)
        if (directory.exists()) {
            if (!directory.isDirectory) {
                throw IOException("File '$directory' exists and is not a directory. Unable to create directory.")
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory) {
                    throw IOException("Unable to create directory '$directory'")
                }
            }
        }

        if (!directory.isDirectory) {
            throw IOException(errorMessage)
        }
        return directory
    }

    /**
     * Creates new connection parameters.
     */
    private fun createConnectionParameters(): SftpConnectionParameters {
        return SftpConnectionParametersBuilder.newInstance().createConnectionParameters()
            .withHostFromEnvironmentVariable(ENVIRONMENT_VARIABLE_HOST)
            .withPortFromEnvironmentVariable(ENVIRONMENT_VARIABLE_PORT)
            .withUsernameFromEnvironmentVariable(ENVIRONMENT_VARIABLE_USERNAME)
            .withPasswordFromEnvironmentVariable(ENVIRONMENT_VARIABLE_PASSWORD)
            .create()
    }

    @Test
    @Throws(Exception::class)
    fun testAllSftpOperations() {
        executeBatchUpload()
    }

    @Throws(Exception::class)
    private fun executeBatchUpload() {

        val remoteFilePaths = ArrayList<String>()
        val filePairs = ArrayList<FilePair>()
        for (testFile in testFiles!!) {
            val remoteFilePath = remoteDirectoryForUploads + File.separator + testFile.name
            filePairs.add(FilePair(testFile.path, remoteFilePath))
            remoteFilePaths.add(remoteFilePath)
        }

        TestCase.assertTrue("Files were not uploaded!", sftpClient!!.upload(filePairs, 120*testFiles!!.size))
        TestCase.assertTrue("Files don't exist on server!", sftpClient!!.checkFiles(remoteFilePaths))
    }
}



