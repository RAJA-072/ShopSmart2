package com.example.shopsmart

import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etQty: EditText
    private lateinit var btnSave: Button
    private lateinit var locationManager: LocationManager

    private var locationTag: String = "Unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        etName = findViewById(R.id.etItemName)
        etExpiry = findViewById(R.id.etExpiry)
        etQty = findViewById(R.id.etQuantity)
        btnSave = findViewById(R.id.btnSaveItem)

        // 📅 Date picker
        etExpiry.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dpd = DatePickerDialog(
                this,
                { _, year, month, day ->
                    etExpiry.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        // 📍 Location access
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationTag = "Lat: ${location.latitude}, Lon: ${location.longitude}"
                    }
                })
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }

        // 💾 Save to SQLite + ⏰ Schedule notification
        btnSave.setOnClickListener {
            val itemName = etName.text.toString()
            val expiry = etExpiry.text.toString().trim() // Trim any leading/trailing spaces
            val quantity = etQty.text.toString()

            if (itemName.isEmpty() || expiry.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the expiry date matches the required format: d/M/yyyy
            val datePattern = "^([0-2]?[0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{4}$".toRegex()
            if (!expiry.matches(datePattern)) {
                Toast.makeText(this, "Invalid expiry date format. Use dd/MM/yyyy.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Try parsing the expiry date
            try {
                val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                sdf.isLenient = false  // Strict validation of the date
                val date = sdf.parse(expiry)

                if (date != null) {
                    // Proceed with database saving and scheduling notification
                    val db = DatabaseHelper(this).writableDatabase
                    val values = ContentValues().apply {
                        put("name", itemName)
                        put("expiry", expiry)
                        put("quantity", quantity)
                        put("location", locationTag)
                    }
                    val newRowId = db.insert("items", null, values)
                    if (newRowId != -1L) {
                        Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT).show()

                        // 🛎 Schedule Notification 1 day before expiry
                        val calendar = Calendar.getInstance().apply {
                            time = date
                            add(Calendar.DATE, -1) // 1 day before expiry
                            set(Calendar.HOUR_OF_DAY, 9)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }

                        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(this, NotificationReceiver::class.java).apply {
                            putExtra("itemName", itemName)
                            putExtra("expiryDate", expiry)
                        }
                        val pendingIntent = PendingIntent.getBroadcast(
                            this,
                            newRowId.toInt(),
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

                        finish()
                    } else {
                        Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Invalid expiry date", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()  // Print stack trace for debugging
                Toast.makeText(this, "Invalid expiry date format", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
