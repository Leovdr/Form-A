package com.example.aplikasiforma

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aplikasiforma.pengisian.NomorSurat
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class FragmentBeranda : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var namaPenggunaTextView : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        auth = FirebaseAuth.getInstance()

        // Set click listeners untuk ImageViews
        val imgPanduan = view.findViewById<ImageView>(R.id.img_panduan)
        val imgTentang = view.findViewById<ImageView>(R.id.img_tentang)
        val imgPrivasi = view.findViewById<ImageView>(R.id.img_privasi)
        val imgPengisian = view.findViewById<ImageView>(R.id.img_pengisian)
        val imgKami = view.findViewById<ImageView>(R.id.img_kami)
        val socMed = view.findViewById<ImageView>(R.id.socmed)
        val imgRiwayat = view.findViewById<ImageView>(R.id.img_history)
        namaPenggunaTextView = view.findViewById(R.id.nama_pengguna)

        val uid = auth.currentUser?.uid
        if (uid != null) {
            fetchFullname(uid)
        }

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

        imgPengisian.setOnClickListener {
            val intent = Intent(requireContext(), NomorSurat::class.java)
            startActivity(intent)
        }

        imgKami.setOnClickListener {
            val intent = Intent(requireContext(), TentangKami::class.java)
            startActivity(intent)
        }

        imgRiwayat.setOnClickListener {
            Toast.makeText(requireContext(),"Tunggu Pengembangan Selanjutnya!", Toast.LENGTH_SHORT).show()
        }

        socMed.setOnClickListener {
            openUrl("https://linktr.ee/bawaslu_kota_batu")
        }


        return view
    }

    private fun fetchFullname(uid: String) {
        val url = "https://kaftapus.web.id/api/get_fullname.php?uid=$uid"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")
                    if (status == "success") {
                        val fullname = jsonObject.getString("fullname")
                        namaPenggunaTextView.text = fullname
                    } else {
                        Toast.makeText(requireContext(), "Fullname not found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun openUrl(link: String){

        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)

        startActivity(intent)
    }
}
