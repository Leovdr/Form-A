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

class FragmentHasilPengawasan : Fragment() {

    private lateinit var etHasilPengawasan: EditText

    // Mengakses SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isUpdating = false  // Flag untuk mencegah loop update

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hasil_pengawasan, container, false)

        etHasilPengawasan = view.findViewById(R.id.etHasilPengawasan)

        // Memulihkan data dari ViewModel ketika fragment dibuka kembali
        sharedViewModel.hasilPengawasan.observe(viewLifecycleOwner) { hasilPengawasan ->
            if (etHasilPengawasan.text.toString() != hasilPengawasan) {
                isUpdating = true
                etHasilPengawasan.setText(hasilPengawasan)
                isUpdating = false
            }
        }

        // Menambahkan TextWatcher untuk memperbarui data di ViewModel ketika teks berubah
        etHasilPengawasan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.hasilPengawasan.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    // Fungsi untuk mendapatkan nilai hasil pengawasan dari EditText, bila dibutuhkan
    fun getHasilPengawasan(): String {
        return etHasilPengawasan.text.toString()
    }
}
