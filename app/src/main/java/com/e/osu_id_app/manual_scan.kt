package com.e.osu_id_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

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
        randomIntent.putExtra("FileName", fileName)
        startActivity(randomIntent)
    }

    fun enterBarcode (view: View) {
        val randomIntent = Intent(this@manual_scan, photo_session::class.java)
        val input = findViewById<EditText>(R.id.editText5)
        val barcode = input.getText().toString()

        val fileName: String = intent.getStringExtra("FileName") as String
        randomIntent.putExtra("student_barcode", barcode)
        randomIntent.putExtra("FileName", fileName)
        startActivity(randomIntent)
    }
}
