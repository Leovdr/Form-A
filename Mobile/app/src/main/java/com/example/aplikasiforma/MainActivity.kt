package com.example.aplikasiforma

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Initialize ViewPager and TabLayout
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        // Set up ViewPager with a FragmentStateAdapter
        viewPager.adapter = SectionsPagerAdapter(this)

        // Link TabLayout and ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Data Pengawas"
                1 -> "Kegiatan Pengawasan"
                2 -> "Hasil Pengawasan"
                3 -> "Dugaan Pelanggaran"
                4 -> "Potensi Sengketa"
                else -> null
            }
        }.attach()

        // Set up the Export button listener
        findViewById<Button>(R.id.btnExport).setOnClickListener {
            collectDataFromFragments()
            generateDocument()
        }

        // Observe data changes from ViewModel
        sharedViewModel.documentData.observe(this, Observer { data ->
            // Perform any action when data is updated, if needed
        })
    }

    private fun collectDataFromFragments() {
        val adapter = viewPager.adapter as SectionsPagerAdapter
        val dataPengawasFragment = adapter.getFragment(0) as FragmentDataPengawas
        val kegiatanPengawasanFragment = adapter.getFragment(1) as FragmentKegiatanPengawasan
        val hasilPengawasanFragment = adapter.getFragment(2) as FragmentHasilPengawasan
        val dugaanPelanggaranFragment = adapter.getFragment(3) as FragmentDugaanPelanggaran
        val potensiSengketaFragment = adapter.getFragment(4) as FragmentPotensiSengketa

        val data = DocumentData(
            noSurat = dataPengawasFragment.getNoSurat(),
            namaPengawas = dataPengawasFragment.getNamaPengawas(),
            jabatan = dataPengawasFragment.getJabatan(),
            nomorSurat = dataPengawasFragment.getNomorSurat(),
            alamat = dataPengawasFragment.getAlamat(),
            tahapan = dataPengawasFragment.getTahapan(),
            kegiatan = kegiatanPengawasanFragment.getKegiatan(),
            tujuan = kegiatanPengawasanFragment.getTujuan(),
            sasaran = kegiatanPengawasanFragment.getSasaran(),
            waktuTempat = kegiatanPengawasanFragment.getWaktuTempat(),
            hasilPengawasan = hasilPengawasanFragment.getHasilPengawasan(),
            dugaanPelanggaran = dugaanPelanggaranFragment.getDugaanPelanggaran(),
            potensiSengketa = potensiSengketaFragment.getPotensiSengketa()
        )

        sharedViewModel.updateDocumentData(data)
    }

    private fun generateDocument() {
        val data = sharedViewModel.documentData.value
        if (data != null) {
            val generator = DocumentGenerator(this)
            val isSaved = generator.generateAndSaveDocument(data)
            if (isSaved) {
                Toast.makeText(this, "File saved successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to save file.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Failed to collect data from fragments.", Toast.LENGTH_LONG).show()
        }
    }

    private inner class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        private val fragments = arrayOf(
            FragmentDataPengawas(),
            FragmentKegiatanPengawasan(),
            FragmentHasilPengawasan(),
            FragmentDugaanPelanggaran(),
            FragmentPotensiSengketa()
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]

        fun getFragment(position: Int): Fragment = fragments[position]
    }
}
