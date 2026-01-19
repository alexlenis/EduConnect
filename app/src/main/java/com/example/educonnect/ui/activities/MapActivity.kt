package com.example.educonnect.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.MapPlace
import com.example.educonnect.data.entity.PlaceType
import com.example.educonnect.ui.map.AddPlaceBottomSheet
import com.example.educonnect.ui.map.PlaceDetailsBottomSheet
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
    private lateinit var map: GoogleMap
    private var pickMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        db = AppDatabase.getDatabase(this)
        pickMode = intent.getBooleanExtra(EXTRA_PICK_MODE, false)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 📍 ΠΡΩΗΝ ΤΕΙ ΠΑΤΡΑΣ
        val teiPatras = LatLng(38.2866, 21.7879)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(teiPatras, 17f))

        loadMarkers()

        if (pickMode) {
            map.setOnMapClickListener {
                setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        putExtra(RESULT_LAT, it.latitude)
                        putExtra(RESULT_LNG, it.longitude)
                    }
                )
                finish()
            }
            return
        }

        // ➕ ADD
        map.setOnMapLongClickListener {
            AddPlaceBottomSheet(
                lat = it.latitude,
                lng = it.longitude,
                onSaved = { loadMarkers() }
            ).show(supportFragmentManager, "AddPlace")
        }


        map.setOnMarkerClickListener { marker ->
            val place = marker.tag as? MapPlace ?: return@setOnMarkerClickListener true
            PlaceDetailsBottomSheet(
                place = place,
                onUpdated = { loadMarkers() },
                onDeleted = { loadMarkers() }
            ).show(supportFragmentManager, "Details")
            true
        }
    }

    private fun loadMarkers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val places = db.mapPlaceDao().getAll()
            withContext(Dispatchers.Main) {
                map.clear()
                places.forEach {
                    val hue = when (it.type) {
                        PlaceType.OFFICE -> BitmapDescriptorFactory.HUE_ORANGE
                        PlaceType.SECRETARIAT -> BitmapDescriptorFactory.HUE_GREEN
                        PlaceType.SUBJECT -> BitmapDescriptorFactory.HUE_AZURE
                    }
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(it.latitude, it.longitude))
                            .title(it.title)
                            .snippet(it.description)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue))
                    )
                    marker?.tag = it
                }
            }
        }
    }
}
