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

class DataPengawas : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var etNamaPelaksana: TextInputEditText
    private lateinit var etJabatan: TextInputEditText
    private lateinit var etNomorSuratPerintah: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var auth: FirebaseAuth // Firebase Authentication untuk mendapatkan UID
    private lateinit var progressDialog: ProgressDialog // ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pengawas)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi PreferencesHelper
        preferencesHelper = PreferencesHelper(this)

        // Inisialisasi ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Menyimpan data...") // Pesan untuk progress dialog
        progressDialog.setCancelable(false) // Dialog tidak bisa di-dismiss dengan tombol back

        // Inisialisasi komponen UI
        etNamaPelaksana = findViewById(R.id.etNamaPelaksana)
        etJabatan = findViewById(R.id.etJabatan)
        etNomorSuratPerintah = findViewById(R.id.etNomorsuratperintah)
        etAlamat = findViewById(R.id.etAlamat)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        // Ambil data dari SharedPreferences jika tersedia
        loadDataFromSharedPreferences()

        // Aksi tombol Next
        btnNext.setOnClickListener {
            progressDialog.show()
            val namaPelaksana = etNamaPelaksana.text.toString()
            val jabatan = etJabatan.text.toString()
            val nomorSuratPerintah = etNomorSuratPerintah.text.toString()
            val alamat = etAlamat.text.toString()

            if (namaPelaksana.isNotEmpty() && jabatan.isNotEmpty() && nomorSuratPerintah.isNotEmpty() && alamat.isNotEmpty()) {
                // Tampilkan ProgressDialog saat mulai proses menyimpan data

                // Simpan data ke SharedPreferences
                preferencesHelper.saveDataPengawas(namaPelaksana, jabatan, nomorSuratPerintah, alamat)

                // Dismiss ProgressDialog setelah proses selesai
                progressDialog.dismiss()

                val intent = Intent(this, JenisTahapan::class.java)
                intent.putExtra("nama_pelaksana", namaPelaksana)
                startActivity(intent)
                Toast.makeText(this, "Data berhasil disimpan.", Toast.LENGTH_SHORT).show()
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
        etNamaPelaksana.setText(preferencesHelper.getNamaPelaksana())
        etJabatan.setText(preferencesHelper.getJabatan())
        etNomorSuratPerintah.setText(preferencesHelper.getNomorSuratPerintah())
        etAlamat.setText(preferencesHelper.getAlamat())
    }
}
