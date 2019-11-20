package com.e.osu_id_app


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.se.omapi.Session
import android.view.View
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.stream.Collectors


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val savedSessions = ArrayList<SessionCard>()
        val sdf = SimpleDateFormat("MM/dd/yy 'at' HH:mm z")

        var filesTotalCnt = 0
        var filesUploadedCnt = 0

        // using extension function walk
        File("/storage/emulated/0/Android/media/com.osu_id_app/").walk().forEach {

            //Create a Path object
            val path = Paths.get(it.absolutePath)

            if(Files.isDirectory(path) && it.name != "com.osu_id_app"){

                filesTotalCnt = 0
                filesUploadedCnt = 0

                // Get files from sub directories
                File(it.absolutePath).walk().forEach {
                    if(it.isFile) {
                        filesTotalCnt++
                    }
                }

                savedSessions.add(SessionCard(it.name, sdf.format(it.lastModified()), filesTotalCnt.toString() + " Files") )


            }


        }

        var adapter = SessionCardAdapter(this, savedSessions)
        recyclerView.adapter = adapter
    }

    fun sessionOptions (view: View) {

        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("Session Options")

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

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
