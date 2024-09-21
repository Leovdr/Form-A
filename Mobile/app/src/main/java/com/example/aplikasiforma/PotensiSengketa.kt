package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class PotensiSengketa : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_potensi_sengketa)

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
            val potensiSengketaData = getPotensiSengketaData()
            uploadPotensiSengketaToDatabase(potensiSengketaData) { success ->
                if (success) {
                    val intent = Intent(this, Lampiran::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Gagal mengirim data ke server.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fungsi untuk mengambil data dari form Potensi Sengketa
    private fun getPotensiSengketaData(): Map<String, String> {
        return mapOf(
            "peserta_pemilihan" to getInfo(findViewById(R.id.checkboxPesertaPemilihan), findViewById(R.id.etPesertaPemilihan)),
            "tempat_kejadian" to getInfo(findViewById(R.id.checkboxTempatKejadian), findViewById(R.id.etTempatKejadian)),
            "waktu_kejadian" to getInfo(findViewById(R.id.checkboxWaktuKejadian), findViewById(R.id.etWaktuKejadian)),
            "bentuk_objek" to getInfo(findViewById(R.id.checkboxBentukObjek), findViewById(R.id.etBentukObjek)),
            "identitas_objek" to getInfo(findViewById(R.id.checkboxIdentitasObjek), findViewById(R.id.etIdentitasObjek)),
            "hari_tanggal_dikeluarkan" to getInfo(findViewById(R.id.checkboxHariTanggal), findViewById(R.id.etHariTanggal)),
            "kerugian_langsung" to getInfo(findViewById(R.id.checkboxKerugianLangsung), findViewById(R.id.etKerugianLangsung)),
            "uraian_singkat" to getInfo(findViewById(R.id.checkboxPotensiSengketa), findViewById(R.id.etPotensiSengketa))
        )
    }

    // Fungsi untuk mendapatkan data dari checkbox dan EditText
    private fun getInfo(checkbox: CheckBox, editText: EditText): String {
        return if (checkbox.isChecked) editText.text.toString() else "Nihil"
    }

    // Fungsi untuk mengirimkan data Potensi Sengketa ke server
    private fun uploadPotensiSengketaToDatabase(potensiSengketaData: Map<String, String>, callback: (Boolean) -> Unit) {
        val url = "https://kaftapus.web.id/api/save_potensisengketa.php"
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
                    params.putAll(potensiSengketaData)
                    // Tambahkan log untuk data yang dikirim
                    Log.d("PotensiSengketa", "Data yang dikirim ke server: $params")
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
