package com.e.osu_id_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import java.io.File
import java.nio.file.Paths

class manual_scan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_scan)
    }

    fun goHome (view: View) {
        val randomIntent = Intent(this@manual_scan, MainActivity::class.java)
        startActivity(randomIntent)
    }

    fun backScan (view: View) {
        val randomIntent = Intent(this@manual_scan, barcode_scan::class.java)
        val fileName: String = intent.getStringExtra("FileName") as String
        val path: String = intent.getStringExtra("Path") as String
        val liveUpload: String = intent.getStringExtra("LiveUpload") as String
        randomIntent.putExtra("FileName", fileName)
        randomIntent.putExtra("Path", path)
        randomIntent.putExtra("LiveUpload", liveUpload)
        startActivity(randomIntent)
    }

    fun enterBarcode (view: View) {
        val searchIt = search()

        val input = findViewById<EditText>(R.id.editText5)
        val barcode = input.getText().toString()
        val fileName: String = intent.getStringExtra("FileName") as String
        val liveUpload: String = intent.getStringExtra("LiveUpload") as String

        var exists = true

        val dir = File(externalMediaDirs.first().toString())
        var filePath = Paths.get(dir.getAbsolutePath())
        println(filePath)

        filePath = searchIt.myMethod(filePath, barcode)
        println(barcode)
        println(filePath)

        try {

            exists = filePath.toString().isEmpty()
        } catch (e: Exception){
            println("No File matches")
        }

        if(!exists){
            val randomIntent = Intent(this@manual_scan, Review::class.java)
            val temp = File(filePath.toString())
            val unreversed = temp.getParentFile().getParentFile().getName()
            randomIntent.putExtra("student_barcode", barcode)
            randomIntent.putExtra("FileName", fileName)
            randomIntent.putExtra("SavedFileName", unreversed)
            randomIntent.putExtra("FilePath", filePath.toString())
            randomIntent.putExtra("LiveUpload", liveUpload)
            startActivity(randomIntent)
        } else {
            val randomIntent = Intent(this@manual_scan, photo_session::class.java)
            randomIntent.putExtra("student_barcode", barcode)
            randomIntent.putExtra("FileName", fileName)
            randomIntent.putExtra("LiveUpload", liveUpload)
            startActivity(randomIntent)
        }

    }
}
