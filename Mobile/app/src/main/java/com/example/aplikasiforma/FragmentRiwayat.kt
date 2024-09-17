package com.example.aplikasiforma

import CustomAdapter
import ItemsViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentRiwayat : Fragment() {

    private lateinit var mList: List<ItemsViewModel>

    // Metode newInstance untuk menerima data
    companion object {
        fun newInstance(dataList: ArrayList<ItemsViewModel>): FragmentRiwayat {
            val fragment = FragmentRiwayat()
            val args = Bundle()
            args.putParcelableArrayList("DATA_LIST", dataList)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil data dari argumen jika ada
        arguments?.let {
            mList = it.getParcelableArrayList("DATA_LIST") ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment ini
        val view = inflater.inflate(R.layout.fragment_riwayat, container, false)

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recriwayat)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = CustomAdapter(mList)

        return view
    }
}
