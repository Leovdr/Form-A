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
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class FragmentPengisian : Fragment() {

    // Mengakses SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    // Request permission handler
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            collectDataAndExportDocument()
        } else {
            Toast.makeText(requireContext(), "Izin ditolak. Tidak dapat menyimpan dokumen.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pengisian, container, false)

        // Inisialisasi ViewPager dan TabLayout
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        // Set offscreenPageLimit agar semua fragment tetap dalam memori
        viewPager.offscreenPageLimit = 7 // Memastikan semua fragment tetap aktif

        // Set adapter untuk ViewPager2 sebelum melampirkan TabLayoutMediator
        viewPager.adapter = SectionsPagerAdapter(this)

        // TabLayoutMediator harus dipanggil setelah adapter diatur
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Data Pengawas"
                1 -> "Jenis dan Tahapan Pengawasan"
                2 -> "Kegiatan Pengawasan"
                3 -> "Uraian Singkat Hasil Pengawasan"
                4 -> "Informasi Dugaan Pelanggaran"
                5 -> "Informasi Potensi Sengketa Pemilihan"
                6 -> "Signature"
                7 -> "Lampiran Gambar"
                else -> null
            }
        }.attach()

        view.findViewById<Button>(R.id.btnExport).setOnClickListener {
            checkStoragePermissionAndGenerateDocument()
            uploadDataToDatabase() // Call upload function after generating document
        }

        return view
    }

    // Method to collect data and export document
    private fun collectDataAndExportDocument() {
        val adapter = viewPager.adapter as SectionsPagerAdapter

        val dataPengawasFragment = adapter.getFragmentAtPosition(0) as? FragmentDataPengawas
        val jenisdanTahapanFragment = adapter.getFragmentAtPosition(1) as? FragmentJenisdanTahapan
        val kegiatanPengawasanFragment = adapter.getFragmentAtPosition(2) as? FragmentKegiatanPengawasan
        val hasilPengawasanFragment = adapter.getFragmentAtPosition(3) as? FragmentHasilPengawasan
        val dugaanPelanggaranFragment = adapter.getFragmentAtPosition(4) as? FragmentDugaanPelanggaran
        val potensiSengketaFragment = adapter.getFragmentAtPosition(5) as? FragmentPotensiSengketa
        val signatureFragment = adapter.getFragmentAtPosition(6) as? FragmentSignature
        val lampiranGambarFragment = adapter.getFragmentAtPosition(7) as? FragmentLampiranGambar

        // Cek apakah fragment yang diperlukan sudah siap
        if (dataPengawasFragment?.view == null ||
            kegiatanPengawasanFragment?.view == null ||
            jenisdanTahapanFragment?.view == null ||
            hasilPengawasanFragment?.view == null ||
            dugaanPelanggaranFragment?.view == null ||
            potensiSengketaFragment?.view == null ||
            signatureFragment?.view == null ||
            lampiranGambarFragment?.view == null) {
            Toast.makeText(requireContext(), "Gagal mengumpulkan data dari fragment. Pastikan semua fragment sudah siap.", Toast.LENGTH_LONG).show()
            return
        }

        val selectedImageUris = lampiranGambarFragment.getSelectedImages()
        val selectedImages = selectedImageUris.mapNotNull { uriToBitmap(it) }

        // Update ViewModel
        sharedViewModel.updateDocumentData(
            DocumentData(
                noSurat = dataPengawasFragment.getNoSurat(),
                namaPelaksana = dataPengawasFragment.getNamaPelaksana(),
                jabatan = dataPengawasFragment.getJabatan(),
                nomorSuratperintah = dataPengawasFragment.getNomorsuratperintah(),
                alamat = dataPengawasFragment.getAlamat(),
                bentuk = kegiatanPengawasanFragment.getBentuk(),
                tujuan = kegiatanPengawasanFragment.getTujuan(),
                sasaran = kegiatanPengawasanFragment.getSasaran(),
                waktuTempat = kegiatanPengawasanFragment.getWaktuTempat(),
                hasilPengawasan = hasilPengawasanFragment.getHasilPengawasan(),
                dugaanPelanggaran = dugaanPelanggaranFragment.getDugaanPelanggaran(),
                potensiSengketa = potensiSengketaFragment.getPotensiSengketa(),
                tahapanPemilihan = jenisdanTahapanFragment.getTahapanpemilihan(),
                jenisPemilihan = jenisdanTahapanFragment.getJenispemilihan()
            )
        )

        val signatureFilePath = signatureFragment.getSignatureFilePath()
        val signatureBitmap = signatureFragment.getSignatureBitmap()
        val signatureDate = signatureFragment.getSignatureDate().orEmpty()
        val signaturePosition = signatureFragment.getSignaturePosition().orEmpty()
        val signatureName = signatureFragment.getSignatureName().orEmpty()

        generateDocument(signatureFilePath, signatureBitmap, selectedImages, signatureDate, signaturePosition, signatureName)
    }

    // Function to upload data to MySQL database
    private fun uploadDataToDatabase() {
        val adapter = viewPager.adapter as SectionsPagerAdapter

        val dataPengawasFragment = adapter.getFragmentAtPosition(0) as? FragmentDataPengawas
        val jenisdanTahapanFragment = adapter.getFragmentAtPosition(1) as? FragmentJenisdanTahapan
        val kegiatanPengawasanFragment = adapter.getFragmentAtPosition(2) as? FragmentKegiatanPengawasan
        val hasilPengawasanFragment = adapter.getFragmentAtPosition(3) as? FragmentHasilPengawasan

        // Ensure all required fragments are ready
        if (dataPengawasFragment?.view == null ||
            jenisdanTahapanFragment?.view == null ||
            kegiatanPengawasanFragment?.view == null ||
            hasilPengawasanFragment?.view == null) {
            Toast.makeText(requireContext(), "Gagal mengumpulkan data. Pastikan semua fragment sudah siap.", Toast.LENGTH_LONG).show()
            return
        }

        // Gather data from fragments
        val noSurat = dataPengawasFragment.getNoSurat()
        val namaPelaksana = dataPengawasFragment.getNamaPelaksana()
        val jabatan = dataPengawasFragment.getJabatan()
        val nomorSuratPerintah = dataPengawasFragment.getNomorsuratperintah()
        val alamat = dataPengawasFragment.getAlamat()
        val bentuk = kegiatanPengawasanFragment.getBentuk()
        val tujuan = kegiatanPengawasanFragment.getTujuan()

        // Create request body with the data
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("noSurat", noSurat)
            .add("namaPelaksana", namaPelaksana)
            .add("jabatan", jabatan)
            .add("nomorSuratPerintah", nomorSuratPerintah)
            .add("alamat", alamat)
            .add("bentuk", bentuk)
            .add("tujuan", tujuan)
            .build()

        // Create request to the server
        val request = Request.Builder()
            .url("https://kaftapus.web.id/api/save_pengawas.php")  // Replace with your server's URL
            .post(formBody)
            .build()

        // Execute the request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Data berhasil diupload!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Gagal upload data: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error saat upload data: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
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
                val generator = DocumentGenerator(requireContext())
                val isSaved = generator.generateAndSaveDocument(data, signatureFilePath, signatureBitmap, selectedImages, signatureDate, signaturePosition, signatureName)
                withContext(Dispatchers.Main) {
                    val message = if (isSaved) "File berhasil disimpan!" else "Gagal menyimpan file."
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Gagal mengumpulkan data dari fragment.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Adapter for ViewPager2
    private inner class SectionsPagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        private val fragmentList = listOf(
            FragmentDataPengawas(),
            FragmentJenisdanTahapan(),
            FragmentKegiatanPengawasan(),
            FragmentHasilPengawasan(),
            FragmentDugaanPelanggaran(),
            FragmentPotensiSengketa(),
            FragmentSignature(),
            FragmentLampiranGambar()
        )

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

        fun getFragmentAtPosition(position: Int): Fragment? {
            return if (position in 0 until fragmentList.size) fragmentList[position] else null
        }
    }

    // Method to check storage permission and generate document
    private fun checkStoragePermissionAndGenerateDocument() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                collectDataAndExportDocument()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivity(intent)
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                collectDataAndExportDocument()
            }
        }
    }

    // Fungsi untuk mengonversi Uri ke Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
