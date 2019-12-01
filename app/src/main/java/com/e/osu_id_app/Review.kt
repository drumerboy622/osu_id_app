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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.nio.file.Files.move
import java.nio.file.Paths
import java.text.SimpleDateFormat

class Review : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get Values from last activity
        val barcode: String = intent.getStringExtra("student_barcode") as String
        val fileName: String = intent.getStringExtra("FileName") as String
        val liveUpload: String = intent.getStringExtra("LiveUpload") as String
        var textView2 = findViewById<TextView>(R.id.textView12)

        val savedFileName: String
        try {
            savedFileName = intent.getStringExtra("SavedFileName") as String
            textView2.text = savedFileName
        } catch (e: Exception){
            textView2.text = fileName
        }

        val filePath: String = intent.getStringExtra("FilePath") as String

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

            if(liveUpload == "true") {
                a.start()
            }

            intent.putExtra("FileName", fileName)
            intent.putExtra("LiveUpload", liveUpload)

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
                intent.putExtra("LiveUpload", liveUpload)
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
                intent.putExtra("LiveUpload", liveUpload)
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

// Photo Upload function is threaded to background
class A(val file: File) : Thread()
{
    // These environment variables must be defined on your machine
    private val ENVIRONMENT_VARIABLE_HOST = "KSFTP_HOST"
    private val ENVIRONMENT_VARIABLE_PORT = "KSFTP_PORT"
    private val ENVIRONMENT_VARIABLE_USERNAME = "KSFTP_USERNAME"
    private val ENVIRONMENT_VARIABLE_PASSWORD = "KSFTP_PASSWORD"

    // Remote directory for upload - a folder at the user's root level on SFTP server
    // Directory will be created if it does not exist
    private val remoteDirectoryForUploads = "OSU_ID_APP/"
    private var sftpClient: SftpClient? = null
    private fun createConnectionParameters(): SftpConnectionParameters {
        return SftpConnectionParametersBuilder.newInstance().createConnectionParameters()
            .withHostFromEnvironmentVariable(ENVIRONMENT_VARIABLE_HOST)
            .withPortFromEnvironmentVariable(ENVIRONMENT_VARIABLE_PORT)
            .withUsernameFromEnvironmentVariable(ENVIRONMENT_VARIABLE_USERNAME)
            .withPasswordFromEnvironmentVariable(ENVIRONMENT_VARIABLE_PASSWORD)
            .create()
    }

    override fun run() {
        // Upload file
        sftpClient = SftpClient.create(createConnectionParameters())

        // Upload to SFTP
        if(sftpClient!!.upload(file.absolutePath, remoteDirectoryForUploads, 300)){

            println("Single File Upload Successful")
            var filePath = Paths.get(file.getAbsolutePath())
            var filePathStr = file.absolutePath
            var newFilePathStr = filePathStr.replaceFirst("unsent", "sent", true)
            var newFilePathDir = File(newFilePathStr)
            var newFilePath = Paths.get(newFilePathDir.getAbsolutePath())
            move(filePath, newFilePath)
            println("Moved file to sent folder")
        }
    }
}



