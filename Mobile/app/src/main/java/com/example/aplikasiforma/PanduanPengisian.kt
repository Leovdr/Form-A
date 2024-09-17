package com.example.aplikasiforma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class PanduanPengisian : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panduan_pengisian)

        // Find the ImageView by ID
        val backButton = findViewById<ImageView>(R.id.back_button)

        // Set OnClickListener to trigger back navigation
        backButton.setOnClickListener {
            onBackPressed() // Navigate back to the previous activity
        }
    }
}
