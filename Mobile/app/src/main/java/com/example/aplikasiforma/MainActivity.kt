package com.example.aplikasiforma

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val fragments = listOf(
        FragmentDataPengawas(),
        FragmentJenisdanTahapan(),
        FragmentKegiatanPengawasan(),
        FragmentHasilPengawasan(),
        FragmentDugaanPelanggaran(),
        FragmentPotensiSengketa(),
        FragmentLampiranGambar(),
        FragmentSignature()
    )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            collectDataAndExportDocument()
        } else {
            Toast.makeText(this, "Izin ditolak. Tidak dapat menyimpan dokumen.", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = SectionsPagerAdapter(this, fragments)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Data Pengawas"
                1 -> "Jenis dan Tahapan Pengawasan"
                2 -> "Kegiatan Pengawasan"
                3 -> "Uraian Singkat Hasil Pengawasan"
                4 -> "Informasi Dugaan Pelanggaran"
                5 -> "Informasi Potensi Sengketa Pemilihan"
                6 -> "Lampiran Gambar"
                7 -> "Signature"
                else -> null
            }
        }.attach()

        findViewById<Button>(R.id.btnExport).setOnClickListener {
            checkStoragePermissionAndGenerateDocument()
        }

        sharedViewModel.documentData.observe(this, Observer { data ->
            // Handle data updates, jika diperlukan.
        })
    }

    private fun checkStoragePermissionAndGenerateDocument() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                collectDataAndExportDocument()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                collectDataAndExportDocument()
            }
        }
    }

    private fun collectDataAndExportDocument() {
        // Inisiasi fragment yang sudah ada
        val dataPengawasFragment = fragments[0] as FragmentDataPengawas
        val jenisdanTahapanFragment = fragments[1] as FragmentJenisdanTahapan
        val kegiatanPengawasanFragment = fragments[2] as FragmentKegiatanPengawasan
        val hasilPengawasanFragment = fragments[3] as FragmentHasilPengawasan
        val dugaanPelanggaranFragment = fragments[4] as FragmentDugaanPelanggaran
        val potensiSengketaFragment = fragments[5] as FragmentPotensiSengketa
        val lampiranGambarFragment = fragments[6] as FragmentLampiranGambar
        val signatureFragment = fragments[7] as FragmentSignature

        // Mengambil URI gambar yang dipilih dari FragmentLampiranGambar dan mengonversinya menjadi Bitmap
        val selectedImageUris = lampiranGambarFragment.getSelectedImages()
        val selectedImages = selectedImageUris.mapNotNull { uriToBitmap(it) } // Konversi URI ke Bitmap

        // Mengambil data dari fragment lain
        val data = DocumentData(
            noSurat = dataPengawasFragment.getNoSurat().orEmpty(),
            namaPelaksana = dataPengawasFragment.getNamaPelaksana().orEmpty(),
            jabatan = dataPengawasFragment.getJabatan().orEmpty(),
            nomorSuratperintah = dataPengawasFragment.getNomorsuratperintah().orEmpty(),
            alamat = dataPengawasFragment.getAlamat().orEmpty(),
            bentuk = kegiatanPengawasanFragment.getBentuk().orEmpty(),
            tujuan = kegiatanPengawasanFragment.getTujuan().orEmpty(),
            sasaran = kegiatanPengawasanFragment.getSasaran().orEmpty(),
            waktuTempat = kegiatanPengawasanFragment.getWaktuTempat().orEmpty(),
            hasilPengawasan = hasilPengawasanFragment.getHasilPengawasan().orEmpty(),
            dugaanPelanggaran = dugaanPelanggaranFragment.getDugaanPelanggaran(),
            potensiSengketa = potensiSengketaFragment.getPotensiSengketa().orEmpty(),
            tahapanPemilihan = jenisdanTahapanFragment.getTahapanpemilihan().orEmpty(),
            jenisPemilihan = jenisdanTahapanFragment.getJenispemilihan().orEmpty()
        )

        // Mengambil tanda tangan dan input dari FragmentSignature
        val signatureFilePath = signatureFragment.getSignatureFilePath()
        val signatureBitmap = signatureFragment.getSignatureBitmap()
        val signatureDate = signatureFragment.getSignatureDate().orEmpty()
        val signaturePosition = signatureFragment.getSignaturePosition().orEmpty()
        val signatureName = signatureFragment.getSignatureName().orEmpty()

        // Memperbarui data di ViewModel
        sharedViewModel.updateDocumentData(data)
        generateDocument(signatureFilePath, signatureBitmap, selectedImages, signatureDate, signaturePosition, signatureName)
    }

    private fun generateDocument(
        signatureFilePath: String?,
        signatureBitmap: Bitmap?,
        selectedImages: List<Bitmap>,
        signatureDate: String,
        signaturePosition: String,
        signatureName: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = sharedViewModel.documentData.value
            if (data != null) {
                val generator = DocumentGenerator(this@MainActivity)
                val isSaved = generator.generateAndSaveDocument(data, signatureFilePath, signatureBitmap, selectedImages, signatureDate, signaturePosition, signatureName)
                withContext(Dispatchers.Main) {
                    if (isSaved) {
                        Toast.makeText(this@MainActivity, "File berhasil disimpan!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Gagal menyimpan file.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Gagal mengumpulkan data dari fragment.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Fungsi untuk mengonversi Uri ke Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private inner class SectionsPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
