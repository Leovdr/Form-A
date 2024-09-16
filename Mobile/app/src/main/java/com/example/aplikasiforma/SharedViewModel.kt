package com.example.aplikasiforma

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
    val dugaanPelanggaran = MutableLiveData<String>()
    val potensiSengketa = MutableLiveData<String>()
    val tahapanPemilihan = MutableLiveData<String>()
    val jenisPemilihan = MutableLiveData<String>()

    // LiveData for the whole document data (optional if you need to hold all data in one object)
    private val _documentData = MutableLiveData<DocumentData>()
    val documentData: LiveData<DocumentData> = _documentData

    // Function to manually update documentData
    fun updateDocumentData(data: DocumentData) {
        _documentData.value = data
    }
}
