package com.example.aplikasiforma

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

        // Setup RecyclerView dengan GridLayoutManager
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        imageAdapter = ImageAdapter(this, selectedImages)
        recyclerView.adapter = imageAdapter

        // Muat gambar yang tersimpan di SharedPreferences
        selectedImages.addAll(preferencesHelper.getImageUris())
        imageAdapter.notifyDataSetChanged()

        // Setup ActivityResultLauncher untuk memilih gambar
        pickImagesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val clipData = result.data!!.clipData
                if (clipData != null) {  // Multiple images selected
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        selectedImages.add(imageUri)
                    }
                } else {  // Single image selected
                    result.data?.data?.let { imageUri ->
                        selectedImages.add(imageUri)
                    }
                }
                imageAdapter.notifyDataSetChanged()

                // Simpan URI gambar ke SharedPreferences
                preferencesHelper.saveImageUris(selectedImages)

                Toast.makeText(this, "${selectedImages.size} gambar dipilih", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Pilih Gambar
        btnSelectImages.setOnClickListener {
            selectImages()
        }

        // Tombol Clear untuk menghapus gambar yang dipilih
        btnClear.setOnClickListener {
            selectedImages.clear()  // Menghapus semua gambar dari list
            imageAdapter.notifyDataSetChanged()
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
