package com.example.aplikasiforma

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _documentData = MutableLiveData<DocumentData>()
    val documentData: LiveData<DocumentData> = _documentData

    fun updateDocumentData(data: DocumentData) {
        _documentData.value = data
    }
}
