package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText

class JenisTahapan : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var etJenisPemilihan: TextInputEditText
    private lateinit var etTahapanPemilihan: TextInputEditText
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            val jenisPemilihan = etJenisPemilihan.text.toString().trim()
            val tahapanPemilihan = etTahapanPemilihan.text.toString().trim()
            val uid = preferencesHelper.getUid() // Mengambil UID dari SharedPreferences

            if (jenisPemilihan.isNotEmpty() && tahapanPemilihan.isNotEmpty() && uid != null) {
                // Simpan data ke SharedPreferences
                preferencesHelper.saveJenisTahapan(jenisPemilihan, tahapanPemilihan)

                // Upload data ke database
                uploadJenisTahapanToDatabase(jenisPemilihan, tahapanPemilihan, uid) { success ->
                    if (success) {
                        // Lanjutkan ke halaman berikutnya jika sukses
                        Toast.makeText(
                            this,
                            "Data berhasil disimpan ke server.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, KegiatanPengawasan::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Gagal menyimpan data ke server.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
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

    private fun uploadJenisTahapanToDatabase(
        jenisPemilihan: String,
        tahapanPemilihan: String,
        uid: String,
        callback: (Boolean) -> Unit
    ) {
        val url = "https://kaftapus.web.id/api/save_jenistahapan.php" // Ganti dengan URL endpoint PHP Anda

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                // Logika jika respons berhasil
                if (response.contains("success")) {
                    callback(true)
                } else {
                    callback(false)
                }
            },
            { error ->
                // Logika jika ada error
                error.printStackTrace()
                callback(false)
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["jenis_pemilihan"] = jenisPemilihan
                params["tahapan_pemilihan"] = tahapanPemilihan
                params["uid"] = uid // Mengirim UID pengguna
                return params
            }
        }

        // Tambahkan request ke antrian
        requestQueue.add(stringRequest)
    }
}
