package com.example.aplikasiforma.pengisian

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasiforma.R

class PotensiSengketa : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_potensi_sengketa)

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

            // Logika setelah menekan tombol Next
            Toast.makeText(this, "Data berhasil disimpan.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Lampiran::class.java)
            startActivity(intent)
        }

        // Set up checkbox listeners to toggle visibility of corresponding layouts
        setCheckboxListener(findViewById(R.id.checkboxPesertaPemilihan), findViewById(R.id.layoutPesertaPemilihan))
        setCheckboxListener(findViewById(R.id.checkboxTempatKejadian), findViewById(R.id.layoutTempatKejadian))
        setCheckboxListener(findViewById(R.id.checkboxWaktuKejadian), findViewById(R.id.layoutWaktuKejadian))
        setCheckboxListener(findViewById(R.id.checkboxBentukObjek), findViewById(R.id.layoutBentukObjek))
        setCheckboxListener(findViewById(R.id.checkboxIdentitasObjek), findViewById(R.id.layoutIdentitasObjek))
        setCheckboxListener(findViewById(R.id.checkboxHariTanggal), findViewById(R.id.layoutHariTanggal))
        setCheckboxListener(findViewById(R.id.checkboxKerugianLangsung), findViewById(R.id.layoutKerugianLangsung))
        setCheckboxListener(findViewById(R.id.checkboxPotensiSengketa), findViewById(R.id.layoutPotensiSengketa))
    }

    // Fungsi untuk mengatur visibilitas layout berdasarkan checkbox
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
}
