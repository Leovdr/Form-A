package com.example.aplikasiforma.pengisian

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasiforma.ImageAdapter
import com.example.aplikasiforma.PreferencesHelper
import com.example.aplikasiforma.R

class Lampiran : AppCompatActivity() {

    private lateinit var btnSelectImages: Button
    private lateinit var btnClear: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private val selectedImages = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var pickImagesLauncher: ActivityResultLauncher<Intent>
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var progressDialog: ProgressDialog // ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lampiran)

        // Inisialisasi Views
        btnSelectImages = findViewById(R.id.btnSelectImages)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        preferencesHelper = PreferencesHelper(this)

        // Inisialisasi ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Memproses gambar...")
        progressDialog.setCancelable(false)

        // Setup RecyclerView dengan GridLayoutManager
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        imageAdapter = ImageAdapter(this, selectedImages)
        recyclerView.adapter = imageAdapter

        // Muat gambar yang tersimpan di SharedPreferences
        selectedImages.addAll(preferencesHelper.getImageUris())
        imageAdapter.notifyItemRangeInserted(0, selectedImages.size) // Efficient update

        // Setup ActivityResultLauncher untuk memilih gambar
        pickImagesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            progressDialog.show()  // Tampilkan ProgressDialog saat gambar dipilih
            if (result.resultCode == RESULT_OK && result.data != null) {
                val clipData = result.data!!.clipData
                if (clipData != null) {
                    val startIndex = selectedImages.size
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        selectedImages.add(imageUri)
                        imageAdapter.notifyItemInserted(selectedImages.size - 1) // Efficient update
                    }
                } else {
                    result.data?.data?.let { imageUri ->
                        selectedImages.add(imageUri)
                        imageAdapter.notifyItemInserted(selectedImages.size - 1) // Efficient update
                    }
                }

                // Simpan URI gambar ke SharedPreferences
                preferencesHelper.saveImageUris(selectedImages)
                Toast.makeText(this, "${selectedImages.size} gambar dipilih", Toast.LENGTH_SHORT).show()
            }
            progressDialog.dismiss()  // Sembunyikan ProgressDialog setelah proses selesai
        }

        // Tombol Pilih Gambar
        btnSelectImages.setOnClickListener {
            selectImages()
        }

        // Tombol Clear untuk menghapus gambar yang dipilih
        btnClear.setOnClickListener {
            val sizeBeforeClear = selectedImages.size
            selectedImages.clear()  // Menghapus semua gambar dari list
            imageAdapter.notifyItemRangeRemoved(0, sizeBeforeClear) // Efficient update
            preferencesHelper.saveImageUris(selectedImages)  // Hapus gambar dari SharedPreferences
            Toast.makeText(this, "Semua gambar telah dihapus", Toast.LENGTH_SHORT).show()
        }

        // Tombol Previous untuk mengakhiri aktivitas
        btnPrevious.setOnClickListener {
            finish()  // Mengakhiri aktivitas saat ini dan kembali ke aktivitas sebelumnya
        }

        // Tombol Next untuk membuka aktivitas berikutnya
        btnNext.setOnClickListener {
            val intent = Intent(this, TandaTangan::class.java)
            startActivity(intent)  // Berpindah ke aktivitas berikutnya
        }
    }

    private fun selectImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickImagesLauncher.launch(intent)
    }
}
