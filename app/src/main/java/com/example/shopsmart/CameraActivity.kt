package com.example.lumi

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap ?: return

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Enable My Location layer
        mMap.isMyLocationEnabled = true

        // Get current location and show nearby shops
        getCurrentLocationAndShowShops()
    }

    private fun getCurrentLocationAndShowShops() {
        fusedLocationClient.lastLocation.addOnSuccessListener(this, object : OnSuccessListener<Location> {
            override fun onSuccess(location: Location?) {
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)

                    // Move the camera to the user's location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                    // Simulate nearby shops (replace with API for real shops)
                    showNearbyShops(userLocation)
                } ?: run {
                    Toast.makeText(this@MapActivity, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showNearbyShops(userLocation: LatLng) {
        // Example: Add a few simulated shops near the user (replace with real data from Google Places API)
        val shop1 = LatLng(userLocation.latitude + 0.001, userLocation.longitude + 0.001)
        val shop2 = LatLng(userLocation.latitude + 0.002, userLocation.longitude + 0.002)
        val shop3 = LatLng(userLocation.latitude - 0.003, userLocation.longitude - 0.003)

        // Add markers for nearby shops
        mMap.addMarker(MarkerOptions().position(shop1).title("Shop 1"))
        mMap.addMarker(MarkerOptions().position(shop2).title("Shop 2"))
        mMap.addMarker(MarkerOptions().position(shop3).title("Shop 3"))

        // You can replace the simulated data above with actual data from Google Places API or any other service.
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retry fetching the location
                getCurrentLocationAndShowShops()
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
