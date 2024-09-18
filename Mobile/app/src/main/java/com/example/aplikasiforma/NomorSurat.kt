package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class NomorSurat : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var etNosurat: TextInputEditText
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var auth: FirebaseAuth // Firebase Authentication untuk mengambil user_id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nomor_surat)

        // Inisialisasi PreferencesHelper dan FirebaseAuth
        preferencesHelper = PreferencesHelper(this)
        auth = FirebaseAuth.getInstance()

        // Inisialisasi komponen UI
        etNosurat = findViewById(R.id.etNosurat)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        // Ambil data yang sudah disimpan di SharedPreferences jika ada
        val savedNomorSurat = preferencesHelper.getNomorSurat()
        if (savedNomorSurat != null) {
            etNosurat.setText(savedNomorSurat)
        }

        // Aksi tombol Next
        btnNext.setOnClickListener {
            val nomorSurat = etNosurat.text.toString()

            if (nomorSurat.isNotEmpty()) {
                uploadNomorSuratToDatabase(nomorSurat) { success ->
                    if (success) {
                        val intent = Intent(this, DataPengawas::class.java)
                        intent.putExtra("nomor_surat", nomorSurat)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Gagal menyimpan Nomor Surat ke database.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Nomor Surat harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }


        // Aksi tombol Previous
        btnPrevious.setOnClickListener {
            // Simpan data ke SharedPreferences sebelum kembali
            preferencesHelper.saveNomorSurat(etNosurat.text.toString())
            finish() // Kembali ke halaman sebelumnya
        }
    }

    private fun uploadNomorSuratToDatabase(nomorSurat: String, callback: (Boolean) -> Unit) {
        val url = "https://kaftapus.web.id/api/save_nosurat.php"
        val uid = preferencesHelper.getUid()

        if (uid.isNullOrEmpty()) {
            Toast.makeText(this, "UID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                if (response.contains("success")) {
                    callback(true)
                } else {
                    callback(false)
                }
            },
            { error ->
                error.printStackTrace()
                callback(false)
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["nomor_surat"] = nomorSurat
                params["uid"] = uid // Kirim UID pengguna
                return params
            }
        }

        requestQueue.add(stringRequest)
    }



    // Simpan data saat Activity dihancurkan
    override fun onDestroy() {
        super.onDestroy()
        preferencesHelper.saveNomorSurat(etNosurat.text.toString())
    }
}
