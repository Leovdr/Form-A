package com.example.aplikasiforma

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("data_pengawas_pref", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val UID = "uid"
        private const val NAMA_PELAKSANA = "nama_pelaksana"
        private const val JABATAN = "jabatan"
        private const val NOMOR_SURAT = "nomor_surat"
        private const val NOMOR_SURATPERINTAH = "nomor_suratperintah"
        private const val ALAMAT = "alamat"
        private const val BENTUK_PENGAWASAN = "bentuk_pengawasan"
        private const val TUJUAN_PENGAWASAN = "tujuan_pengawasan"
        private const val SASARAN_PENGAWASAN = "sasaran_pengawasan"
        private const val WAKTU_TEMPAT_PENGAWASAN = "waktu_tempat_pengawasan"
        private const val URAIAN_SINGKAT = "uraian_singkat"
    }

    // Fungsi untuk menyimpan UID dan status login
    fun saveUid(uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString(UID, uid)
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.apply()
    }

    // Fungsi untuk mendapatkan UID
    fun getUid(): String? {
        return sharedPreferences.getString(UID, null)
    }

    // Fungsi untuk memeriksa apakah pengguna sudah login
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    // Fungsi untuk logout dan menghapus status login
    fun logout() {
        val editor = sharedPreferences.edit()
        editor.remove(IS_LOGGED_IN)  // Menghapus status login
        editor.remove(UID)           // Menghapus UID
        editor.apply()
    }

    // Fungsi untuk menyimpan data pengawas
    fun saveDataPengawas(namaPelaksana: String, jabatan: String, nomorSuratPerintah: String, alamat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(NAMA_PELAKSANA, namaPelaksana)
        editor.putString(JABATAN, jabatan)
        editor.putString(NOMOR_SURATPERINTAH, nomorSuratPerintah)
        editor.putString(ALAMAT, alamat)
        editor.apply()
    }

    // Fungsi untuk mendapatkan Nama Pelaksana
    fun getNamaPelaksana(): String? {
        return sharedPreferences.getString(NAMA_PELAKSANA, null)
    }

    // Fungsi untuk mendapatkan Jabatan
    fun getJabatan(): String? {
        return sharedPreferences.getString(JABATAN, null)
    }

    // Fungsi untuk mendapatkan Nomor Surat Perintah
    fun getNomorSuratPerintah(): String? {
        return sharedPreferences.getString(NOMOR_SURATPERINTAH, null)
    }

    // Fungsi untuk mendapatkan Alamat
    fun getAlamat(): String? {
        return sharedPreferences.getString(ALAMAT, null)
    }

    // Fungsi untuk menyimpan Nomor Surat dari ActivityNomorSurat
    fun saveNomorSurat(nomorSurat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(NOMOR_SURAT, nomorSurat)
        editor.apply()
    }

    // Fungsi untuk mendapatkan Nomor Surat dari ActivityNomorSurat
    fun getNomorSurat(): String? {
        return sharedPreferences.getString(NOMOR_SURAT, null)
    }

    fun saveJenisTahapan(jenisPemilihan: String, tahapanPemilihan: String) {
        val editor = sharedPreferences.edit()
        editor.putString("jenis_pemilihan", jenisPemilihan)
        editor.putString("tahapan_pemilihan", tahapanPemilihan)
        editor.apply()
    }

    fun getJenisPemilihan(): String? {
        return sharedPreferences.getString("jenis_pemilihan", "")
    }

    fun getTahapanPemilihan(): String? {
        return sharedPreferences.getString("tahapan_pemilihan", "")
    }

    fun saveKegiatanPengawasan(bentuk: String, tujuan: String, sasaran: String, waktuTempat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(BENTUK_PENGAWASAN, bentuk)
        editor.putString(TUJUAN_PENGAWASAN, tujuan)
        editor.putString(SASARAN_PENGAWASAN, sasaran)
        editor.putString(WAKTU_TEMPAT_PENGAWASAN, waktuTempat)
        editor.apply()
    }

    // Fungsi untuk mendapatkan bentuk pengawasan
    fun getBentukPengawasan(): String? {
        return sharedPreferences.getString(BENTUK_PENGAWASAN, "")
    }

    // Fungsi untuk mendapatkan tujuan pengawasan
    fun getTujuanPengawasan(): String? {
        return sharedPreferences.getString(TUJUAN_PENGAWASAN, "")
    }

    // Fungsi untuk mendapatkan sasaran pengawasan
    fun getSasaranPengawasan(): String? {
        return sharedPreferences.getString(SASARAN_PENGAWASAN, "")
    }

    // Fungsi untuk mendapatkan waktu dan tempat pengawasan
    fun getWaktuTempatPengawasan(): String? {
        return sharedPreferences.getString(WAKTU_TEMPAT_PENGAWASAN, "")
    }

    fun saveUraianSingkat(uraianSingkat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(URAIAN_SINGKAT, uraianSingkat)
        editor.apply()
    }

    // Fungsi untuk mendapatkan Uraian Singkat
    fun getUraianSingkat(): String? {
        return sharedPreferences.getString(URAIAN_SINGKAT, "")
    }
}
