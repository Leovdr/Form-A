package com.example.aplikasiforma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentHasilPengawasan : Fragment() {

    private lateinit var etHasilPengawasan: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hasil_pengawasan, container, false)

        etHasilPengawasan = view.findViewById(R.id.etHasilPengawasan)

        return view
    }

    fun getHasilPengawasan(): String {
        return if (this::etHasilPengawasan.isInitialized){
            etHasilPengawasan.text.toString()
        }else{
            ""
        }
    }
}
