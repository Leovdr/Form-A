package com.example.aplikasiforma

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("data_pengawas_pref", Context.MODE_PRIVATE)

    companion object {
        private const val UID = "uid"
        private const val NAMA_PELAKSANA = "nama_pelaksana"
        private const val JABATAN = "jabatan"
        private const val NOMOR_SURAT = "nomor_surat"
        private const val NOMOR_SURATPERINTAH = "nomor_suratperintah"
        private const val ALAMAT = "alamat"
    }

    // Fungsi untuk menyimpan UID
    fun saveUid(uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString(UID, uid)
        editor.apply()
    }

    // Fungsi untuk mendapatkan UID
    fun getUid(): String? {
        return sharedPreferences.getString(UID, null)
    }

    // Fungsi lain untuk menyimpan dan mendapatkan data
    fun saveDataPengawas(namaPelaksana: String, jabatan: String, nomorSuratPerintah: String, alamat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(NAMA_PELAKSANA, namaPelaksana)
        editor.putString(JABATAN, jabatan)
        editor.putString(NOMOR_SURATPERINTAH, nomorSuratPerintah)
        editor.putString(ALAMAT, alamat)
        editor.apply()
    }

    fun getNamaPelaksana(): String? {
        return sharedPreferences.getString(NAMA_PELAKSANA, "")
    }

    fun getJabatan(): String? {
        return sharedPreferences.getString(JABATAN, "")
    }

    fun getNomorSuratPerintah(): String? {
        return sharedPreferences.getString(NOMOR_SURATPERINTAH, "")
    }

    fun getAlamat(): String? {
        return sharedPreferences.getString(ALAMAT, "")
    }

    fun saveNomorSurat(nomorSurat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(NOMOR_SURAT, nomorSurat)
        editor.apply()
    }

    fun getNomorSurat(): String? {
        return sharedPreferences.getString(NOMOR_SURAT, "")
    }
}
