package com.example.aplikasiforma

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FragmentPotensiSengketa : Fragment() {

    private lateinit var etPesertaPemilihan: EditText
    private lateinit var etTempatKejadian: EditText
    private lateinit var etWaktuKejadian: EditText
    private lateinit var etBentukObjek: EditText
    private lateinit var etIdentitasObjek: EditText
    private lateinit var etHariTanggal: EditText
    private lateinit var etKerugianLangsung: EditText
    private lateinit var etPotensiSengketa: EditText

    // Mengakses SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Flag untuk mencegah loop update
    private var isUpdating = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_potensi_sengketa, container, false)

        // Inisialisasi EditText berdasarkan ID di XML
        etPesertaPemilihan = view.findViewById(R.id.etPesertaPemilihan)
        etTempatKejadian = view.findViewById(R.id.etTempatKejadian)
        etWaktuKejadian = view.findViewById(R.id.etWaktuKejadian)
        etBentukObjek = view.findViewById(R.id.etBentukObjek)
        etIdentitasObjek = view.findViewById(R.id.etIdentitasObjek)
        etHariTanggal = view.findViewById(R.id.etHariTanggal)
        etKerugianLangsung = view.findViewById(R.id.etKerugianLangsung)
        etPotensiSengketa = view.findViewById(R.id.etPotensiSengketa)

        // Memulihkan data dari SharedViewModel saat fragment dibuka kembali
        restoreDataFromViewModel()

        // Menambahkan TextWatcher untuk setiap EditText agar memperbarui data di SharedViewModel
        setupTextWatchers()

        return view
    }

    private fun setupTextWatchers() {
        etPesertaPemilihan.addTextChangedListener(createTextWatcher("Peserta Pemilihan"))
        etTempatKejadian.addTextChangedListener(createTextWatcher("Tempat Kejadian"))
        etWaktuKejadian.addTextChangedListener(createTextWatcher("Waktu Kejadian"))
        etBentukObjek.addTextChangedListener(createTextWatcher("Bentuk Objek Sengketa"))
        etIdentitasObjek.addTextChangedListener(createTextWatcher("Identitas Objek Sengketa"))
        etHariTanggal.addTextChangedListener(createTextWatcher("Hari/Tanggal Dikeluarkan"))
        etKerugianLangsung.addTextChangedListener(createTextWatcher("Kerugian Langsung"))
        etPotensiSengketa.addTextChangedListener(createTextWatcher("Uraian Singkat Potensi Sengketa"))
    }

    private fun createTextWatcher(field: String): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    // Perbarui data di ViewModel hanya jika tidak sedang update dari ViewModel
                    sharedViewModel.updatePotensiSengketa(field, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    // Memulihkan data dari ViewModel saat fragment dibuka
    private fun restoreDataFromViewModel() {
        sharedViewModel.potensiSengketaData.observe(viewLifecycleOwner) { data ->
            isUpdating = true  // Set flag untuk mencegah loop
            data?.let {
                setTextIfDifferent(etPesertaPemilihan, it["Peserta Pemilihan"])
                setTextIfDifferent(etTempatKejadian, it["Tempat Kejadian"])
                setTextIfDifferent(etWaktuKejadian, it["Waktu Kejadian"])
                setTextIfDifferent(etBentukObjek, it["Bentuk Objek Sengketa"])
                setTextIfDifferent(etIdentitasObjek, it["Identitas Objek Sengketa"])
                setTextIfDifferent(etHariTanggal, it["Hari/Tanggal Dikeluarkan"])
                setTextIfDifferent(etKerugianLangsung, it["Kerugian Langsung"])
                setTextIfDifferent(etPotensiSengketa, it["Uraian Singkat Potensi Sengketa"])
            }
            isUpdating = false  // Reset flag setelah selesai update
        }
    }

    // Fungsi untuk menghindari setText jika data tidak berubah
    private fun setTextIfDifferent(editText: EditText, newText: String?) {
        if (editText.text.toString() != newText && newText != null) {
            editText.setText(newText)
        }
    }

    // Mengambil semua data input dari fragment
    fun getPotensiSengketa(): Map<String, String> {
        return mapOf(
            "Peserta Pemilihan" to etPesertaPemilihan.text.toString(),
            "Tempat Kejadian" to etTempatKejadian.text.toString(),
            "Waktu Kejadian" to etWaktuKejadian.text.toString(),
            "Bentuk Objek Sengketa" to etBentukObjek.text.toString(),
            "Identitas Objek Sengketa" to etIdentitasObjek.text.toString(),
            "Hari/Tanggal Dikeluarkan" to etHariTanggal.text.toString(),
            "Kerugian Langsung" to etKerugianLangsung.text.toString(),
            "Uraian Singkat Potensi Sengketa" to etPotensiSengketa.text.toString()
        )
    }
}
