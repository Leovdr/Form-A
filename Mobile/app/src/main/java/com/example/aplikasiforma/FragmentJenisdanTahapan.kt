package com.example.aplikasiforma

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FragmentJenisdanTahapan : Fragment() {

    private lateinit var etJenispemilihan: EditText
    private lateinit var etTahapanpemilihan: EditText

    // Mengakses SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isUpdating = false  // Flag untuk mencegah loop update

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jenis_dan_tahapan, container, false)

        etJenispemilihan = view.findViewById(R.id.etJenispemilihan)
        etTahapanpemilihan = view.findViewById(R.id.etTahapanpemilihan)

        // Memulihkan data dari SharedViewModel ketika fragment dibuka kembali
        sharedViewModel.jenisPemilihan.observe(viewLifecycleOwner) { jenisPemilihan ->
            if (etJenispemilihan.text.toString() != jenisPemilihan) {
                isUpdating = true
                etJenispemilihan.setText(jenisPemilihan)
                isUpdating = false
            }
        }

        sharedViewModel.tahapanPemilihan.observe(viewLifecycleOwner) { tahapanPemilihan ->
            if (etTahapanpemilihan.text.toString() != tahapanPemilihan) {
                isUpdating = true
                etTahapanpemilihan.setText(tahapanPemilihan)
                isUpdating = false
            }
        }

        // Menambahkan TextWatcher untuk memperbarui data di SharedViewModel ketika teks berubah
        etJenispemilihan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.jenisPemilihan.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etTahapanpemilihan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.tahapanPemilihan.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    // Fungsi untuk mendapatkan nilai jenis pemilihan dari EditText, bila dibutuhkan
    fun getJenispemilihan(): String = etJenispemilihan.text.toString()

    // Fungsi untuk mendapatkan nilai tahapan pemilihan dari EditText, bila dibutuhkan
    fun getTahapanpemilihan(): String = etTahapanpemilihan.text.toString()
}
