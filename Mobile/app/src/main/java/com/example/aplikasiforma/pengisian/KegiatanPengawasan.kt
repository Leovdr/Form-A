package com.example.aplikasiforma.pengisian

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasiforma.PreferencesHelper
import com.example.aplikasiforma.R
import com.google.android.material.textfield.TextInputEditText

class KegiatanPengawasan : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var etBentuk: TextInputEditText
    private lateinit var etTujuan: TextInputEditText
    private lateinit var etSasaran: TextInputEditText
    private lateinit var etWaktuTempat: TextInputEditText
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kegiatan_pengawasan)

        // Inisialisasi PreferencesHelper
        preferencesHelper = PreferencesHelper(this)

        // Inisialisasi komponen UI
        etBentuk = findViewById(R.id.etBentuk)
        etTujuan = findViewById(R.id.etTujuan)
        etSasaran = findViewById(R.id.etSasaran)
        etWaktuTempat = findViewById(R.id.etWaktuTempat)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        // Ambil data dari SharedPreferences jika tersedia
        loadDataFromSharedPreferences()

        // Aksi tombol Next
        btnNext.setOnClickListener {
            val bentuk = etBentuk.text.toString().trim()
            val tujuan = etTujuan.text.toString().trim()
            val sasaran = etSasaran.text.toString().trim()
            val waktuTempat = etWaktuTempat.text.toString().trim()

            if (bentuk.isNotEmpty() && tujuan.isNotEmpty() && sasaran.isNotEmpty() && waktuTempat.isNotEmpty()) {
                // Simpan data ke SharedPreferences
                preferencesHelper.saveKegiatanPengawasan(bentuk, tujuan, sasaran, waktuTempat)

                // Lanjutkan ke halaman berikutnya
                val intent = Intent(this, UraianSingkat::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        // Aksi tombol Previous
        btnPrevious.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }

    private fun loadDataFromSharedPreferences() {
        etBentuk.setText(preferencesHelper.getBentukPengawasan())
        etTujuan.setText(preferencesHelper.getTujuanPengawasan())
        etSasaran.setText(preferencesHelper.getSasaranPengawasan())
        etWaktuTempat.setText(preferencesHelper.getWaktuTempatPengawasan())
    }
}
