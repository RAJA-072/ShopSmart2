package com.example.shopsmart

import android.app.Application
import org.osmdroid.config.Configuration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
    }
}
