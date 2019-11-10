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
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat


class Review : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get Values from last activity
        val barcode: String = intent.getStringExtra("student_barcode") as String
        val fileName: String = intent.getStringExtra("FileName") as String
        val filePath: String = intent.getStringExtra("FilePath") as String

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
        var textView2 = findViewById<TextView>(R.id.textView12)
        textView2.text = fileName


        // View image
        val bmp =
            BitmapFactory.decodeFile(filePath)
        val rotatedBitmap = bmp.rotate(90)
        imageView.setImageBitmap(rotatedBitmap)


        // Keep Button
        val kButton = findViewById<ImageButton>(R.id.imageButton2)

        kButton.setOnClickListener {
            val intent = Intent(this, barcode_scan::class.java)

            intent.putExtra("FileName", fileName)

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
                intent.putExtra("student_barcode", barcode)
                intent.putExtra("FileName", fileName)
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

        fun editSession(view: View) {

            //val randomIntent = Intent(this@MainActivity, barcode_scan::class.java)

            val builder = AlertDialog.Builder(this@Review)

            builder.setTitle("Change Session - COMING SOON")


            /*
            val input = EditText(this@MainActivity)

            builder.setView(input)



            builder.setPositiveButton("Start new Session"){dialog, which -> run {

                var input1 = input.getText().toString()

                //Create Folder
                var filename = File(externalMediaDirs.first(), input1)
                filename?.mkdirs()

                randomIntent.putExtra("FileName", input1)
                startActivity(randomIntent)
            }
            }
             */

            // Display a neutral button on alert dialog
            builder.setNeutralButton("Cancel"){_,_ -> }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()

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
