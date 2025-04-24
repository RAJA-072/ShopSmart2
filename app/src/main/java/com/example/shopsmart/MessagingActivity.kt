package com.example.shopsmart

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

class MessagingActivity : AppCompatActivity() {

    private lateinit var numberEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var smsButton: Button
    private lateinit var whatsappButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        numberEditText = findViewById(R.id.numberEditText)
        messageEditText = findViewById(R.id.messageEditText)
        smsButton = findViewById(R.id.smsButton)
        whatsappButton = findViewById(R.id.whatsappButton)

        // Request SMS permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)
        }

        smsButton.setOnClickListener {
            val phone = numberEditText.text.toString()
            val msg = messageEditText.text.toString()
            try {
                val sms = SmsManager.getDefault()
                sms.sendTextMessage(phone, null, msg, null, null)
                Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "SMS failed", Toast.LENGTH_SHORT).show()
            }
        }

        whatsappButton.setOnClickListener {
            val msg = messageEditText.text.toString()
            val uri = Uri.parse("https://api.whatsapp.com/send?text=$msg")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }
}
