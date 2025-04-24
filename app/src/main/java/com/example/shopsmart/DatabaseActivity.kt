package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        findViewById<Button>(R.id.btnAddItem).setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }

        findViewById<Button>(R.id.btnViewItems).setOnClickListener {
            startActivity(Intent(this, ViewItemsActivity::class.java))
        }

        findViewById<Button>(R.id.btnCamera).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<Button>(R.id.btnMessage).setOnClickListener {
            startActivity(Intent(this, MessagingActivity::class.java))
        }

        findViewById<Button>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
