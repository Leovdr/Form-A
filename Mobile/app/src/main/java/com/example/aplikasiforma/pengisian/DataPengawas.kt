package com.example.aplikasiforma.pengisian

import android.app.ProgressDialog
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
            val namaPelaksana = etNamaPelaksana.text.toString()
            val jabatan = etJabatan.text.toString()
            val nomorSuratPerintah = etNomorSuratPerintah.text.toString()
            val alamat = etAlamat.text.toString()

            if (namaPelaksana.isNotEmpty() && jabatan.isNotEmpty() && nomorSuratPerintah.isNotEmpty() && alamat.isNotEmpty()) {
                // Tampilkan ProgressDialog saat mulai proses unggah
                progressDialog.show()

                // Simpan data ke SharedPreferences
                preferencesHelper.saveDataPengawas(namaPelaksana, jabatan, nomorSuratPerintah, alamat)

                // Upload data ke database
                uploadDataPengawasToDatabase(namaPelaksana, jabatan, nomorSuratPerintah, alamat) { success ->
                    // Selalu dismiss ProgressDialog setelah proses selesai
                    progressDialog.dismiss()

                    if (success) {
                        val intent = Intent(this, JenisTahapan::class.java)
                        intent.putExtra("nama_pelaksana", namaPelaksana)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Gagal menyimpan data ke server.", Toast.LENGTH_SHORT).show()
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
        etNamaPelaksana.setText(preferencesHelper.getNamaPelaksana())
        etJabatan.setText(preferencesHelper.getJabatan())
        etNomorSuratPerintah.setText(preferencesHelper.getNomorSuratPerintah())
        etAlamat.setText(preferencesHelper.getAlamat())
    }

    private fun uploadDataPengawasToDatabase(
        namaPelaksana: String,
        jabatan: String,
        nomorSuratPerintah: String,
        alamat: String,
        callback: (Boolean) -> Unit
    ) {
        val url = "https://kaftapus.web.id/api/save_datapengawas.php" // Ganti dengan URL endpoint PHP Anda

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
                    Toast.makeText(this@DataPengawas, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    callback(false)
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["uid"] = uid // Kirim UID pengguna
                    params["nama_pelaksana"] = namaPelaksana
                    params["jabatan"] = jabatan
                    params["nomor_suratperintah"] = nomorSuratPerintah
                    params["alamat"] = alamat
                    return params
                }
            }

            // Tambahkan request ke antrian
            requestQueue.add(stringRequest)
        } else {
            Toast.makeText(this, "Gagal mendapatkan UID pengguna. Pastikan Anda telah login.", Toast.LENGTH_SHORT).show()
            callback(false)
        }
    }
}
