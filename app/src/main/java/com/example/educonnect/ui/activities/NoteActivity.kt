package com.example.educonnect.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Note
import com.example.educonnect.ui.adapters.NoteAdapter
import kotlinx.coroutines.launch

class NoteActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: NoteAdapter

    private var selectedImagePath: String? = null

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        db = AppDatabase.getDatabase(this)

        val etNoteText = findViewById<EditText>(R.id.etNoteText)
        val btnSave = findViewById<Button>(R.id.btnSaveNote)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        val recycler = findViewById<RecyclerView>(R.id.recyclerNotes)

        adapter = NoteAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        loadNotes()

        // 📸 CAMERA
        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }

        // 🖼️ GALLERY
        btnGallery.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        // 💾 SAVE NOTE
        btnSave.setOnClickListener {
            val note = Note(
                subjectId = 1, // προσωρινό
                text = etNoteText.text.toString(),
                imagePath = selectedImagePath,
                createdAt = System.currentTimeMillis()
            )

            lifecycleScope.launch {
                db.noteDao().insert(note)
                etNoteText.text.clear()
                selectedImagePath = null
                loadNotes()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQUEST_CAMERA -> {
                // απλό requirement coverage – αποθηκεύουμε placeholder
                selectedImagePath = "camera_image"
            }

            REQUEST_GALLERY -> {
                val uri: Uri? = data?.data
                selectedImagePath = uri?.toString()
            }
        }
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = db.noteDao().getAllNotes()
            adapter.updateData(notes)
        }
    }
}
