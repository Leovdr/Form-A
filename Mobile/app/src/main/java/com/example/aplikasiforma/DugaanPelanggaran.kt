package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth

class DugaanPelanggaran : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dugaan_pelanggaran)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Button Previous
        val btnPrevious: Button = findViewById(R.id.btnPrevious)
        btnPrevious.setOnClickListener {
            onBackPressed()
            finish()
        }

        // Button Next
        val btnNext: Button = findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            val dugaanPelanggaranData = getDugaanPelanggaranData()
            uploadDugaanPelanggaranToDatabase(dugaanPelanggaranData) { success ->
                if (success) {
                    val intent = Intent(this, PotensiSengketa::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Gagal mengirim data ke server.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fungsi untuk mengambil data dari form Dugaan Pelanggaran
    private fun getDugaanPelanggaranData(): Map<String, String> {
        return mapOf(
            "peristiwa" to getInfo(findViewById(R.id.checkboxPeristiwa), findViewById(R.id.etInformasiPeristiwa)),
            "tempat_kejadian" to getInfo(findViewById(R.id.checkboxTempatkejadian), findViewById(R.id.etInformasiTempatkejadian)),
            "waktu_kejadian" to getInfo(findViewById(R.id.checkboxWaktukejadian), findViewById(R.id.etInformasiWaktukejadian)),
            "pelaku" to getInfo(findViewById(R.id.checkboxPelaku), findViewById(R.id.etInformasiPelaku)),
            "alamat" to getInfo(findViewById(R.id.checkboxAlamat), findViewById(R.id.etInformasiAlamat)),
            "pasal_dilanggar" to getInfo(findViewById(R.id.checkboxPasal), findViewById(R.id.etInformasiPasal)),
            "nama_saksi1" to getInfo(findViewById(R.id.checkboxNama1), findViewById(R.id.etInformasiNama1)),
            "alamat_saksi1" to getInfo(findViewById(R.id.checkboxAlamatsaksi1), findViewById(R.id.etInformasiAlamatsaksi1)),
            "nama_saksi2" to getInfo(findViewById(R.id.checkboxNama2), findViewById(R.id.etInformasiNama2)),
            "alamat_saksi2" to getInfo(findViewById(R.id.checkboxAlamatsaksi2), findViewById(R.id.etInformasiAlamatsaksi2)),
            "bukti" to getInfo(findViewById(R.id.checkboxBarangbukti), findViewById(R.id.etInformasiBarangbukti)),
            "uraian_pelanggaran" to getInfo(findViewById(R.id.checkboxUraian), findViewById(R.id.etInformasiUraian)),
            "jenis_pelanggaran" to getInfo(findViewById(R.id.checkboxJenis), findViewById(R.id.etInformasiJenis)),
            "fakta" to getInfo(findViewById(R.id.checkboxFakta), findViewById(R.id.etInformasiFakta)),
            "analisa" to getInfo(findViewById(R.id.checkboxAnalisa), findViewById(R.id.etInformasiAnalisa)),
            "tindak_lanjut" to getInfo(findViewById(R.id.checkboxTindak), findViewById(R.id.etInformasiTindak))
        )
    }

    // Fungsi untuk mendapatkan data dari checkbox dan EditText
    private fun getInfo(checkbox: CheckBox, editText: EditText): String {
        return if (checkbox.isChecked) editText.text.toString() else "Nihil"
    }

    // Fungsi untuk mengirimkan data Dugaan Pelanggaran ke server
    private fun uploadDugaanPelanggaranToDatabase(dugaanPelanggaranData: Map<String, String>, callback: (Boolean) -> Unit) {
        val url = "https://kaftapus.web.id/api/save_dugaanpelanggaran.php"
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val requestQueue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener { response ->
                    if (response.contains("success")) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    callback(false)
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["uid"] = uid
                    params.putAll(dugaanPelanggaranData)
                    return params
                }
            }
            requestQueue.add(stringRequest)
        } else {
            Toast.makeText(this, "Gagal mendapatkan UID pengguna.", Toast.LENGTH_SHORT).show()
            callback(false)
        }
    }
}
