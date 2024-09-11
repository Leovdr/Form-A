package com.example.aplikasiforma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreen, GetStarted::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}