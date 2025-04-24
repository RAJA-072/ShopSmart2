package com.example.shopsmart

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ViewItemsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val itemList = mutableListOf<String>()
    private val itemMap = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_items)

        listView = findViewById(R.id.itemListView)
        registerForContextMenu(listView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        fetchData()

        listView.setOnItemClickListener { _, _, position, _ ->
            val name = itemList[position].split(" | ")[0]
            showNotification("Check $name!", "Expiring soon? Better use it today.")
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            listView.showContextMenuForChild(listView.getChildAt(position))
            false
        }

        createNotificationChannel()
    }

    private fun fetchData() {
        itemList.clear()
        val db = DatabaseHelper(this).readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM items", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val expiry = cursor.getString(2)
                val qty = cursor.getString(3)
                itemList.add("$name | Qty: $qty | Exp: $expiry")
                itemMap[id] = name
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.item_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val selectedItem = itemList[info.position]
        val itemId = itemMap.keys.toList()[info.position]

        return when (item.itemId) {
            R.id.menu_delete -> {
                deleteItem(itemId)
                true
            }
            R.id.menu_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "Check this item: $selectedItem")
                startActivity(Intent.createChooser(intent, "Share via"))
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun deleteItem(id: Int) {
        val db = DatabaseHelper(this).writableDatabase
        db.delete("items", "id=?", arrayOf(id.toString()))
        fetchData()
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ExpiryChannel"
            val descriptionText = "Item Expiry Alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("expire_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(this, "expire_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify((0..10000).random(), builder.build())
        }
    }
}
