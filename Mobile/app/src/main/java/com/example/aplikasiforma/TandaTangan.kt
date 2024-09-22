package com.example.aplikasiforma

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class TandaTangan : AppCompatActivity() {

    private lateinit var etTanggalTtd: TextInputEditText
    private lateinit var etJabatanTtd: TextInputEditText
    private lateinit var etNamaTtd: TextInputEditText
    private lateinit var signaturePad: SignaturePad
    private lateinit var btnSaveSignature: Button
    private lateinit var btnClearSignature: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var documentGenerator: DocumentGenerator

    companion object {
        const val REQUEST_CODE_READ_STORAGE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanda_tangan)

        // Inisialisasi FirebaseAuth, PreferencesHelper, dan DocumentGenerator
        auth = FirebaseAuth.getInstance()
        preferencesHelper = PreferencesHelper(this)
        documentGenerator = DocumentGenerator(this)

        // Inisialisasi komponen UI
        etTanggalTtd = findViewById(R.id.etTanggalttd)
        etJabatanTtd = findViewById(R.id.etJabatanttd)
        etNamaTtd = findViewById(R.id.etNamattd)
        signaturePad = findViewById(R.id.signature_pad)
        btnSaveSignature = findViewById(R.id.btnSaveSignature)
        btnClearSignature = findViewById(R.id.btnClearSignature)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        // Fungsi untuk tombol Clear, membersihkan tanda tangan
        btnClearSignature.setOnClickListener {
            signaturePad.clear() // Membersihkan area SignaturePad
        }

        // Aksi untuk tombol Simpan
        btnSaveSignature.setOnClickListener {
            if (!signaturePad.isEmpty) {
                val signatureBitmap = signaturePad.signatureBitmap
                val signatureBase64 = bitmapToBase64(signatureBitmap)

                // Simpan data ke SharedPreferences
                preferencesHelper.saveSignatureData(
                    etTanggalTtd.text.toString(),
                    etJabatanTtd.text.toString(),
                    etNamaTtd.text.toString(),
                    signatureBase64
                )
                Toast.makeText(this, "Tanda tangan disimpan secara lokal", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Tanda tangan kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Aksi untuk tombol Next: Ekspor dokumen Word
        btnNext.setOnClickListener {
            exportToWord()
        }

        btnPrevious.setOnClickListener {
            finish()
        }

        // Muat data yang tersimpan saat tampilan pertama kali terbuka
        loadSavedSignatureData()

        // Cek dan minta izin akses penyimpanan jika diperlukan
        checkAndRequestPermissions()
    }

    // Fungsi untuk meminta izin akses penyimpanan
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_STORAGE
            )
        }
    }

    // Fungsi untuk mengekspor dokumen Word
    private fun exportToWord() {
        val fileDir = File(getExternalFilesDir(null), "Documents")
        if (!fileDir.exists()) {
            fileDir.mkdirs()  // Buat direktori jika belum ada
        }

        val fileName = "${preferencesHelper.getNomorSurat()}.docx"
        val file = File(fileDir, fileName)

        try {
            val fileOutputStream = FileOutputStream(file)
            // Lanjutkan dengan menulis ke dalam file
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Ambil data yang diperlukan untuk DocumentData dari SharedPreferences
        val documentData = DocumentData(
            noSurat = preferencesHelper.getNomorSurat() ?: "N/A",
            namaPelaksana = preferencesHelper.getNamaPelaksana() ?: "N/A",
            jabatan = preferencesHelper.getJabatan() ?: "N/A",
            nomorSuratperintah = preferencesHelper.getNomorSuratPerintah() ?: "N/A",
            alamat = preferencesHelper.getAlamat() ?: "N/A",
            jenisPemilihan = preferencesHelper.getJenisPemilihan() ?: "N/A",
            tahapanPemilihan = preferencesHelper.getTahapanPemilihan() ?: "N/A",
            bentuk = preferencesHelper.getBentukPengawasan() ?: "N/A",
            tujuan = preferencesHelper.getTujuanPengawasan() ?: "N/A",
            sasaran = preferencesHelper.getSasaranPengawasan() ?: "N/A",
            waktuTempat = preferencesHelper.getWaktuTempatPengawasan() ?: "N/A",
            hasilPengawasan = preferencesHelper.getUraianSingkat() ?: "N/A",
            dugaanPelanggaran = mapOf(),  // Sesuaikan ini dengan data Dugaan Pelanggaran
            potensiSengketa = mapOf()     // Sesuaikan ini dengan data Potensi Sengketa
        )

        // Ambil daftar gambar dari SharedPreferences yang disimpan dari Activity lain
        val selectedImages = preferencesHelper.getImageUris().mapNotNull { uri ->
            try {
                contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                null
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        // Panggil DocumentGenerator untuk membuat dokumen
        val success = documentGenerator.generateAndSaveDocument(
            data = documentData,
            signatureFilePath = null,  // Jika Anda ingin menggunakan path file, sesuaikan
            signatureBitmap = signaturePad.signatureBitmap, // Ambil tanda tangan dari SignaturePad
            selectedImages = selectedImages  // Gambar yang dipilih
        )

        if (success) {
            Toast.makeText(this, "Dokumen berhasil diekspor", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal mengekspor dokumen", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mengubah Bitmap menjadi Base64 String
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Fungsi untuk memuat data tanda tangan yang tersimpan
    private fun loadSavedSignatureData() {
        etTanggalTtd.setText(preferencesHelper.getTanggalTtd())
        etJabatanTtd.setText(preferencesHelper.getJabatanTtd())
        etNamaTtd.setText(preferencesHelper.getNamaTtd())

        // Memuat gambar tanda tangan jika ada
        val signatureBase64 = preferencesHelper.getSignatureImage()
        if (signatureBase64 != null) {
            val signatureBitmap = base64ToBitmap(signatureBase64)
            signaturePad.signatureBitmap = signatureBitmap
        }
    }

    // Fungsi untuk mengubah Base64 String menjadi Bitmap
    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
