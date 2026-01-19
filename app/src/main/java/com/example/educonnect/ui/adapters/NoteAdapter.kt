package com.example.educonnect.ui.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
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
        val btnUpdate: ImageButton = view.findViewById(R.id.btnUpdate)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val btnShare: ImageButton = view.findViewById(R.id.btnShare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        // TEXT
        holder.text.text = note.text

        // IMAGE
        holder.image.visibility = View.GONE
        holder.image.setImageBitmap(null)
        holder.image.setOnClickListener(null)

        val path = note.imagePath
        if (!path.isNullOrEmpty()) {
            val file = File(path)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.image.setImageBitmap(bitmap)
                holder.image.visibility = View.VISIBLE

                holder.image.setOnClickListener {
                    val intent = Intent(activity, FullscreenImageActivity::class.java)
                    intent.putExtra("image_path", path)
                    activity.startActivity(intent)
                }
            }
        }

        // EDIT
        holder.btnUpdate.setOnClickListener {
            onUpdate(note)
        }

        // DELETE
        holder.btnDelete.setOnClickListener {
            onDelete(note)
        }

        // SHARE (TEXT + IMAGE – COMPATIBLE)
        holder.btnShare.setOnClickListener {

            val shareText = """
                📝 Note

                ${note.text}

                Shared from EduConnect
            """.trimIndent()

            val imagePath = note.imagePath


            if (!imagePath.isNullOrEmpty()) {
                val file = File(imagePath)

                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        activity,
                        "${activity.packageName}.fileprovider",
                        file
                    )

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "*/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        clipData = android.content.ClipData.newRawUri("image", uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    activity.startActivity(
                        Intent.createChooser(intent, "Share note via")
                    )
                }

            } else {

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }

                activity.startActivity(
                    Intent.createChooser(intent, "Share note via")
                )
            }
        }
    }

    override fun getItemCount(): Int = notes.size

    fun updateData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
