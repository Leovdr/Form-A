package com.example.aplikasiforma

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etNosurat: EditText
    private lateinit var etNamaPengawas: EditText
    private lateinit var etJabatan: EditText
    private lateinit var etNomorSurat: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etTahapan: EditText
    private lateinit var etKegiatan: EditText
    private lateinit var etTujuan: EditText
    private lateinit var etSasaran: EditText
    private lateinit var etWaktuTempat: EditText
    private lateinit var etHasilPengawasan: EditText
    private lateinit var etDugaanPelanggaran: EditText
    private lateinit var etPotensiSengketa: EditText
    private lateinit var btnExport: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Inisialisasi semua EditText berdasarkan ID
        etNosurat = findViewById(R.id.etNosurat)
        etNamaPengawas = findViewById(R.id.etNamaPengawas)
        etJabatan = findViewById(R.id.etJabatan)
        etNomorSurat = findViewById(R.id.etNomorSurat)
        etAlamat = findViewById(R.id.etAlamat)
        etTahapan = findViewById(R.id.etTahapan)
        etKegiatan = findViewById(R.id.etKegiatan)
        etTujuan = findViewById(R.id.etTujuan)
        etSasaran = findViewById(R.id.etSasaran)
        etWaktuTempat = findViewById(R.id.etWaktuTempat)
        etHasilPengawasan = findViewById(R.id.etHasilPengawasan)
        etDugaanPelanggaran = findViewById(R.id.etDugaanPelanggaran)
        etPotensiSengketa = findViewById(R.id.etPotensiSengketa)
        btnExport = findViewById(R.id.btnExport)

        btnExport.setOnClickListener {
            if (checkPermission()) {
                generateDocument()
            } else {
                requestPermission()
            }
        }
    }

    private fun generateDocument() {
        val data = DocumentData(
            noSurat = etNosurat.text.toString(),
            namaPengawas = etNamaPengawas.text.toString(),
            jabatan = etJabatan.text.toString(),
            nomorSurat = etNomorSurat.text.toString(),
            alamat = etAlamat.text.toString(),
            tahapan = etTahapan.text.toString(),
            kegiatan = etKegiatan.text.toString(),
            tujuan = etTujuan.text.toString(),
            sasaran = etSasaran.text.toString(),
            waktuTempat = etWaktuTempat.text.toString(),
            hasilPengawasan = etHasilPengawasan.text.toString(),
            dugaanPelanggaran = etDugaanPelanggaran.text.toString(),
            potensiSengketa = etPotensiSengketa.text.toString()
        )

        val generator = DocumentGenerator(this)
        val isSaved = generator.generateAndSaveDocument(data)

        if (isSaved) {
            Toast.makeText(this, "File saved successfully!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Failed to save file.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            true
        } else {
            val result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            )
        }
    }
}
