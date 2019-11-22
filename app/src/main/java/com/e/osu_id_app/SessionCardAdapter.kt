package com.e.osu_id_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
//import kotlinx.android.synthetic.main.popup_menu.view.*
import kotlinx.android.synthetic.main.row.view.*
import java.io.File
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent.getIntent
import android.content.Intent.parseIntent
import java.nio.file.Files.isDirectory




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
            itemView.SessionTitle.text = sessionCard!!.title
            itemView.Session_Date.text = sessionCard!!.dateStr
            itemView.Session_Number_Uploaded.text = sessionCard!!.progressStr
            // Continue Session
            itemView.imageButton4.setOnClickListener {
                val randomIntent = Intent(context, barcode_scan::class.java)
                val file = File(sessionCard!!.path)
                val path = file.getParentFile().getName()

                randomIntent.putExtra("FileName", sessionCard!!.title)
                randomIntent.putExtra("Path", path)
                context.startActivity(randomIntent)
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