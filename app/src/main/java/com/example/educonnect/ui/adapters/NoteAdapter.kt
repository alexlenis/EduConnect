package com.example.educonnect.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Note
import java.io.File

class NoteAdapter(
    private var notes: List<Note>,
    private val onUpdate: (Note) -> Unit,
    private val onDelete: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tvNoteText)
        val image: ImageView = view.findViewById(R.id.imgNote)
        val btnUpdate: Button = view.findViewById(R.id.btnUpdate)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        holder.text.text = note.text

        // ---------- IMAGE ----------
        if (!note.imagePath.isNullOrEmpty()) {
            val file = File(note.imagePath)

            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.image.setImageBitmap(bitmap)
                holder.image.visibility = View.VISIBLE
            } else {
                holder.image.visibility = View.GONE
            }
        } else {
            holder.image.visibility = View.GONE
        }

        // ---------- BUTTONS ----------
        holder.btnUpdate.setOnClickListener {
            onUpdate(note)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(note)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun updateData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
