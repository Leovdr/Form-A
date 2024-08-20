package com.example.aplikasiforma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentPotensiSengketa : Fragment() {

    private lateinit var etPotensiSengketa: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_potensi_sengketa, container, false)

        etPotensiSengketa = view.findViewById(R.id.etPotensiSengketa)

        return view
    }

    fun getPotensiSengketa(): String {
        return if (this::etPotensiSengketa.isInitialized){
            etPotensiSengketa.text.toString()
        }else{
            ""
        }
    }
}
