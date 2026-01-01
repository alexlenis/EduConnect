package com.example.educonnect.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.MapPlace
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlaceBottomSheet(
    private val latitude: Double,
    private val longitude: Double
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottomsheet_add_place, container, false)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val spinnerType = view.findViewById<Spinner>(R.id.spinnerType)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val types = listOf("OFFICE", "SECRETARIAT", "SUBJECT")
        spinnerType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            val type = spinnerType.selectedItem.toString()

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val place = MapPlace(
                title = title,
                description = description,
                latitude = latitude,
                longitude = longitude,
                type = type,
                subjectId = null
            )

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(requireContext())
                    .mapPlaceDao()
                    .insert(place)
            }

            dismiss()
        }

        return view
    }
}
