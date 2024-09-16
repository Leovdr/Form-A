package com.example.aplikasiforma

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentLampiranGambar : Fragment() {

    private lateinit var selectImagesButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var imageUris: MutableList<Uri> = mutableListOf()  // Initialize list of URIs
    private lateinit var pickImagesLauncher: ActivityResultLauncher<Intent>
    private val selectedImageUris = mutableListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lampiran_gambar, container, false)

        selectImagesButton = view.findViewById(R.id.btnSelectImages)
        recyclerView = view.findViewById(R.id.recyclerView)

        setupRecyclerView()

        // Setup ActivityResultLauncher
        pickImagesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val clipData = result.data!!.clipData
                if (clipData != null) {  // Multiple images selected
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        imageUris.add(imageUri)
                    }
                } else {  // Single image selected
                    result.data?.data?.let { imageUri ->
                        imageUris.add(imageUri)
                    }
                }
                imageAdapter.notifyDataSetChanged()
                Toast.makeText(context, "${imageUris.size} gambar dipilih", Toast.LENGTH_SHORT).show()
            }
        }

        selectImagesButton.setOnClickListener {
            selectImages()
        }

        return view
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(requireContext(), imageUris)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = imageAdapter
    }

    private fun selectImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickImagesLauncher.launch(intent)
    }

    // Method to get selected images URIs
    fun getSelectedImages(): List<Uri> {
        return imageUris.toList()  // Return a copy of the list
    }

    fun addImageUri(uri: Uri) {
        selectedImageUris.add(uri)
    }

    fun getSelectedImageUris(): List<Uri> {
        return selectedImageUris
    }

}
