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

class KegiatanPengawasan : AppCompatActivity() {

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var etBentuk: TextInputEditText
    private lateinit var etTujuan: TextInputEditText
    private lateinit var etSasaran: TextInputEditText
    private lateinit var etWaktuTempat: TextInputEditText
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var auth: FirebaseAuth // Firebase Authentication untuk mendapatkan UID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kegiatan_pengawasan)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

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

                // Upload data ke database
                uploadKegiatanPengawasanToDatabase(bentuk, tujuan, sasaran, waktuTempat) { success ->
                    if (success) {
                        // Lanjutkan ke halaman berikutnya jika sukses
                        Toast.makeText(
                            this,
                            "Data berhasil disimpan ke server.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, UraianSingkat::class.java)
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
        etBentuk.setText(preferencesHelper.getBentukPengawasan())
        etTujuan.setText(preferencesHelper.getTujuanPengawasan())
        etSasaran.setText(preferencesHelper.getSasaranPengawasan())
        etWaktuTempat.setText(preferencesHelper.getWaktuTempatPengawasan())
    }

    private fun uploadKegiatanPengawasanToDatabase(
        bentuk: String,
        tujuan: String,
        sasaran: String,
        waktuTempat: String,
        callback: (Boolean) -> Unit
    ) {
        val url = "https://kaftapus.web.id/api/save_kegiatanpengawasan.php" // Ganti dengan URL endpoint PHP Anda

        // Ambil UID pengguna dari Firebase Authentication
        val uid = auth.currentUser?.uid // Mengambil UID langsung dari Firebase Authentication

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
                    params["bentuk"] = bentuk
                    params["tujuan"] = tujuan
                    params["sasaran"] = sasaran
                    params["waktu_tempat"] = waktuTempat
                    params["uid"] = uid // Mengirim UID pengguna
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
