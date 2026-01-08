package com.example.educonnect.ui.map

import android.os.Bundle
import android.view.*
import android.widget.*
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.MapPlace
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*

class PlaceDetailsBottomSheet(
    private val place: MapPlace,
    private val onUpdated: () -> Unit,
    private val onDeleted: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.bottomsheet_place_details, container, false)

        v.findViewById<TextView>(R.id.tvTitle).text = place.title
        v.findViewById<TextView>(R.id.tvDescription).text = place.description

        v.findViewById<Button>(R.id.btnEdit).setOnClickListener {
            dismiss()
            AddPlaceBottomSheet(
                lat = place.latitude,
                lng = place.longitude,
                place = place,
                onSaved = onUpdated
            ).show(parentFragmentManager, "Edit")
        }

        v.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(requireContext())
                    .mapPlaceDao().delete(place)
                withContext(Dispatchers.Main) {
                    onDeleted()
                    dismiss()
                }
            }
        }
        return v
    }
}
