package com.example.aplikasiforma

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signature, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signaturePad = view.findViewById(R.id.signature_pad)
        saveButton = view.findViewById(R.id.btnSaveSignature)
        clearButton = view.findViewById(R.id.btnClearSignature)
        dateEditText = view.findViewById(R.id.etTanggalttd)
        positionEditText = view.findViewById(R.id.etJabatanttd)
        nameEditText = view.findViewById(R.id.etNamattd)

        clearButton.setOnClickListener {
            signaturePad.clear()
        }

        saveButton.setOnClickListener {
            saveSignature()
        }
    }

    private fun saveSignature() {
        if (!signaturePad.isEmpty) {
            signatureBitmap = signaturePad.signatureBitmap
            signatureFilePath = saveBitmapToFile(signatureBitmap)
            Toast.makeText(context, "Tanda tangan berhasil disimpan!", Toast.LENGTH_SHORT).show()
        } else {
            signatureBitmap = null
            Toast.makeText(
                context,
                "Tanda tangan kosong, tidak ada yang disimpan.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        val file = File(requireContext().cacheDir, "signature.png")
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getSignatureBitmap(): Bitmap? {
        if (!this::signaturePad.isInitialized) {
            Toast.makeText(context, "Tanda tangan belum siap.", Toast.LENGTH_SHORT).show()
            return null
        }
        return if (signaturePad.isEmpty) null else signaturePad.signatureBitmap
    }

    // Tambahkan fungsi get untuk input
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
}
