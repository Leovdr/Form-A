package com.example.aplikasiforma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentDugaanPelanggaran : Fragment() {

    private lateinit var etDugaanPelanggaran: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dugaan_pelanggaran, container, false)

        etDugaanPelanggaran = view.findViewById(R.id.etDugaanPelanggaran)

        return view
    }

    fun getDugaanPelanggaran(): String {
        return if (this::etDugaanPelanggaran.isInitialized){
            etDugaanPelanggaran.text.toString()
        }else{
            ""
        }
    }
}
