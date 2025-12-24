package com.example.educonnect.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Note

class NoteAdapter(
    private var notes: List<Note>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tvNoteText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.text.text = notes[position].text
    }

    override fun getItemCount() = notes.size

    fun updateData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}

