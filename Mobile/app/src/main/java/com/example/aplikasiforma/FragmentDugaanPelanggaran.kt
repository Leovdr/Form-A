package com.example.aplikasiforma

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.aplikasiforma.databinding.FragmentDugaanPelanggaranBinding

class FragmentDugaanPelanggaran : Fragment(R.layout.fragment_dugaan_pelanggaran) {

    private var _binding: FragmentDugaanPelanggaranBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDugaanPelanggaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set listener untuk setiap checkbox untuk mengatur visibilitas layout terkait
        setCheckboxListener(binding.checkboxPeristiwa, binding.layoutPeristiwa)
        setCheckboxListener(binding.checkboxTempatkejadian, binding.layoutTempatkejadian)
        setCheckboxListener(binding.checkboxWaktukejadian, binding.layoutWaktukejadian)
        setCheckboxListener(binding.checkboxPelaku, binding.layoutPelaku)
        setCheckboxListener(binding.checkboxAlamat, binding.layoutAlamat)
        setCheckboxListener(binding.checkboxNama1, binding.layoutNama1)
        setCheckboxListener(binding.checkboxAlamatsaksi1, binding.layoutAlamatsaksi1)
        setCheckboxListener(binding.checkboxNama2, binding.layoutNama2)
        setCheckboxListener(binding.checkboxAlamatsaksi2, binding.layoutAlamatsaksi2)
        setCheckboxListener(binding.checkboxAlatbukti, binding.layoutAlatbukti)
        setCheckboxListener(binding.checkboxBarangbukti, binding.layoutBarangbukti)
        setCheckboxListener(binding.checkboxUraian, binding.layoutUraian)
        setCheckboxListener(binding.checkboxFakta, binding.layoutFakta)
        setCheckboxListener(binding.checkboxAnalisa, binding.layoutAnalisa)
    }

    private fun setCheckboxListener(checkbox: CheckBox, layout: LinearLayout) {
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            layout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    fun getDugaanPelanggaran(): Map<String, String> {
        if (_binding == null) {
            Log.e("FragmentDugaanPelanggaran", "Binding belum siap.")
            return emptyMap() // Kembalikan map kosong sebagai penanganan error
        }

        // Ambil data dari input pengguna di fragment
        return mapOf(
            "Peristiwa" to getInfo(binding.checkboxPeristiwa, binding.etInformasiPeristiwa),
            "Tempat Kejadian" to getInfo(binding.checkboxTempatkejadian, binding.etInformasiTempatkejadian),
            "Waktu Kejadian" to getInfo(binding.checkboxWaktukejadian, binding.etInformasiWaktukejadian),
            "Pelaku" to getInfo(binding.checkboxPelaku, binding.etInformasiPelaku),
            "Alamat" to getInfo(binding.checkboxAlamat, binding.etInformasiAlamat),
            "Nama Saksi" to getInfo(binding.checkboxNama1, binding.etInformasiNama1),
            "Alamat Saksi" to getInfo(binding.checkboxAlamatsaksi1, binding.etInformasiAlamatsaksi1),
            "Nama Saksi" to getInfo(binding.checkboxNama2, binding.etInformasiNama2),
            "Alamat Saksi" to getInfo(binding.checkboxAlamatsaksi2, binding.etInformasiAlamatsaksi2),
            "Alat Bukti" to getInfo(binding.checkboxAlatbukti, binding.etInformasiAlatbukti),
            "Barang Bukti" to getInfo(binding.checkboxBarangbukti, binding.etInformasiBarangbukti),
            "Uraian Dugaan Pelanggaran" to getInfo(binding.checkboxUraian, binding.etInformasiUraian),
            "Fakta dan Keterangan" to getInfo(binding.checkboxFakta, binding.etInformasiFakta),
            "Analisa" to getInfo(binding.checkboxAnalisa, binding.etInformasiAnalisa)
        )
    }

    private fun getInfo(checkbox: CheckBox, editText: EditText): String {
        return if (checkbox.isChecked) editText.text.toString() else "Nihil"
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
