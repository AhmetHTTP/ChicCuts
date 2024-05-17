package com.chiccuts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.chiccuts.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnSaveLocation).setOnClickListener {
            selectedLocation?.let {
                val resultIntent = Intent().apply {
                    putExtra("location", "${it.latitude},${it.longitude}")
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLocation = latLng
        }

        val defaultLocation = LatLng(39.925533, 32.866287) // Default location (Ankara)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }
}
