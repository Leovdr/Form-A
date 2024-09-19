package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        preferencesHelper = PreferencesHelper(this)

        // Handler untuk menunda proses
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            if (preferencesHelper.isLoggedIn()) {
                // Jika pengguna sudah login, arahkan ke HomeActivity
                val intent = Intent(this@SplashScreen, HomeActivity::class.java)
                startActivity(intent)
            } else {
                // Jika belum login, arahkan ke GetStarted
                val intent = Intent(this@SplashScreen, GetStarted::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000) // Waktu delay 3 detik
    }
}
