package com.example.aplikasiforma.pengisian

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aplikasiforma.DocumentData
import com.example.aplikasiforma.DocumentGenerator
import com.example.aplikasiforma.HomeActivity
import com.example.aplikasiforma.PreferencesHelper
import com.example.aplikasiforma.R
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TandaTangan : AppCompatActivity() {

    private lateinit var etTanggalTtd: TextInputEditText
    private lateinit var etJabatanTtd: TextInputEditText
    private lateinit var etNamaTtd: TextInputEditText
    private lateinit var signaturePad: SignaturePad
    private lateinit var btnSaveSignature: Button
    private lateinit var btnClearSignature: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var documentGenerator: DocumentGenerator

    private lateinit var progressDialog: ProgressDialog
    private val selectedImages = mutableListOf<Bitmap>() // List untuk menyimpan gambar yang dikonversi ke Bitmap

    companion object {
        const val REQUEST_CODE_READ_STORAGE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanda_tangan)

        // Inisialisasi PreferencesHelper dan DocumentGenerator
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

        // Inisialisasi progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Proses sedang berlangsung...")
        progressDialog.setCancelable(false)

        // Ambil gambar yang tersimpan di SharedPreferences
        loadSavedImagesFromPreferences()

        // Fungsi untuk tombol Clear, membersihkan tanda tangan
        btnClearSignature.setOnClickListener {
            signaturePad.clear()
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
            progressDialog.show() // Menampilkan progress dialog
            val file = exportToWord()
            if (file != null) {
                progressDialog.dismiss()
                Toast.makeText(this, "Dokumen berhasil diekspor", Toast.LENGTH_SHORT).show()
                preferencesHelper.isExported() // Reset gambar setelah ekspor
                val intent = Intent(this@TandaTangan, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal mengekspor dokumen", Toast.LENGTH_SHORT).show()
            }
        }

        btnPrevious.setOnClickListener {
            finish()
        }

        // Muat data yang tersimpan saat tampilan pertama kali terbuka
        loadSavedSignatureData()

        // Cek dan minta izin akses penyimpanan jika diperlukan
        checkAndRequestPermissions()
    }

    // Memeriksa dan meminta izin penyimpanan jika diperlukan
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
        }
    }

    // Ekspor dokumen Word dengan gambar dan tanda tangan dari SharedPreferences
    private fun exportToWord(): File? {
        val nomorSurat = preferencesHelper.getNomorSurat() ?: "default_document"
        val sanitizedNomorSurat = nomorSurat.replace("/", "-")

        // Direktori penyimpanan dokumen
        val fileDir = File(getExternalFilesDir(null), "Documents/$sanitizedNomorSurat")

        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }

        val fileName = "$sanitizedNomorSurat.docx"
        val file = File(fileDir, fileName)

        return try {
            FileOutputStream(file).use { fileOutputStream ->

                // Ambil data dokumen
                val documentData = DocumentData(
                    noSurat = nomorSurat,
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
                    dugaanPelanggaran = mapOf(),
                    potensiSengketa = mapOf()
                )

                // Tambahkan gambar dan tanda tangan ke dokumen
                val success = documentGenerator.generateAndSaveDocument(
                    data = documentData,
                    signatureFilePath = null,
                    signatureBitmap = signaturePad.signatureBitmap,
                    selectedImages = selectedImages // Sertakan gambar yang diambil dari SharedPreferences
                )

                if (success) {
                    fileOutputStream.flush()
                    return file
                } else {
                    null
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Fungsi untuk memuat gambar yang tersimpan di SharedPreferences
    private fun loadSavedImagesFromPreferences() {
        val imageUris = preferencesHelper.getImageUris() // Ambil URI yang tersimpan dari SharedPreferences
        imageUris.forEach { uri ->
            val bitmap = uriToBitmap(Uri.parse(uri)) // Ubah URI menjadi Bitmap
            if (bitmap != null) {
                selectedImages.add(bitmap) // Simpan bitmap ke dalam list selectedImages
            }
        }
    }

    // Konversi Uri menjadi Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Konversi Bitmap ke Base64
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    // Muat data tanda tangan yang tersimpan
    private fun loadSavedSignatureData() {
        etTanggalTtd.setText(preferencesHelper.getTanggalTtd())
        etJabatanTtd.setText(preferencesHelper.getJabatanTtd())
        etNamaTtd.setText(preferencesHelper.getNamaTtd())

        val signatureBase64 = preferencesHelper.getSignatureImage()
        if (signatureBase64 != null) {
            val signatureBitmap = base64ToBitmap(signatureBase64)
            signaturePad.signatureBitmap = signatureBitmap
        }
    }

    // Konversi Base64 ke Bitmap
    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
