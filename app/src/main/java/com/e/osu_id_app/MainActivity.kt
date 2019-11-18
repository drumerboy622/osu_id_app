package com.e.osu_id_app


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_file.view.*
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startSession (view: View) {

        val randomIntent = Intent(this@MainActivity, barcode_scan::class.java)

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_file, null)
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("Enter Session Name")

        builder.setView(mDialogView)

        builder.setMessage("Live Uploading capabilities COMING SOON")

        builder.setPositiveButton("Start new Session"){dialog, which -> run {

            var input1 = mDialogView.editText.text.toString()
            var fileString = "sent/$input1"
            var path = "sent"
            if(mDialogView.checkBox.isChecked) {
                fileString = "unsent/$input1"
                path = "unsent"
            }

            //Create Folder
            var filename = File(externalMediaDirs.first(), fileString)
            filename?.mkdirs()

            randomIntent.putExtra("FileName", input1)
            randomIntent.putExtra("Path", path)
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
