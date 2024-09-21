package com.example.aplikasiforma

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class TandaTangan : AppCompatActivity() {

    private lateinit var etTanggalTtd: TextInputEditText
    private lateinit var etJabatanTtd: TextInputEditText
    private lateinit var etNamaTtd: TextInputEditText
    private lateinit var etNomorSurat: TextInputEditText // Untuk nomor surat
    private lateinit var signaturePad: SignaturePad
    private lateinit var btnSaveSignature: Button
    private lateinit var btnClearSignature: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var documentGenerator: DocumentGenerator

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

        // Ambil nomor surat dari server saat tampilan terbuka
        fetchNomorSuratFromServer()
    }

    // Fungsi untuk mengambil nomor surat dari server
    private fun fetchNomorSuratFromServer() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            FetchNomorSuratTask().execute(userId)
        } else {
            Toast.makeText(this, "Gagal mengambil nomor surat, pengguna belum login", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi AsyncTask untuk mengambil nomor surat
    private inner class FetchNomorSuratTask : AsyncTask<String, Void, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            val userId = params[0]
            val url = URL("https://example.com/fetch_nosurat.php?uid=$userId") // URL API PHP Anda

            return try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    inputStream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                try {
                    val jsonResponse = JSONObject(result)
                    val nomorSurat = jsonResponse.getString("nomor_surat")
                    etNomorSurat.setText(nomorSurat)
                } catch (e: Exception) {
                    Toast.makeText(this@TandaTangan, "Gagal memuat nomor surat", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this@TandaTangan, "Tidak ada data nomor surat dari server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportToWord() {
        // Ambil data yang diperlukan untuk DocumentData dari SharedPreferences
        val documentData = DocumentData(
            noSurat = etNomorSurat.text.toString(), // Nomor surat diambil dari input yang sudah terisi dari server
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

        // Ambil daftar gambar dari SharedPreferences sebagai bitmap
        val selectedImages = preferencesHelper.getImageUris().map { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
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
