package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class FragmentProfil : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi PreferencesHelper
        preferencesHelper = PreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        // Mencari button logout
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        // Hapus status login dari SharedPreferences
        preferencesHelper.logout()

        // Logout dari Firebase
        auth.signOut()

        // Kembali ke halaman login atau activity yang diinginkan setelah logout
        val intent = Intent(activity, LoginActivity::class.java) // Ganti dengan Activity yang sesuai
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // Tutup activity saat ini
    }
}
