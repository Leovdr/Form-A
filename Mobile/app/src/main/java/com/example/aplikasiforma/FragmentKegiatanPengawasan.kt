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

class FragmentKegiatanPengawasan : Fragment() {

    private lateinit var etBentuk: EditText
    private lateinit var etTujuan: EditText
    private lateinit var etSasaran: EditText
    private lateinit var etWaktuTempat: EditText

    // Mengakses SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isUpdating = false  // Flag untuk mencegah loop update

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kegiatan_pengawasan, container, false)

        etBentuk = view.findViewById(R.id.etBentuk)
        etTujuan = view.findViewById(R.id.etTujuan)
        etSasaran = view.findViewById(R.id.etSasaran)
        etWaktuTempat = view.findViewById(R.id.etWaktuTempat)

        // Memulihkan data dari SharedViewModel ketika fragment dibuka kembali
        sharedViewModel.bentuk.observe(viewLifecycleOwner) { bentuk ->
            if (etBentuk.text.toString() != bentuk) {
                isUpdating = true
                etBentuk.setText(bentuk)
                isUpdating = false
            }
        }

        sharedViewModel.tujuan.observe(viewLifecycleOwner) { tujuan ->
            if (etTujuan.text.toString() != tujuan) {
                isUpdating = true
                etTujuan.setText(tujuan)
                isUpdating = false
            }
        }

        sharedViewModel.sasaran.observe(viewLifecycleOwner) { sasaran ->
            if (etSasaran.text.toString() != sasaran) {
                isUpdating = true
                etSasaran.setText(sasaran)
                isUpdating = false
            }
        }

        sharedViewModel.waktuTempat.observe(viewLifecycleOwner) { waktuTempat ->
            if (etWaktuTempat.text.toString() != waktuTempat) {
                isUpdating = true
                etWaktuTempat.setText(waktuTempat)
                isUpdating = false
            }
        }

        // Menambahkan TextWatcher untuk memperbarui data di SharedViewModel ketika teks berubah
        etBentuk.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.bentuk.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etTujuan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.tujuan.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etSasaran.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.sasaran.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etWaktuTempat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.waktuTempat.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    // Fungsi untuk mendapatkan nilai bentuk dari EditText, bila dibutuhkan
    fun getBentuk(): String = etBentuk.text.toString()

    // Fungsi untuk mendapatkan nilai tujuan dari EditText, bila dibutuhkan
    fun getTujuan(): String = etTujuan.text.toString()

    // Fungsi untuk mendapatkan nilai sasaran dari EditText, bila dibutuhkan
    fun getSasaran(): String = etSasaran.text.toString()

    // Fungsi untuk mendapatkan nilai waktu dan tempat dari EditText, bila dibutuhkan
    fun getWaktuTempat(): String = etWaktuTempat.text.toString()
}
