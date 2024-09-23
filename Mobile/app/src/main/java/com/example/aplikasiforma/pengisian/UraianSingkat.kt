package com.example.aplikasiforma.pengisian

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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

                // Upload data ke database
                uploadUraianSingkatToDatabase(uraianSingkat) { success ->
                    if (success) {
                        // Lanjutkan ke halaman berikutnya
                        Toast.makeText(this, "Data berhasil disimpan ke server.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, DugaanPelanggaran::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Gagal menyimpan data ke server.", Toast.LENGTH_SHORT).show()
                    }
                }
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

    private fun uploadUraianSingkatToDatabase(
        uraianSingkat: String,
        callback: (Boolean) -> Unit
    ) {
        val url = "https://kaftapus.web.id/api/save_uraiansingkat.php" // Ganti dengan URL endpoint PHP Anda

        // Ambil UID pengguna dari Firebase Authentication
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val requestQueue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(
                Method.POST, url,
                { response ->
                    // Jika respons berhasil
                    if (response.contains("success")) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                },
                { error ->
                    // Jika terjadi kesalahan
                    error.printStackTrace()
                    callback(false)
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["uid"] = uid // Kirim UID pengguna
                    params["uraian_singkat"] = uraianSingkat
                    return params
                }
            }

            // Tambahkan request ke antrian
            requestQueue.add(stringRequest)
        } else {
            Toast.makeText(this, "Gagal mendapatkan UID pengguna.", Toast.LENGTH_SHORT).show()
            callback(false)
        }
    }
}
