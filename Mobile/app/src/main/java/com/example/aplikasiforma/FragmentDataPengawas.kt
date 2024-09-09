package com.example.aplikasiforma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentDataPengawas : Fragment() {

    private lateinit var etNosurat: EditText
    private lateinit var etNamapelaksana: EditText
    private lateinit var etJabatan: EditText
    private lateinit var etNomorsuratperintah: EditText
    private lateinit var etAlamat: EditText

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

        return view
    }

    fun getNoSurat(): String {
        return if (this::etNosurat.isInitialized){
            etNosurat.text.toString()
        }else{
            ""
        }
    }
    fun getNamaPelaksana(): String = etNamapelaksana.text.toString()
    fun getJabatan(): String = etJabatan.text.toString()
    fun getNomorsuratperintah(): String = etNomorsuratperintah.text.toString()
    fun getAlamat(): String = etAlamat.text.toString()
}
