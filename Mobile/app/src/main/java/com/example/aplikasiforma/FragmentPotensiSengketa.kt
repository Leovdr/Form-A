package com.example.aplikasiforma

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentPotensiSengketa : Fragment() {

    private lateinit var etPesertaPemilihan: EditText
    private lateinit var etTempatKejadian: EditText
    private lateinit var etWaktuKejadian: EditText
    private lateinit var etBentukObjek: EditText
    private lateinit var etIdentitasObjek: EditText
    private lateinit var etHariTanggal: EditText
    private lateinit var etKerugianLangsung: EditText
    private lateinit var etPotensiSengketa: EditText

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

        return view
    }

    fun getPotensiSengketa(): Map<String, String> {
        if (!isAdded) {
            Log.e("FragmentPotensiSengketa", "Fragment is not attached to the activity.")
            return emptyMap()
        }

        if (!this::etPesertaPemilihan.isInitialized) {
            Log.e("FragmentPotensiSengketa", "EditTexts belum diinisialisasi.")
            return emptyMap()
        }

        val dataMap = mapOf(
            "Peserta Pemilihan" to etPesertaPemilihan.text.toString(),
            "Tempat Kejadian" to etTempatKejadian.text.toString(),
            "Waktu Kejadian" to etWaktuKejadian.text.toString(),
            "Bentuk Objek Sengketa" to etBentukObjek.text.toString(),
            "Identitas Objek Sengketa" to etIdentitasObjek.text.toString(),
            "Hari/Tanggal Dikeluarkan" to etHariTanggal.text.toString(),
            "Kerugian Langsung" to etKerugianLangsung.text.toString(),
            "Uraian Singkat Potensi Sengketa" to etPotensiSengketa.text.toString()
        )
        Log.d("FragmentPotensiSengketa", "Data Potensi Sengketa: $dataMap")
        return dataMap
    }

}
