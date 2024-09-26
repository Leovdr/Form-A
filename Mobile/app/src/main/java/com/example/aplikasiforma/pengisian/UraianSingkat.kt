package com.example.aplikasiforma.pengisian

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasiforma.PreferencesHelper
import com.example.aplikasiforma.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class UraianSingkat : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var etUraianSingkat: TextInputEditText
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uraian_singkat)

        // Inisialisasi FirebaseAuth dan PreferencesHelper
        auth = FirebaseAuth.getInstance()
        preferencesHelper = PreferencesHelper(this)

        // Inisialisasi komponen UI
        etUraianSingkat = findViewById(R.id.etUraianSingkat)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        // Ambil data dari SharedPreferences jika tersedia
        loadDataFromSharedPreferences()

        // Aksi tombol Next
        btnNext.setOnClickListener {
            val uraianSingkat = etUraianSingkat.text.toString().trim()

            if (uraianSingkat.isNotEmpty()) {
                // Simpan data ke SharedPreferences
                preferencesHelper.saveUraianSingkat(uraianSingkat)

                // Lanjutkan ke halaman berikutnya
                val intent = Intent(this, DugaanPelanggaran::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Uraian Singkat harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        // Aksi tombol Previous
        btnPrevious.setOnClickListener {
            preferencesHelper.saveUraianSingkat(etUraianSingkat.text.toString())
            finish() // Kembali ke halaman sebelumnya
        }
    }

    private fun loadDataFromSharedPreferences() {
        etUraianSingkat.setText(preferencesHelper.getUraianSingkat())
    }
}
