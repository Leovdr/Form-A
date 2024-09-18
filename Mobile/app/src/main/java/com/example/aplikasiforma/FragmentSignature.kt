package com.example.aplikasiforma

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FragmentSignature : Fragment() {

    private lateinit var signaturePad: SignaturePad
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button
    private lateinit var dateEditText: EditText
    private lateinit var positionEditText: EditText
    private lateinit var nameEditText: EditText
    private var signatureBitmap: Bitmap? = null
    private var signatureFilePath: String? = null

    // Mengakses SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Flag untuk mencegah loop saat memulihkan data
    private var isUpdating = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signature, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signaturePad = view.findViewById(R.id.signature_pad)
        saveButton = view.findViewById(R.id.btnSaveSignature)
        clearButton = view.findViewById(R.id.btnClearSignature)
        dateEditText = view.findViewById(R.id.etTanggalttd)
        positionEditText = view.findViewById(R.id.etJabatanttd)
        nameEditText = view.findViewById(R.id.etNamattd)

        // Memulihkan data dari SharedViewModel
        restoreDataFromViewModel()

        // Setup tombol clear
        clearButton.setOnClickListener {
            signaturePad.clear()
            signatureBitmap = null
            signatureFilePath = null
        }

        // Setup tombol save
        saveButton.setOnClickListener {
            saveSignature()
        }

        // Menambahkan TextWatcher untuk memperbarui data di SharedViewModel ketika teks berubah
        dateEditText.addTextChangedListener {
            if (!isUpdating) {
                sharedViewModel.signatureDate.value = it.toString()
            }
        }

        positionEditText.addTextChangedListener {
            if (!isUpdating) {
                sharedViewModel.signaturePosition.value = it.toString()
            }
        }

        nameEditText.addTextChangedListener {
            if (!isUpdating) {
                sharedViewModel.signatureName.value = it.toString()
            }
        }
    }

    // Memulihkan data dari ViewModel saat fragment dibuka kembali
    private fun restoreDataFromViewModel() {
        isUpdating = true // Mencegah loop

        sharedViewModel.signatureDate.observe(viewLifecycleOwner) { date ->
            if (date != dateEditText.text.toString()) {
                dateEditText.setText(date)
            }
        }

        sharedViewModel.signaturePosition.observe(viewLifecycleOwner) { position ->
            if (position != positionEditText.text.toString()) {
                positionEditText.setText(position)
            }
        }

        sharedViewModel.signatureName.observe(viewLifecycleOwner) { name ->
            if (name != nameEditText.text.toString()) {
                nameEditText.setText(name)
            }
        }

        sharedViewModel.signatureBitmap.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                signaturePad.setSignatureBitmap(bitmap)
                signatureBitmap = bitmap
            }
        }

        isUpdating = false
    }

    // Menyimpan tanda tangan ke file
    private fun saveSignature() {
        if (!signaturePad.isEmpty) {
            signatureBitmap = signaturePad.signatureBitmap
            signatureFilePath = saveBitmapToFile(signatureBitmap)
            sharedViewModel.signatureBitmap.value = signatureBitmap

            context?.let {
                Toast.makeText(it, "Tanda tangan berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        } else {
            signatureBitmap = null
            context?.let {
                Toast.makeText(it, "Tanda tangan kosong, tidak ada yang disimpan.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Menyimpan Bitmap ke file
    private fun saveBitmapToFile(bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        val file = File(requireContext().cacheDir, "signature.png")
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Fungsi untuk mendapatkan input dengan cek null
    fun getSignatureDate(): String {
        return dateEditText.text.toString()
    }

    fun getSignaturePosition(): String {
        return positionEditText.text.toString()
    }

    fun getSignatureName(): String {
        return nameEditText.text.toString()
    }

    fun getSignatureFilePath(): String? {
        return signatureFilePath
    }

    // Fungsi untuk mengambil Bitmap tanda tangan
    fun getSignatureBitmap(): Bitmap? {
        return if (signaturePad.isEmpty) {
            context?.let {
                Toast.makeText(it, "Tanda tangan kosong.", Toast.LENGTH_SHORT).show()
            }
            null
        } else {
            signaturePad.signatureBitmap
        }
    }
}
