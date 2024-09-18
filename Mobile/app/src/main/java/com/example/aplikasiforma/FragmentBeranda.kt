package com.example.aplikasiforma

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import android.widget.ImageView

class FragmentBeranda : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // Set click listeners untuk ImageViews
        val imgPanduan = view.findViewById<ImageView>(R.id.img_panduan)
        val imgTentang = view.findViewById<ImageView>(R.id.img_tentang)
        val imgPrivasi = view.findViewById<ImageView>(R.id.img_privasi)
        val imgSoon3 = view.findViewById<ImageView>(R.id.img_soon3)

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

        imgSoon3.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
