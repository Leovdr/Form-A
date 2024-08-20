package com.example.aplikasiforma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentDataPengawas : Fragment() {

    private lateinit var etNosurat: EditText
    private lateinit var etNamaPengawas: EditText
    private lateinit var etJabatan: EditText
    private lateinit var etNomorSurat: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etTahapan: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data_pengawas, container, false)

        etNosurat = view.findViewById(R.id.etNosurat)
        etNamaPengawas = view.findViewById(R.id.etNamaPengawas)
        etJabatan = view.findViewById(R.id.etJabatan)
        etNomorSurat = view.findViewById(R.id.etNomorSurat)
        etAlamat = view.findViewById(R.id.etAlamat)
        etTahapan = view.findViewById(R.id.etTahapan)

        return view
    }

    fun getNoSurat(): String {
        return if (this::etNosurat.isInitialized){
            etNosurat.text.toString()
        }else{
            ""
        }
    }
    fun getNamaPengawas(): String = etNamaPengawas.text.toString()
    fun getJabatan(): String = etJabatan.text.toString()
    fun getNomorSurat(): String = etNomorSurat.text.toString()
    fun getAlamat(): String = etAlamat.text.toString()
    fun getTahapan(): String = etTahapan.text.toString()
}
