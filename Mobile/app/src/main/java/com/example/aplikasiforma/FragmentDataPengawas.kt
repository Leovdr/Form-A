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

class FragmentDataPengawas : Fragment() {

    private lateinit var etNosurat: EditText
    private lateinit var etNamapelaksana: EditText
    private lateinit var etJabatan: EditText
    private lateinit var etNomorsuratperintah: EditText
    private lateinit var etAlamat: EditText

    // Mengakses SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isUpdating = false  // Flag untuk mencegah loop update

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data_pengawas, container, false)

        etNosurat = view.findViewById(R.id.etNosurat)
        etNamapelaksana = view.findViewById(R.id.etNamaPelaksana)
        etJabatan = view.findViewById(R.id.etJabatan)
        etNomorsuratperintah = view.findViewById(R.id.etNomorsuratperintah)
        etAlamat = view.findViewById(R.id.etAlamatpelaksana)

        // Mengamati LiveData di SharedViewModel untuk memulihkan data
        sharedViewModel.noSurat.observe(viewLifecycleOwner) { nosurat ->
            nosurat?.let {
                if (etNosurat.text.toString() != it) {
                    isUpdating = true
                    etNosurat.setText(it)
                    isUpdating = false
                }
            }
        }

        sharedViewModel.namaPelaksana.observe(viewLifecycleOwner) { namaPelaksana ->
            namaPelaksana?.let {
                if (etNamapelaksana.text.toString() != it) {
                    isUpdating = true
                    etNamapelaksana.setText(it)
                    isUpdating = false
                }
            }
        }

        sharedViewModel.jabatan.observe(viewLifecycleOwner) { jabatan ->
            jabatan?.let {
                if (etJabatan.text.toString() != it) {
                    isUpdating = true
                    etJabatan.setText(it)
                    isUpdating = false
                }
            }
        }

        sharedViewModel.nomorSuratperintah.observe(viewLifecycleOwner) { nomorSuratperintah ->
            nomorSuratperintah?.let {
                if (etNomorsuratperintah.text.toString() != it) {
                    isUpdating = true
                    etNomorsuratperintah.setText(it)
                    isUpdating = false
                }
            }
        }

        sharedViewModel.alamat.observe(viewLifecycleOwner) { alamat ->
            alamat?.let {
                if (etAlamat.text.toString() != it) {
                    isUpdating = true
                    etAlamat.setText(it)
                    isUpdating = false
                }
            }
        }

        // Menambahkan TextWatcher untuk memperbarui data ke ViewModel
        etNosurat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.noSurat.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etNamapelaksana.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.namaPelaksana.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etJabatan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.jabatan.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etNomorsuratperintah.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.nomorSuratperintah.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etAlamat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    sharedViewModel.alamat.value = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    // Fungsi untuk mendapatkan nilai dari EditText, bila dibutuhkan
    fun getNoSurat(): String = etNosurat.text.toString()
    fun getNamaPelaksana(): String = etNamapelaksana.text.toString()
    fun getJabatan(): String = etJabatan.text.toString()
    fun getNomorsuratperintah(): String = etNomorsuratperintah.text.toString()
    fun getAlamat(): String = etAlamat.text.toString()
}
