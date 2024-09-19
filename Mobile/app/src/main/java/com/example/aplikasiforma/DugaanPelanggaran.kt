package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
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
            val dugaanPelanggaranData = getDugaanPelanggaran()
            uploadDugaanPelanggaranToDatabase(dugaanPelanggaranData) { success ->
                if (success) {
                    val intent = Intent(this, PotensiSengketa::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Gagal mengirim data ke server.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set listener for each checkbox to show/hide the related layout
        setCheckboxListener(findViewById(R.id.checkboxPeristiwa), findViewById(R.id.layoutPeristiwa))
        setCheckboxListener(findViewById(R.id.checkboxTempatkejadian), findViewById(R.id.layoutTempatkejadian))
        setCheckboxListener(findViewById(R.id.checkboxWaktukejadian), findViewById(R.id.layoutWaktukejadian))
        setCheckboxListener(findViewById(R.id.checkboxPelaku), findViewById(R.id.layoutPelaku))
        setCheckboxListener(findViewById(R.id.checkboxAlamat), findViewById(R.id.layoutAlamat))
        setCheckboxListener(findViewById(R.id.checkboxPasal), findViewById(R.id.layoutPasal))
        setCheckboxListener(findViewById(R.id.checkboxNama1), findViewById(R.id.layoutNama1))
        setCheckboxListener(findViewById(R.id.checkboxAlamatsaksi1), findViewById(R.id.layoutAlamatsaksi1))
        setCheckboxListener(findViewById(R.id.checkboxNama2), findViewById(R.id.layoutNama2))
        setCheckboxListener(findViewById(R.id.checkboxAlamatsaksi2), findViewById(R.id.layoutAlamatsaksi2))
        setCheckboxListener(findViewById(R.id.checkboxBarangbukti), findViewById(R.id.layoutBarangbukti))
        setCheckboxListener(findViewById(R.id.checkboxUraian), findViewById(R.id.layoutUraian))
        setCheckboxListener(findViewById(R.id.checkboxJenis), findViewById(R.id.layoutJenis))
        setCheckboxListener(findViewById(R.id.checkboxFakta), findViewById(R.id.layoutFakta))
        setCheckboxListener(findViewById(R.id.checkboxAnalisa), findViewById(R.id.layoutAnalisa))
        setCheckboxListener(findViewById(R.id.checkboxTindak), findViewById(R.id.layoutTindak))
    }

    private fun setCheckboxListener(checkbox: CheckBox, layout: LinearLayout) {
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            layout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    private fun getDugaanPelanggaran(): Map<String, String> {
        return mapOf(
            "Peristiwa" to getInfo(findViewById(R.id.checkboxPeristiwa), findViewById(R.id.etInformasiPeristiwa)),
            "Tempat Kejadian" to getInfo(findViewById(R.id.checkboxTempatkejadian), findViewById(R.id.etInformasiTempatkejadian)),
            "Waktu Kejadian" to getInfo(findViewById(R.id.checkboxWaktukejadian), findViewById(R.id.etInformasiWaktukejadian)),
            "Pelaku" to getInfo(findViewById(R.id.checkboxPelaku), findViewById(R.id.etInformasiPelaku)),
            "Alamat" to getInfo(findViewById(R.id.checkboxAlamat), findViewById(R.id.etInformasiAlamat)),
            "Pasal yang dilanggar" to getInfo(findViewById(R.id.checkboxPasal), findViewById(R.id.etInformasiPasal)),
            "Nama Saksi 1" to getInfo(findViewById(R.id.checkboxNama1), findViewById(R.id.etInformasiNama1)),
            "Alamat Saksi 1" to getInfo(findViewById(R.id.checkboxAlamatsaksi1), findViewById(R.id.etInformasiAlamatsaksi1)),
            "Nama Saksi 2" to getInfo(findViewById(R.id.checkboxNama2), findViewById(R.id.etInformasiNama2)),
            "Alamat Saksi 2" to getInfo(findViewById(R.id.checkboxAlamatsaksi2), findViewById(R.id.etInformasiAlamatsaksi2)),
            "Bukti" to getInfo(findViewById(R.id.checkboxBarangbukti), findViewById(R.id.etInformasiBarangbukti)),
            "Uraian Singkat" to getInfo(findViewById(R.id.checkboxUraian), findViewById(R.id.etInformasiUraian)),
            "Jenis Dugaan" to getInfo(findViewById(R.id.checkboxJenis), findViewById(R.id.etInformasiJenis)),
            "Fakta dan Keterangan" to getInfo(findViewById(R.id.checkboxFakta), findViewById(R.id.etInformasiFakta)),
            "Analisa" to getInfo(findViewById(R.id.checkboxAnalisa), findViewById(R.id.etInformasiAnalisa)),
            "Tindak lanjut" to getInfo(findViewById(R.id.checkboxTindak), findViewById(R.id.etInformasiTindak))
        )
    }

    private fun getInfo(checkbox: CheckBox, editText: EditText): String {
        return if (checkbox.isChecked) editText.text.toString() else "Nihil"
    }

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
