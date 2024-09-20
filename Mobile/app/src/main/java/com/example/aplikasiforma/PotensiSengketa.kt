package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth

class PotensiSengketa : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesHelper: PreferencesHelper  // Tambahkan PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_potensi_sengketa)

        // Inisialisasi Firebase Auth dan PreferencesHelper
        auth = FirebaseAuth.getInstance()
        preferencesHelper = PreferencesHelper(this)  // Inisialisasi PreferencesHelper

        // Button untuk kembali (Previous)
        val btnPrevious: Button = findViewById(R.id.btnPrevious)
        btnPrevious.setOnClickListener {
            onBackPressed()
            finish()
        }

        // Button Next untuk mengirim data
        val btnNext: Button = findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            val potensiSengketaData = getPotensiSengketaData()
            saveDataLocally(potensiSengketaData)  // Simpan data secara lokal
            uploadPotensiSengketaToDatabase(potensiSengketaData) { success ->
                if (success) {
                    val intent = Intent(this, TandaTangan::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Gagal mengirim data ke server.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set listener for checkboxes to show/hide the related layout
        setCheckboxListener(findViewById(R.id.checkboxPesertaPemilihan), findViewById(R.id.layoutPesertaPemilihan))
        setCheckboxListener(findViewById(R.id.checkboxTempatKejadian), findViewById(R.id.layoutTempatKejadian))
        setCheckboxListener(findViewById(R.id.checkboxWaktuKejadian), findViewById(R.id.layoutWaktuKejadian))
        setCheckboxListener(findViewById(R.id.checkboxBentukObjek), findViewById(R.id.layoutBentukObjek))
        setCheckboxListener(findViewById(R.id.checkboxIdentitasObjek), findViewById(R.id.layoutIdentitasObjek))
        setCheckboxListener(findViewById(R.id.checkboxHariTanggal), findViewById(R.id.layoutHariTanggal))
        setCheckboxListener(findViewById(R.id.checkboxKerugianLangsung), findViewById(R.id.layoutKerugianLangsung))
        setCheckboxListener(findViewById(R.id.checkboxPotensiSengketa), findViewById(R.id.layoutPotensiSengketa))
    }

    private fun setCheckboxListener(checkbox: CheckBox, layout: LinearLayout) {
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            layout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    // Fungsi untuk mengambil data dari form Potensi Sengketa
    private fun getPotensiSengketaData(): Map<String, String> {
        return mapOf(
            "peserta_pemilihan" to getInfo(findViewById(R.id.checkboxPesertaPemilihan), findViewById(R.id.etPesertaPemilihan)),
            "tempat_kejadian" to getInfo(findViewById(R.id.checkboxTempatKejadian), findViewById(R.id.etTempatKejadian)),
            "waktu_kejadian" to getInfo(findViewById(R.id.checkboxWaktuKejadian), findViewById(R.id.etWaktuKejadian)),
            "bentuk_objek_sengketa" to getInfo(findViewById(R.id.checkboxBentukObjek), findViewById(R.id.etBentukObjek)),
            "identitas_objek" to getInfo(findViewById(R.id.checkboxIdentitasObjek), findViewById(R.id.etIdentitasObjek)),
            "hari_tanggal_dikeluarkan" to getInfo(findViewById(R.id.checkboxHariTanggal), findViewById(R.id.etHariTanggal)),
            "kerugian_langsung" to getInfo(findViewById(R.id.checkboxKerugianLangsung), findViewById(R.id.etKerugianLangsung)),
            "uraian_potensi_sengketa" to getInfo(findViewById(R.id.checkboxPotensiSengketa), findViewById(R.id.etPotensiSengketa))
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

            // Menggunakan lambdas sebagai fungsi eksplisit untuk menghindari error lambda
            val responseListener = Response.Listener<String> { response ->
                Log.d("PotensiSengketa", "Response dari server: $response")
                if (response.contains("success")) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            val errorListener = Response.ErrorListener { error ->
                Log.e("PotensiSengketa", "Error response dari server: ", error)
                error.printStackTrace()
                callback(false)
            }

            val stringRequest = object : StringRequest(Request.Method.POST, url, responseListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["uid"] = uid
                    params.putAll(potensiSengketaData)
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

    // Fungsi untuk menyimpan data ke SharedPreferences menggunakan PreferencesHelper
    private fun saveDataLocally(potensiSengketaData: Map<String, String>) {
        preferencesHelper.savePotensiSengketa(
            potensiSengketaData["peserta_pemilihan"] ?: "Nihil",
            potensiSengketaData["tempat_kejadian"] ?: "Nihil",
            potensiSengketaData["waktu_kejadian"] ?: "Nihil",
            potensiSengketaData["bentuk_objek_sengketa"] ?: "Nihil",
            potensiSengketaData["identitas_objek"] ?: "Nihil",
            potensiSengketaData["hari_tanggal_dikeluarkan"] ?: "Nihil",
            potensiSengketaData["kerugian_langsung"] ?: "Nihil",
            potensiSengketaData["uraian_potensi_sengketa"] ?: "Nihil"
        )
        Toast.makeText(this, "Data tersimpan secara lokal.", Toast.LENGTH_SHORT).show()
    }
}
