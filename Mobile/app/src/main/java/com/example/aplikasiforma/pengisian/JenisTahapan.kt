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

class JenisTahapan : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var etJenisPemilihan: TextInputEditText
    private lateinit var etTahapanPemilihan: TextInputEditText
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Menyimpan data...") // Pesan untuk progress dialog
        progressDialog.setCancelable(false) // Dialog tidak bisa di-dismiss dengan tombol back
        setContentView(R.layout.activity_jenis_tahapan)

        // Inisialisasi PreferencesHelper
        preferencesHelper = PreferencesHelper(this)

        // Inisialisasi komponen UI
        etJenisPemilihan = findViewById(R.id.etJenisPemilihan)
        etTahapanPemilihan = findViewById(R.id.etTahapanpemilihan)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        // Ambil data dari SharedPreferences jika tersedia
        loadDataFromSharedPreferences()

        // Aksi tombol Next
        btnNext.setOnClickListener {
            progressDialog.show()
            val jenisPemilihan = etJenisPemilihan.text.toString().trim()
            val tahapanPemilihan = etTahapanPemilihan.text.toString().trim()

            if (jenisPemilihan.isNotEmpty() && tahapanPemilihan.isNotEmpty()) {
                // Simpan data ke SharedPreferences
                preferencesHelper.saveJenisTahapan(jenisPemilihan, tahapanPemilihan)

                progressDialog.dismiss()

                // Lanjutkan ke halaman berikutnya
                val intent = Intent(this, KegiatanPengawasan::class.java)
                startActivity(intent)
                Toast.makeText(this, "Data berhasil tersimpan", Toast.LENGTH_SHORT).show()
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
        etJenisPemilihan.setText(preferencesHelper.getJenisPemilihan())
        etTahapanPemilihan.setText(preferencesHelper.getTahapanPemilihan())
    }
}
