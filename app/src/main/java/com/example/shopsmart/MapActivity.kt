package com.example.shopsmart

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var controller: IMapController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load osmdroid configuration
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        setContentView(R.layout.activity_map)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK) // Green terrain view
        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.ALWAYS)

        controller = map.controller
        controller.setZoom(15.0)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLocation = GeoPoint(it.latitude, it.longitude)
                controller.setCenter(userLocation)

                val userMarker = Marker(map)
                userMarker.position = userLocation
                userMarker.title = "You are here"
                userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(userMarker)

                fetchNearbyStores(it.latitude, it.longitude)
            }
        }
    }

    private fun fetchNearbyStores(lat: Double, lon: Double) {
        thread {
            try {
                val url = "https://overpass-api.de/api/interpreter?data=[out:json];node[shop](around:1000,$lat,$lon);out;"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                val json = JSONObject(response)
                val elements = json.getJSONArray("elements")

                runOnUiThread {
                    for (i in 0 until elements.length()) {
                        val item = elements.getJSONObject(i)
                        val shopLat = item.getDouble("lat")
                        val shopLon = item.getDouble("lon")
                        val tags = item.optJSONObject("tags")
                        val name = tags?.optString("name", "Shop") ?: "Shop"

                        val point = GeoPoint(shopLat, shopLon)
                        val marker = Marker(map)
                        marker.position = point
                        marker.title = name
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        map.overlays.add(marker)
                    }
                    map.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
