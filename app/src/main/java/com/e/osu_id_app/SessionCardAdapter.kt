package com.e.osu_id_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*
import java.io.File
import android.graphics.BitmapFactory
import java.nio.file.StandardCopyOption.*
import java.nio.file.Paths
import kotlinx.coroutines.*
import java.nio.file.Files.move


class SessionCardAdapter(val context: Context, val sessioncards: List<SessionCard>) : RecyclerView.Adapter<SessionCardAdapter.MyViewHolder>(){

    private var sftpClient: SftpClient? = null
    var filesForUpload: Array<File>? = null
    val remoteDirectoryForUploads = "OSU_ID_APP/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Create the viewHolder for the sessionCard
        val view = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        // Get the number of sessionCards
        return sessioncards.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        // Bind the data from list to viewHolder
        val sessioncard = sessioncards[position]
        holder.setData(sessioncard, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        // Build Cards For Each Session
        fun setData(sessionCard: SessionCard?, pos: Int) {
            val file = File(sessionCard!!.path)
            val path = file.getParentFile().getName()

            if (path == "live")
            {
                itemView.imageButton5.setImageResource(R.drawable.upload1)
            }

            itemView.SessionTitle.text = sessionCard!!.title
            itemView.Session_Date.text = sessionCard!!.dateStr
            itemView.Session_Number_Uploaded.text = sessionCard!!.progressStr

            if (sessionCard!!.photo1 != "Null") {
                val bmp =
                    BitmapFactory.decodeFile(sessionCard!!.photo1)
                val rotatedBitmap = bmp.rotate(90)
                itemView.photoPreview1.setImageBitmap(rotatedBitmap)
            } else {
                itemView.photoPreview1.setVisibility(View.GONE)
            }

            if (sessionCard!!.photo2 != "Null") {
                val bmp1 =
                    BitmapFactory.decodeFile(sessionCard!!.photo2)
                val rotatedBitmap1 = bmp1.rotate(90)
                itemView.photoPreview2.setImageBitmap(rotatedBitmap1)
            } else {
                itemView.photoPreview2.setVisibility(View.GONE)
            }

            if (sessionCard!!.photo3 != "Null") {
                val bmp2 =
                    BitmapFactory.decodeFile(sessionCard!!.photo3)
                val rotatedBitmap2 = bmp2.rotate(90)
                itemView.photoPreview3.setImageBitmap(rotatedBitmap2)
            } else {
                itemView.photoPreview3.setVisibility(View.GONE)
            }

            // Continue Session
            itemView.imageButton4.setOnClickListener {
                val randomIntent = Intent(context, barcode_scan::class.java)


                val builder = AlertDialog.Builder(context)

                // Display information
                builder.setTitle("Continue " + sessionCard!!.title)
                builder.setMessage("Are you sure you would like to CONTINUE this Session?")

                builder.setPositiveButton("Yes") { dialog, which ->
                    run {
                        randomIntent.putExtra("FileName", sessionCard!!.title)


                        if (path == "live") {
                            randomIntent.putExtra("LiveUpload", "true")
                        } else {
                            randomIntent.putExtra("LiveUpload", "false")
                        }
                        context.startActivity(randomIntent)
                    }
                }

                // Display a neutral button on alert dialog
                builder.setNeutralButton("Cancel") { _, _ -> }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }

            // Cloud / Batch Upload Button
            itemView.imageButton5.setOnClickListener {

                if (path == "live"){

                    val intent = Intent(context, MainActivity::class.java)
                    // refresh page
                    itemView.Session_Number_Uploaded.text = "Going Offline"
                    // For sent folder, move the files to notLive

                    var filePath = Paths.get(file.getAbsolutePath())
                    var dir = File("/storage/emulated/0/Android/media/com.osu_id_app/notLive/" + sessionCard!!.title)
                    var newFilePath = Paths.get(dir.getAbsolutePath())


                    move(filePath, newFilePath, REPLACE_EXISTING)

                    context.startActivity(intent)

                } else {
                    var dir = File("/storage/emulated/0/Android/media/com.osu_id_app/live/" + sessionCard!!.title + "/unsent")

                    GlobalScope.launch {
                        delay(10L)

                        //Get SFTP information from SharedPreferences
                        val sharedPreference = SharedPreference(context)
                        val host = sharedPreference.getValueString("host").toString()
                        val port = sharedPreference.getValueInt("port")
                        val username = sharedPreference.getValueString("username").toString()
                        val password = sharedPreference.getValueString("password").toString()

                        // Initiate Connection
                        sftpClient = SftpClient.create(SftpConnectionParameters(host, port, username, password.toByteArray()))

                        // Get Files for upload
                        filesForUpload = dir.listFiles()

                        val remoteFilePaths = ArrayList<String>()
                        val filePairs = ArrayList<FilePair>()
                        for (uploadFile in filesForUpload!!) {
                            val remoteFilePath = remoteDirectoryForUploads + File.separator + uploadFile.name
                            filePairs.add(FilePair(uploadFile.path, remoteFilePath))
                            remoteFilePaths.add(remoteFilePath)
                        }

                        // Upload to SFTP
                        if (sftpClient!!.upload(filePairs, 120*filesForUpload!!.size)) {

                            // Successful upload
                            println("Batch Upload Successful")

                            // Move files to sent folder
                            for (uploadFile in filesForUpload!!) {
                                var filePath = Paths.get(uploadFile.getAbsolutePath())
                                var filePathStr = uploadFile.absolutePath
                                var newFilePathStr = filePathStr.replaceFirst("unsent", "sent", true)
                                var newFilePathDir = File(newFilePathStr)
                                var newFilePath = Paths.get(newFilePathDir.getAbsolutePath())
                                move(filePath, newFilePath, REPLACE_EXISTING)
                                println("Moved file to sent folder")
                            }
                        } else {  // Failed Upload
                            println("Batch Upload Failed")
                            }

                        //Reload Main Activity
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }

                    // Disable buttons that should not be diplayed
                    itemView.imageButton5.setVisibility(View.INVISIBLE)
                    itemView.imageButton6.setVisibility(View.INVISIBLE)

                    // Display Text to let use know what is going on
                    itemView.Session_Number_Uploaded.text = "Please Wait - Uploading"

                    // Update the progress text
                    var filePath = Paths.get(file.getAbsolutePath())
                    var dir1 = File("/storage/emulated/0/Android/media/com.osu_id_app/live/" + sessionCard!!.title)
                    var newFilePath = Paths.get(dir1.getAbsolutePath())
                    move(filePath, newFilePath, REPLACE_EXISTING)
                }
            }

            // Delete Session
            itemView.imageButton6.setOnClickListener {

                val builder = AlertDialog.Builder(context)

                // Display information
                builder.setTitle("Delete " + sessionCard!!.title)
                builder.setMessage("Are you sure you would like to DELETE this Session?")

                //Yes Button
                builder.setPositiveButton("Yes") { dialog, which ->
                    run {

                        fun deleteRecursive(fileOrDirectory: File) {
                            if (fileOrDirectory.isDirectory)
                                for (child in fileOrDirectory.listFiles()!!)
                                    deleteRecursive(child)

                            fileOrDirectory.delete()
                        }

                        val intent = Intent(context, MainActivity::class.java)
                        val file = File(sessionCard!!.path)
                        deleteRecursive(file)
                        context.startActivity(intent)
                    }}

                // Display a neutral button on alert dialog
                builder.setNeutralButton("Cancel") { _, _ -> }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }
        }
    }
}
