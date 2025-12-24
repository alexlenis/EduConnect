package com.example.educonnect.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.educonnect.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val university = LatLng(37.9838, 23.7275) // Αθήνα (βάλε ό,τι θες)
        googleMap.addMarker(
            MarkerOptions().position(university).title("University")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 15f))
    }
}
