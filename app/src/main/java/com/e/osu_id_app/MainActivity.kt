package com.e.osu_id_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_file.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_setting.*
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.activity_setting.view.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        
        // Initializes the app and loads the main framework
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Initializes recycler view for saved sessions
        loadRecyclerView()

    }

    private fun loadRecyclerView () {

        // Create a vertical recyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        // Retrieve the saved sessions
        val savedSessions = ArrayList<SessionCard>()
        getSavedSessions(savedSessions)

        if(savedSessions.count() == 0) {

            val showIt = findViewById<TextView>(R.id.textView19)
            showIt.setVisibility(View.VISIBLE)
        }

        // Bind the recyclerView adapter to the session list
        var adapter = SessionCardAdapter(this, savedSessions)
        recyclerView.adapter = adapter

    }

    private fun getSavedSessions(sessionsLst: ArrayList<SessionCard>) {

        val sdf = SimpleDateFormat("MM/dd/yy 'at' HH:mm z")
        var filesTotalCnt = 0

        // using extension function walkBottomUp - gets the most recent folders first
        File("/storage/emulated/0/Android/media/com.osu_id_app/").walkBottomUp().forEach {

            // Iterate through each "live" and "notLive" directory
            val path = Paths.get(it.absolutePath)
            if(Files.isDirectory(path) && it.name != "com.osu_id_app"  && it.parentFile.name != "com.osu_id_app" && it.name != "unsent" && it.name != "sent" ){

                println(it.absolutePath)
                var unsent = 0
                filesTotalCnt = 0
                var path1 = "Null"
                var path2 = "Null"
                var path3 = "Null"

                // Count files from sub directories and get the paths for the three most recent files
                File(it.absolutePath).walk().forEach { it1 ->
                    if(it1.isFile) {
                        if(filesTotalCnt == 0)
                        {
                            path1 = it1.absolutePath
                        }
                        if(filesTotalCnt == 1)
                        {
                            path2 = it1.absolutePath
                        }
                        if(filesTotalCnt == 2)
                        {
                            path3 = it1.absolutePath
                        }
                        if (it1.getParentFile().getName() == "unsent") {
                            unsent++
                        }
                        filesTotalCnt++
                    }
                }

                var uploaded  = filesTotalCnt - unsent
                var live = it.getParentFile().getName()
                if (live == "notLive")
                    live = "not live"

                // Create a sessionCard for each directory and add it to the sessions list
                sessionsLst.add(SessionCard(path1, path2, path3, it.absolutePath, it.name, it.lastModified(), sdf.format(it.lastModified()),
                    "$uploaded of $filesTotalCnt Files Sent - $live"
                ) )
            }
        }

        // sort the sessions once complete to show the most recent first
        sessionsLst.sortByDescending { selector(it) }

    }

    fun startSettings(view: View) {

        val sharedPreference:SharedPreference=SharedPreference(this)

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_setting, null)
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("Settings - Not Working Yet")
        builder.setView(mDialogView)

        builder.setPositiveButton("Update") { dialog, which ->
            run {
                val host = mDialogView.host.text.toString()
                val port = Integer.parseInt(mDialogView.port.text.toString())
                val username = mDialogView.username.text.toString()
                val password = mDialogView.password.text.toString()
                sharedPreference.save("host",host)
                sharedPreference.save("port",port)
                sharedPreference.save("username",username)
                sharedPreference.save("password",password)

                println(sharedPreference.getValueString("host"))
                println(sharedPreference.getValueInt("port"))
                println(sharedPreference.getValueString("username"))
                println(sharedPreference.getValueString("password"))


            }
        }

        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel"){_,_ -> }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }


    fun startSession(view: View) {

        // Handles creating a new session
        val randomIntent = Intent(this@MainActivity, barcode_scan::class.java)
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_file, null)
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("Enter Session Name")
        builder.setView(mDialogView)
        builder.setPositiveButton("Start new Session"){dialog, which -> run {

            val searchIt = search()
            val input1 = mDialogView.editText.text.toString()
            var exists = true
            val dir = File(externalMediaDirs.first().toString())
            var filePath = Paths.get(dir.getAbsolutePath())

            filePath = searchIt.myMethod3(filePath, input1)
            println(filePath)

            try {
                exists = filePath.toString().isEmpty()
            } catch (e: Exception){
                println("No File matches")
            }

            if (!exists){

                Toast.makeText(this, "Name Already In Use", Toast.LENGTH_SHORT).show()

            } else {
                var fileString = "live/$input1/unsent"
                var fileString2 = "live/$input1/sent"
                var liveUpload = "true"

                if(mDialogView.checkBox.isChecked) {
                    liveUpload = "false"
                    fileString = "notLive/$input1/unsent"
                    fileString2 = "notLive/$input1/sent"
                }


                // Create Sent and Unsent Folders
                var createSentDir = File(externalMediaDirs.first(), fileString2)
                createSentDir?.mkdirs()
                var filename = File(externalMediaDirs.first(), fileString)
                filename?.mkdirs()

                randomIntent.putExtra("FileName", input1)
                randomIntent.putExtra("LiveUpload", liveUpload)
                startActivity(randomIntent)
            }

        } }

        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel"){_,_ -> }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

}


