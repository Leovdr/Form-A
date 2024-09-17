package com.example.aplikasiforma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class TentangAplikasi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang_aplikasi)

            val backButton = findViewById<ImageView>(R.id.back_button)

        // Set OnClickListener to trigger back navigation
        backButton.setOnClickListener {
            onBackPressed() // Navigate back to the previous activity
        }
    }
}