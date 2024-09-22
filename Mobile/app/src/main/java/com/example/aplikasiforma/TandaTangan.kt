package com.example.aplikasiforma

import android.Manifest
import android.app.ProgressDialog
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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.*

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

    private lateinit var progressDialog: ProgressDialog

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

        // Inisialisasi progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Mengunggah dokumen...")
        progressDialog.setCancelable(false)

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

        // Aksi untuk tombol Next: Ekspor dokumen Word dan unggah ke server
        btnNext.setOnClickListener {
            progressDialog.show()
            val file = exportToWord()
            if (file != null) {
                // Ambil nomor_surat dari SharedPreferences
                val nomorSurat = preferencesHelper.getNomorSurat()

                if (!nomorSurat.isNullOrEmpty()) {
                    uploadFileToServer(file, nomorSurat)
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Nomor surat tidak ditemukan di penyimpanan.", Toast.LENGTH_SHORT).show()
                }
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

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
        }
    }

    private fun uploadFileToServer(file: File, nomorSurat: String) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Pengguna belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid

        val url = "https://kaftapus.web.id/api/save_dokumen.php"
        val requestQueue = Volley.newRequestQueue(this)

        val multipartRequest = object : VolleyMultipartRequest(
            Method.POST, url,
            Response.Listener { response ->
                val responseStr = String(response.data)
                Log.d("UPLOAD_RESPONSE", responseStr)
                progressDialog.dismiss()
                if (responseStr.contains("success")) {
                    Toast.makeText(this, "Dokumen berhasil diunggah", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal mengunggah dokumen", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                error.printStackTrace()
                Toast.makeText(this, "Terjadi kesalahan: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {

            override fun getByteData(): Map<String, DataPart> {
                val params = HashMap<String, DataPart>()
                params["file"] = DataPart(file.name, file.readBytes(), "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                return params
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["uid"] = uid
                params["nomor_surat"] = nomorSurat // Ambil dari SharedPreferences
                Log.d("UPLOAD_PARAMS", params.toString())
                return params
            }
        }
        requestQueue.add(multipartRequest)
    }


    private fun exportToWord(): File? {
        val nomorSurat = preferencesHelper.getNomorSurat()?.takeIf { it.isNotBlank() } ?: "default_document"

        val fileDir = File(getExternalFilesDir(null), "Documents")
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }

        val fileName = "$nomorSurat.docx"
        val file = File(fileDir, fileName)

        try {
            val fileOutputStream = FileOutputStream(file)

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

            val selectedImages = preferencesHelper.getImageUris().mapNotNull { uri ->
                try {
                    val tempFile = createTempFileFromUri(uri)
                    BitmapFactory.decodeFile(tempFile.absolutePath)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    null
                }
            }

            val success = documentGenerator.generateAndSaveDocument(
                data = documentData,
                signatureFilePath = null,
                signatureBitmap = signaturePad.signatureBitmap,
                selectedImages = selectedImages
            )

            if (success) {
                Toast.makeText(this, "Dokumen berhasil diekspor sebagai $fileName", Toast.LENGTH_SHORT).show()
                return file
            } else {
                Toast.makeText(this, "Gagal mengekspor dokumen", Toast.LENGTH_SHORT).show()
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal membuat file dokumen", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("tempImage", ".jpg", cacheDir)

        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input?.copyTo(output)
            }
        }

        return tempFile
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

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

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
