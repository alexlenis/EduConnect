package com.example.educonnect.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.MapPlace
import com.example.educonnect.data.entity.PlaceType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_PICK_MODE = "pick_mode"
        const val RESULT_LAT = "result_lat"
        const val RESULT_LNG = "result_lng"
    }

    private lateinit var db: AppDatabase
    private var map: GoogleMap? = null
    private var pickMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        db = AppDatabase.getDatabase(this)
        pickMode = intent.getBooleanExtra(EXTRA_PICK_MODE, false)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val patras = LatLng(38.2466, 21.7346)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(patras, 15f))

        // φορτώνει όλα τα saved places
        loadMarkers()

        // tap marker -> default info window (title/snippet)
        googleMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }

        // long press: είτε pick (επιστροφή), είτε add place
        googleMap.setOnMapLongClickListener { latLng ->
            if (pickMode) {
                val result = Intent().apply {
                    putExtra(RESULT_LAT, latLng.latitude)
                    putExtra(RESULT_LNG, latLng.longitude)
                }
                setResult(Activity.RESULT_OK, result)
                finish()
            } else {
                showAddPlaceDialog(latLng)
            }
        }
    }

    private fun loadMarkers() {
        val googleMap = map ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val places = db.mapPlaceDao().getAll()

            withContext(Dispatchers.Main) {
                googleMap.clear()

                for (p in places) {
                    val hue = when (p.type) {
                        PlaceType.OFFICE -> BitmapDescriptorFactory.HUE_ORANGE
                        PlaceType.SECRETARIAT -> BitmapDescriptorFactory.HUE_GREEN
                        PlaceType.SUBJECT -> BitmapDescriptorFactory.HUE_AZURE
                    }

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(p.latitude, p.longitude))
                            .title(p.title)
                            .snippet(p.description)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue))
                    )
                }
            }
        }
    }

    private fun showAddPlaceDialog(latLng: LatLng) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_place, null)
        val etTitle = view.findViewById<EditText>(R.id.etPlaceTitle)
        val etDesc = view.findViewById<EditText>(R.id.etPlaceDesc)
        val spinner = view.findViewById<Spinner>(R.id.spPlaceType)

        val types = listOf("OFFICE (Γραφείο)", "SECRETARIAT (Γραμματεία)")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        AlertDialog.Builder(this)
            .setTitle("Add place")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val title = etTitle.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                if (title.isBlank()) {
                    Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val type = if (spinner.selectedItemPosition == 0) PlaceType.OFFICE else PlaceType.SECRETARIAT

                lifecycleScope.launch(Dispatchers.IO) {
                    db.mapPlaceDao().insert(
                        MapPlace(
                            title = title,
                            description = desc,
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            type = type
                        )
                    )
                    withContext(Dispatchers.Main) { loadMarkers() }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
