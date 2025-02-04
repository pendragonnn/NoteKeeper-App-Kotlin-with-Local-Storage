package com.example.notekeeper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.room.Note

class NoteAdapter(private val notes: ArrayList<Note>, private val listener: OnAdapterListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_main, parent, false)
        )
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        val text_title = holder.view.findViewById<TextView>(R.id.text_title)
        val edit_icon = holder.view.findViewById<ImageView>(R.id.icon_edit)
        val delete_icon = holder.view.findViewById<ImageView>(R.id.icon_delete)

        text_title.text = note.title
        text_title.setOnClickListener {
            listener.onClick(note)
        }

        edit_icon.setOnClickListener {
            listener.onUpdate(note)
        }

        delete_icon.setOnClickListener {
            listener.onDelete(note)
        }
    }

    fun setData(list: List<Note>) {
        notes.clear()
        notes.addAll(list)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(note: Note)
        fun onUpdate(note: Note)

        fun onDelete(note: Note)
    }
}