package com.example.aplikasiforma

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    // Individual LiveData for form fields
    val noSurat = MutableLiveData<String>()
    val namaPelaksana = MutableLiveData<String>()
    val jabatan = MutableLiveData<String>()
    val nomorSuratperintah = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val bentuk = MutableLiveData<String>()
    val tujuan = MutableLiveData<String>()
    val sasaran = MutableLiveData<String>()
    val waktuTempat = MutableLiveData<String>()
    val hasilPengawasan = MutableLiveData<String>()
    val jenisPemilihan = MutableLiveData<String>()
    val tahapanPemilihan = MutableLiveData<String>()

    // Dugaan Pelanggaran data as a Map
    private val _dugaanPelanggaranData = MutableLiveData<Map<String, String>>()
    val dugaanPelanggaranData: LiveData<Map<String, String>> = _dugaanPelanggaranData

    // Potensi Sengketa data as a Map
    private val _potensiSengketaData = MutableLiveData<Map<String, String>>()
    val potensiSengketaData: LiveData<Map<String, String>> = _potensiSengketaData

    // Signature-related data
    val signatureDate = MutableLiveData<String>()
    val signaturePosition = MutableLiveData<String>()
    val signatureName = MutableLiveData<String>()
    val signatureBitmap = MutableLiveData<Bitmap?>()

    // LiveData for the whole document data (optional if you need to hold all data in one object)
    private val _documentData = MutableLiveData<DocumentData>()
    val documentData: LiveData<DocumentData> = _documentData

    // Function to manually update documentData
    fun updateDocumentData(data: DocumentData) {
        _documentData.value = data
    }

    // Function to update dugaan pelanggaran data
    fun updateDugaanPelanggaran(field: String, value: String) {
        val currentData = _dugaanPelanggaranData.value?.toMutableMap() ?: mutableMapOf()
        currentData[field] = value
        _dugaanPelanggaranData.value = currentData
    }

    // Function to clear dugaan pelanggaran data (optional)
    fun clearDugaanPelanggaran() {
        _dugaanPelanggaranData.value = emptyMap()
    }

    // Function to update potensi sengketa data
    fun updatePotensiSengketa(field: String, value: String) {
        val currentData = _potensiSengketaData.value?.toMutableMap() ?: mutableMapOf()
        currentData[field] = value
        _potensiSengketaData.value = currentData
    }

    // Function to clear potensi sengketa data (optional)
    fun clearPotensiSengketa() {
        _potensiSengketaData.value = emptyMap()
    }
}
