package com.example.educonnect.ui.bottomsheet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Note
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddEditNoteBottomSheet(
    private val note: Note?,
    private val onSaved: () -> Unit
) : BottomSheetDialogFragment() {

    private var selectedImagePath: String? = null
    private var cameraImagePath: String? = null

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
        private const val PERMISSION_CAMERA = 200
        private const val PERMISSION_GALLERY = 201
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottomsheet_add_note, container, false)
        val db = AppDatabase.getDatabase(requireContext())

        val etText = view.findViewById<EditText>(R.id.etNoteText)
        val imgPreview = view.findViewById<ImageView>(R.id.imgPreview)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        val btnGallery = view.findViewById<Button>(R.id.btnGallery)

        imgPreview.visibility = View.GONE

        note?.let {
            etText.setText(it.text)
            selectedImagePath = it.imagePath

            it.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    imgPreview.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                    imgPreview.visibility = View.VISIBLE
                }
            }
        }

        btnCancel.setOnClickListener { dismiss() }

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA)
            } else {
                openCamera()
            }
        }

        btnGallery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
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

        btnSave.setOnClickListener {
            val text = etText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Text required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (note == null) {
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
                        note.copy(
                            text = text,
                            imagePath = selectedImagePath
                        )
                    )
                }
                onSaved()
                dismiss()
            }
        }

        return view
    }

    private fun openCamera() {
        val imagesDir = File(requireContext().cacheDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()

        val imageFile = File(imagesDir, "note_camera_${System.currentTimeMillis()}.jpg")
        cameraImagePath = imageFile.absolutePath

        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            return
        }
        when (requestCode) {
            PERMISSION_CAMERA -> openCamera()
            PERMISSION_GALLERY -> openGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        val root = view ?: return
        val imgPreview = root.findViewById<ImageView>(R.id.imgPreview)

        when (requestCode) {
            REQUEST_CAMERA -> {
                selectedImagePath = cameraImagePath
                cameraImagePath?.let {
                    imgPreview.setImageBitmap(BitmapFactory.decodeFile(it))
                    imgPreview.visibility = View.VISIBLE
                }
            }

            REQUEST_GALLERY -> {
                val uri = data?.data ?: return
                val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return

                val file = File(requireContext().cacheDir, "note_gallery_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }

                selectedImagePath = file.absolutePath
                imgPreview.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                imgPreview.visibility = View.VISIBLE
            }
        }
    }
}
