package com.example.educonnect.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Note
import com.example.educonnect.ui.adapters.NoteAdapter
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NoteActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: NoteAdapter

    private lateinit var imgPreview: ImageView

    private var selectedImagePath: String? = null
    private var cameraImagePath: String? = null
    private var editingNote: Note? = null

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
        private const val PERMISSION_CAMERA = 200
        private const val PERMISSION_GALLERY = 201
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        db = AppDatabase.getDatabase(this)

        val etText = findViewById<EditText>(R.id.etNoteText)
        val btnSave = findViewById<Button>(R.id.btnSaveNote)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        val recycler = findViewById<RecyclerView>(R.id.recyclerNotes)

        imgPreview = findViewById(R.id.imgPreview)
        imgPreview.visibility = View.GONE

        adapter = NoteAdapter(
            activity = this,
            notes = emptyList(),
            onUpdate = { note ->
                editingNote = note
                etText.setText(note.text)
                selectedImagePath = note.imagePath
            },
            onDelete = { note ->
                AlertDialog.Builder(this)
                    .setTitle("Delete note")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes") { _, _ ->
                        lifecycleScope.launch {
                            db.noteDao().delete(note)
                            loadNotes()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        loadNotes()

        // 📸 CAMERA
        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_CAMERA
                )
            } else {
                openCamera()
            }
        }

        // 🖼️ GALLERY
        btnGallery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        PERMISSION_GALLERY
                    )
                } else {
                    openGallery()
                }
            } else {
                openGallery()
            }
        }

        // 💾 SAVE NOTE
        btnSave.setOnClickListener {
            val text = etText.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(this, "Text required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (editingNote == null) {
                    db.noteDao().insert(
                        Note(
                            subjectId = 1,
                            text = text,
                            imagePath = selectedImagePath,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    db.noteDao().update(
                        editingNote!!.copy(
                            text = text,
                            imagePath = selectedImagePath
                        )
                    )
                    editingNote = null
                }

                etText.text.clear()
                selectedImagePath = null

                // 🔹 κρύβουμε preview μετά το save
                imgPreview.setImageDrawable(null)
                imgPreview.visibility = View.GONE

                loadNotes()
            }
        }
    }

    // ---------- CAMERA (FULL RES) ----------
    private fun openCamera() {
        val imagesDir = File(cacheDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()

        val imageFile = File(
            imagesDir,
            "note_camera_${System.currentTimeMillis()}.jpg"
        )

        cameraImagePath = imageFile.absolutePath

        val imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        startActivityForResult(intent, REQUEST_CAMERA)
    }

    // ---------- GALLERY ----------
    private fun openGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    // ---------- PERMISSIONS ----------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            return
        }

        when (requestCode) {
            PERMISSION_CAMERA -> openCamera()
            PERMISSION_GALLERY -> openGallery()
        }
    }

    // ---------- RESULT ----------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {

            REQUEST_CAMERA -> {
                selectedImagePath = cameraImagePath

                cameraImagePath?.let {
                    val bitmap = BitmapFactory.decodeFile(it)
                    imgPreview.setImageBitmap(bitmap)
                    imgPreview.visibility = View.VISIBLE
                }
            }

            REQUEST_GALLERY -> {
                val uri = data?.data ?: return
                val inputStream = contentResolver.openInputStream(uri) ?: return

                val file = File(
                    cacheDir,
                    "note_gallery_${System.currentTimeMillis()}.jpg"
                )

                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }

                selectedImagePath = file.absolutePath

                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imgPreview.setImageBitmap(bitmap)
                imgPreview.visibility = View.VISIBLE
            }
        }
    }

    // ---------- LOAD ----------
    private fun loadNotes() {
        lifecycleScope.launch {
            adapter.updateData(db.noteDao().getAllNotes())
        }
    }
}
