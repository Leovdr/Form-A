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
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class FragmentPengisian : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val fragments = listOf(
        FragmentDataPengawas(),
        FragmentJenisdanTahapan(),
        FragmentKegiatanPengawasan(),
        FragmentHasilPengawasan(),
        FragmentDugaanPelanggaran(),
        FragmentPotensiSengketa(),
        FragmentSignature(),
        FragmentLampiranGambar()

    )

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

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        viewPager.adapter = SectionsPagerAdapter(requireActivity(), fragments)

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
        }

        sharedViewModel.documentData.observe(viewLifecycleOwner) { data ->
            // Handle data updates jika diperlukan.
        }

        return view
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

    // Method to collect data and export document
    private fun collectDataAndExportDocument() {
        // Debugging log
        if (!isFragmentReady(fragments[5])) { // FragmentPotensiSengketa berada di posisi ke-5
            Toast.makeText(requireContext(), "Fragment Potensi Sengketa belum siap, coba lagi.", Toast.LENGTH_SHORT).show()
            return
        }

        val dataPengawasFragment = fragments[0] as FragmentDataPengawas
        val jenisdanTahapanFragment = fragments[1] as FragmentJenisdanTahapan
        val kegiatanPengawasanFragment = fragments[2] as FragmentKegiatanPengawasan
        val hasilPengawasanFragment = fragments[3] as FragmentHasilPengawasan
        val dugaanPelanggaranFragment = fragments[4] as FragmentDugaanPelanggaran
        val potensiSengketaFragment = fragments[5] as FragmentPotensiSengketa
        val potensiSengketaData = potensiSengketaFragment.getPotensiSengketa()
        val signatureFragment = fragments[6] as FragmentSignature
        val lampiranGambarFragment = fragments[7] as FragmentLampiranGambar

        val selectedImageUris = lampiranGambarFragment.getSelectedImages()
        val selectedImages = selectedImageUris.mapNotNull { uriToBitmap(it) }

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
            potensiSengketa = potensiSengketaFragment.getPotensiSengketa(),
            tahapanPemilihan = jenisdanTahapanFragment.getTahapanpemilihan().orEmpty(),
            jenisPemilihan = jenisdanTahapanFragment.getJenispemilihan().orEmpty()
        )

        val signatureFilePath = signatureFragment.getSignatureFilePath()
        val signatureBitmap = signatureFragment.getSignatureBitmap()
        val signatureDate = signatureFragment.getSignatureDate().orEmpty()
        val signaturePosition = signatureFragment.getSignaturePosition().orEmpty()
        val signatureName = signatureFragment.getSignatureName().orEmpty()

        Log.d("Pengisian", "Data Potensi Sengketa: $potensiSengketaData")
        sharedViewModel.updateDocumentData(data)
        generateDocument(signatureFilePath, signatureBitmap, selectedImages, signatureDate, signaturePosition, signatureName)
    }

    private fun isFragmentReady(fragment: Fragment): Boolean {
        return fragment.isAdded && fragment.view != null
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
                    if (isSaved) {
                        Toast.makeText(requireContext(), "File berhasil disimpan!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal menyimpan file.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Gagal mengumpulkan data dari fragment.", Toast.LENGTH_LONG).show()
                }
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

    // Adapter for ViewPager2
    private inner class SectionsPagerAdapter(
        activity: FragmentActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
