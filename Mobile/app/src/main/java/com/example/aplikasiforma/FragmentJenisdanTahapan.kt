package com.example.aplikasiforma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class FragmentJenisdanTahapan : Fragment() {

    private lateinit var etJenispemilihan: EditText
    private lateinit var etTahapanpemilihan: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jenis_dan_tahapan, container, false)

        etJenispemilihan = view.findViewById(R.id.etJenispemilihan)
        etTahapanpemilihan = view.findViewById(R.id.etTahapanpemilihan)

        return view
    }

    fun getJenispemilihan(): String {
        return if (this::etJenispemilihan.isInitialized){
            etJenispemilihan.text.toString()
        }else{
            ""
        }
    }
    fun getTahapanpemilihan(): String {
        return if (this::etTahapanpemilihan.isInitialized){
            etTahapanpemilihan.text.toString()
        }else{
            ""
        }
    }
}
