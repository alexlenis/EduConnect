package com.example.educonnect.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.MapPlace
import com.example.educonnect.data.entity.PlaceType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPlaceBottomSheet(
    private val lat: Double,
    private val lng: Double,
    private val place: MapPlace? = null,
    private val onSaved: () -> Unit
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

        spinnerType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("OFFICE", "SECRETARIAT", "SUBJECT")
        )

        // EDIT MODE
        place?.let {
            etTitle.setText(it.title)
            etDescription.setText(it.description)
            spinnerType.setSelection(it.type.ordinal)
            btnSave.text = "Update"
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = PlaceType.values()[spinnerType.selectedItemPosition]

            CoroutineScope(Dispatchers.IO).launch {
                val dao = AppDatabase.getDatabase(requireContext()).mapPlaceDao()

                if (place == null) {
                    dao.insert(
                        MapPlace(
                            title = title,
                            description = description,
                            latitude = lat,
                            longitude = lng,
                            type = type
                        )
                    )
                } else {
                    dao.update(
                        place.copy(
                            title = title,
                            description = description,
                            type = type
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    onSaved()
                    dismiss()
                }
            }
        }

        return view
    }
}
