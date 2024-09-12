package com.example.aplikasiforma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.aplikasiforma.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(FragmentBeranda())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.mnberanda -> replaceFragment(FragmentBeranda())
                R.id.mnriwayat -> replaceFragment(FragmentRiwayat())
                R.id.mnpengisian -> replaceFragment(FragmentPengisian())
                R.id.mnprofil -> replaceFragment(FragmentProfil())
                else ->{
                }
            }
            true
        }
    }

        private fun replaceFragment(fragment: Fragment){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.framemenu,fragment)
            fragmentTransaction.commit()
        }
}