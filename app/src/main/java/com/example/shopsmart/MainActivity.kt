package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the greeting from the passed username (or default to "User")
        val username = intent.getStringExtra("USERNAME") ?: "User"
        val greeting = findViewById<TextView>(R.id.greetingTextView)  // Assuming there is a TextView for greeting
        greeting.text = "Hello, $username"

        // Handle MaterialToolbar properly


        // Optionally, set click listeners on your cards here
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardAddItem)
            .setOnClickListener { startActivity(Intent(this, AddItemActivity::class.java)) }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardViewItems)
            .setOnClickListener { startActivity(Intent(this, ViewItemsActivity::class.java)) }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardMap)
            .setOnClickListener { startActivity(Intent(this, MapActivity::class.java)) }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardReminder)
            .setOnClickListener { startActivity(Intent(this, MessagingActivity::class.java)) }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardCamera)
            .setOnClickListener { startActivity(Intent(this, CameraActivity::class.java)) }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardGallery)
            .setOnClickListener { startActivity(Intent(this, GalleryActivity::class.java)) }

    }
}
