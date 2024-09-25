package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class FragmentProfil : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var namaTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi PreferencesHelper
        preferencesHelper = PreferencesHelper(requireContext())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        namaTextView = view.findViewById(R.id.namaprofile)
        emailTextView = view.findViewById(R.id.emailprofile)

        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val email = currentUser?.email

        if (uid != null) {
            fetchUserProfile(uid)
        }

        if (email != null) {
            emailTextView.text = email
        }

        // Mencari button logout
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun fetchUserProfile(uid: String) {
        val url = "https://kaftapus.web.id/api/get_fullname.php?uid=$uid"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")
                    if (status == "success") {
                        val fullname = jsonObject.getString("fullname")
                        val email = jsonObject.getString("email")  // Handle email not found case
                        view?.findViewById<TextView>(R.id.namaprofile)?.text = fullname
                        view?.findViewById<TextView>(R.id.emailprofile)?.text = email
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
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


    private fun logout() {
        // Hapus status login dari SharedPreferences
        preferencesHelper.logout()

        // Logout dari Firebase
        auth.signOut()

        // Logout dari Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Kembali ke halaman login atau activity yang diinginkan setelah logout
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish() // Tutup activity saat ini
            } else {
                Toast.makeText(requireContext(), "Failed to log out from Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
