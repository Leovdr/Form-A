package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class FragmentBeranda : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // Find the ImageView by its ID
        val imgPanduan = view.findViewById<ImageView>(R.id.img_panduan)
        val imgTentang = view.findViewById<ImageView>(R.id.img_tentang)
        val imgPrivasi = view.findViewById<ImageView>(R.id.img_privasi)

        // Set an OnClickListener for the ImageView to start the PanduanPengisian activity
        imgPanduan.setOnClickListener {
            val intent = Intent(requireContext(), PanduanPengisian::class.java)
            startActivity(intent)
        }

        imgTentang.setOnClickListener {
            val intent = Intent(requireContext(), TentangAplikasi::class.java)
            startActivity(intent)
        }

        imgPrivasi.setOnClickListener {
            val intent = Intent(requireContext(), KebijakanPrivasi::class.java)
            startActivity(intent)
        }



        return view
    }
}
