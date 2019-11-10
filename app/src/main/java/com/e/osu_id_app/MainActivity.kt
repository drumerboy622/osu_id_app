package com.e.osu_id_app


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startSession (view: View) {

        val randomIntent = Intent(this@MainActivity, barcode_scan::class.java)

        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("Enter Session Name")

        val input = EditText(this@MainActivity)

        builder.setView(input)

        builder.setMessage("Live Uploading capabilities COMING SOON")

        builder.setPositiveButton("Start new Session"){dialog, which -> run {

            var input1 = input.getText().toString()

            //Create Folder
            var filename = File(externalMediaDirs.first(), input1)
            filename?.mkdirs()

            randomIntent.putExtra("FileName", input1)
            startActivity(randomIntent)
            }
        }

    // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel"){_,_ -> }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

    }
}
