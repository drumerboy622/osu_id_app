package com.e.osu_id_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*

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
        }

    }

}