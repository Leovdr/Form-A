package com.example.aplikasiforma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentKegiatanPengawasan : Fragment() {

    private lateinit var etKegiatan: EditText
    private lateinit var etTujuan: EditText
    private lateinit var etSasaran: EditText
    private lateinit var etWaktuTempat: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kegiatan_pengawasan, container, false)

        etKegiatan = view.findViewById(R.id.etKegiatan)
        etTujuan = view.findViewById(R.id.etTujuan)
        etSasaran = view.findViewById(R.id.etSasaran)
        etWaktuTempat = view.findViewById(R.id.etWaktuTempat)

        return view
    }

    fun getKegiatan(): String {
        return if (this::etKegiatan.isInitialized){
            etKegiatan.text.toString()
        }else{
            ""
        }
    }
    fun getTujuan(): String {
        return if (this::etTujuan.isInitialized){
            etTujuan.text.toString()
        }else{
            ""
        }
    }
    fun getSasaran(): String {
        return if (this::etTujuan.isInitialized){
            etSasaran.text.toString()
        }else{
            ""
        }
    }
    fun getWaktuTempat(): String {
        return if (this::etWaktuTempat.isInitialized){
            etWaktuTempat.text.toString()
        }else{
            ""
        }
    }
}
