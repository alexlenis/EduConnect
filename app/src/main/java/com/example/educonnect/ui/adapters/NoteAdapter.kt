package com.example.educonnect.ui.adapters

import android.app.Activity
import android.content.Intent
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
import com.example.educonnect.ui.activities.FullscreenImageActivity
import java.io.File

class NoteAdapter(
    private val activity: Activity,
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
        holder.image.visibility = View.GONE
        holder.image.setImageBitmap(null)
        holder.image.setOnClickListener(null)

        val path = note.imagePath
        if (!path.isNullOrEmpty()) {
            val file = File(path)
            if (file.exists()) {

                // ✅ ΑΞΙΟΠΙΣΤΟ LOAD
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.image.setImageBitmap(bitmap)
                holder.image.visibility = View.VISIBLE

                // 👉 Fullscreen
                holder.image.setOnClickListener {
                    val intent = Intent(activity, FullscreenImageActivity::class.java)
                    intent.putExtra("image_path", path)
                    activity.startActivity(intent)
                }
            }
        }

        holder.btnUpdate.setOnClickListener { onUpdate(note) }
        holder.btnDelete.setOnClickListener { onDelete(note) }
    }

    override fun getItemCount(): Int = notes.size

    fun updateData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
