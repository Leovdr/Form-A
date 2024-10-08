package com.example.aplikasiforma

import ItemsViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.aplikasiforma.databinding.ActivityHomeBinding
import com.google.firebase.FirebaseApp

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        // Initialize the first fragment
        replaceFragment(FragmentBeranda())

        // Set up the bottom navigation listener
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mnberanda -> replaceFragment(FragmentBeranda())
                R.id.mnprofil -> replaceFragment(FragmentProfil())
                else -> {
                }
            }
            true
        }
    }

    // Method to replace fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framemenu, fragment)
        fragmentTransaction.commit()
    }
}
