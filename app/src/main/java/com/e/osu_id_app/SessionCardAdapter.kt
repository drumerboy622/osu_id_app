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
import java.nio.file.Files.move
import java.nio.file.Paths



class SessionCardAdapter(val context: Context, val sessioncards: List<SessionCard>) : RecyclerView.Adapter<SessionCardAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
        return MyViewHolder(view)


    }

    override fun getItemCount(): Int {
        return sessioncards.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sessioncard = sessioncards[position]
        holder.setData(sessioncard, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun setData(sessionCard: SessionCard?, pos: Int) {
            val file = File(sessionCard!!.path)
            val path = file.getParentFile().getName()

            if (path == "stage")
            {
                itemView.imageButton5.setImageResource(R.drawable.upload1)
            }


            itemView.SessionTitle.text = sessionCard!!.title
            itemView.Session_Date.text = sessionCard!!.dateStr
            itemView.Session_Number_Uploaded.text = sessionCard!!.progressStr + " - " + path

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

                builder.setTitle("Continue " + sessionCard!!.title)
                builder.setMessage("Are you sure you would like to CONTINUE this Session?")

                builder.setPositiveButton("Yes") { dialog, which ->
                    run {
                        randomIntent.putExtra("FileName", sessionCard!!.title)
                        randomIntent.putExtra("Path", path)
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

            //Turn Live upload on or off

            itemView.imageButton5.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)
                if (path == "stage"){
                    var filePath = Paths.get(file.getAbsolutePath())
                    var dir = File("/storage/emulated/0/Android/media/com.osu_id_app/unsent/" + sessionCard!!.title)
                    var newFilePath = Paths.get(dir.getAbsolutePath())
                    move(filePath, newFilePath)
                } else {
                    var filePath = Paths.get(file.getAbsolutePath())
                    var dir = File("/storage/emulated/0/Android/media/com.osu_id_app/stage/" + sessionCard!!.title)
                    var newFilePath = Paths.get(dir.getAbsolutePath())
                    move(filePath, newFilePath)
                }


                context.startActivity(intent)
            }





            // Delete Session
            itemView.imageButton6.setOnClickListener {

                val builder = AlertDialog.Builder(context)

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