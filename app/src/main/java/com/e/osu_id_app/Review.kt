package com.e.osu_id_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.R.attr.data
import android.content.Intent
import androidx.core.app.NotificationCompat.getExtras
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlinx.android.synthetic.main.activity_review.*
import java.io.File
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.provider.MediaStore
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_review.imageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Environment.getExternalStorageDirectory
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_second.*

private const val REQUEST_CODE_PERMISSIONS = 10

class Review : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get Values from last activity
        val barcode: String = intent.getStringExtra("student_barcode") as String
        val fileName: String = intent.getStringExtra("FileName") as String
        val filePath: String = intent.getStringExtra("FilePath") as String

        //Display Barcode
        var textView = findViewById<TextView>(R.id.textView11)
        textView.text = barcode

        // View image
        val bmp =
            BitmapFactory.decodeFile(filePath)
        val rotatedBitmap = bmp.rotate(90)
        imageView.setImageBitmap(rotatedBitmap)

        //Button
        val mButton = findViewById<Button>(R.id.button2)

        mButton.setOnClickListener {
            val intent = Intent(this, barcode_scan::class.java)

            intent.putExtra("FileName", fileName)

            startActivity(intent)
        }

        //println("The Barcode is $barcode and the Filename is $fileName")

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
