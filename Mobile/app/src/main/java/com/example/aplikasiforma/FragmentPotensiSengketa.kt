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

class FragmentPotensiSengketa : Fragment() {

    private lateinit var etPotensiSengketa: EditText
    private lateinit var signaturePad: SignaturePad
    private lateinit var clearButton: Button
    private var signatureBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_potensi_sengketa, container, false)

        etPotensiSengketa = view.findViewById(R.id.etPotensiSengketa)
        signaturePad = view.findViewById(R.id.signature_pad)
        val saveButton: Button = view.findViewById(R.id.btnSave)
        clearButton = view.findViewById(R.id.btnClear)

        clearButton.setOnClickListener {
            signaturePad.clear()
        }

        saveButton.setOnClickListener {
            saveSignature()
        }

        return view
    }

    private fun saveSignature(){
        if (!signaturePad.isEmpty){
            signatureBitmap = signaturePad.signatureBitmap
            Toast.makeText(context, "Tanda tangan berhasil disimpan!", Toast.LENGTH_SHORT).show()
        } else {
            signatureBitmap = null
            Toast.makeText(context, "Tanda tangan kosong, tidak ada yang disimpan.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getPotensiSengketa(): String {
        return if (this::etPotensiSengketa.isInitialized) {
            etPotensiSengketa.text.toString()
        } else {
            ""
        }
    }

    fun getSignatureBitmap(): Bitmap? {
        return if (signaturePad.isEmpty) null else signaturePad.signatureBitmap
    }
}
