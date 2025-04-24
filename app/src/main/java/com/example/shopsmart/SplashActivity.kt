package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {

    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize Lottie animation view
        lottieAnimationView = findViewById(R.id.lottieAnimationView)

        // Start Lottie animation
        lottieAnimationView.playAnimation()

        // Delay and navigate to LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the animation when the activity is destroyed
        lottieAnimationView.cancelAnimation()
    }
}
