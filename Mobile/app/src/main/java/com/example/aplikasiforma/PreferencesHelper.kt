package com.example.aplikasiforma

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("data_pengawas_pref", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val UID = "uid"
        private const val FULLNAME = "fullname"  // Menambahkan konstanta fullname
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
        private const val NAMA_TTD = "nama_ttd"
        private const val JABATAN_TTD = "jabatan_ttd"
        private const val TANGGAL_TTD = "tanggal_ttd"
        private const val SIGNATURE_IMAGE = "signature_image"
        private const val IMAGES_URI_LIST = "images_uri_list"
        private const val IS_EXPORTED = "is_exported"

//        // Dugaan Pelanggaran fields
//        private const val PERISTIWA = "peristiwa"
//        private const val TEMPAT_KEJADIAN_DUGAAN = "tempat_kejadian_dugaan"
//        private const val WAKTU_KEJADIAN_DUGAAN = "waktu_kejadian_dugaan"
//        private const val PELAKU = "pelaku"
//        private const val ALAMAT_DUGAAN = "alamat_dugaan"
//        private const val PASAL_DILANGGAR = "pasal_dilanggar"
//        private const val NAMA_SAKSI1 = "nama_saksi1"
//        private const val ALAMAT_SAKSI1 = "alamat_saksi1"
//        private const val NAMA_SAKSI2 = "nama_saksi2"
//        private const val ALAMAT_SAKSI2 = "alamat_saksi2"
//        private const val BUKTI = "bukti"
//        private const val URAIAN_PELANGGARAN = "uraian_pelanggaran"
//        private const val JENIS_PELANGGARAN = "jenis_pelanggaran"
//        private const val FAKTA = "fakta"
//        private const val ANALISA = "analisa"
//        private const val TINDAK_LANJUT = "tindak_lanjut"
//
//        // Potensi Sengketa fields
//        private const val PESERTA_PEMILIHAN = "peserta_pemilihan"
//        private const val TEMPAT_KEJADIAN = "tempat_kejadian"
//        private const val WAKTU_KEJADIAN = "waktu_kejadian"
//        private const val BENTUK_OBJEK = "bentuk_objek"
//        private const val IDENTITAS_OBJEK = "identitas_objek"
//        private const val HARI_TANGGAL = "hari_tanggal"
//        private const val KERUGIAN_LANGSUNG = "kerugian_langsung"
//        private const val URAIAN_POTENSI_SENGKETA = "uraian_potensi_sengketa"
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

    // Fungsi untuk menyimpan Fullname
    fun saveFullname(fullname: String) {
        val editor = sharedPreferences.edit()
        editor.putString(FULLNAME, fullname)
        editor.apply()
    }

    // Fungsi untuk mendapatkan Fullname
    fun getFullname(): String? {
        return sharedPreferences.getString(FULLNAME, null)
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

    // Fungsi untuk menyimpan jenis dan tahapan pemilihan
    fun saveJenisTahapan(jenisPemilihan: String, tahapanPemilihan: String) {
        val editor = sharedPreferences.edit()
        editor.putString("jenis_pemilihan", jenisPemilihan)
        editor.putString("tahapan_pemilihan", tahapanPemilihan)
        editor.apply()
    }

    // Fungsi untuk mendapatkan jenis pemilihan
    fun getJenisPemilihan(): String? {
        return sharedPreferences.getString("jenis_pemilihan", "")
    }

    // Fungsi untuk mendapatkan tahapan pemilihan
    fun getTahapanPemilihan(): String? {
        return sharedPreferences.getString("tahapan_pemilihan", "")
    }

    // Fungsi untuk menyimpan data kegiatan pengawasan
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

    // Fungsi untuk menyimpan uraian singkat
    fun saveUraianSingkat(uraianSingkat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(URAIAN_SINGKAT, uraianSingkat)
        editor.apply()
    }

    // Fungsi untuk mendapatkan Uraian Singkat
    fun getUraianSingkat(): String? {
        return sharedPreferences.getString(URAIAN_SINGKAT, "")
    }

    fun saveSignatureData(tanggalTtd: String, jabatanTtd: String, namaTtd: String, signatureImage: String) {
        val editor = sharedPreferences.edit()
        editor.putString(TANGGAL_TTD, tanggalTtd)
        editor.putString(JABATAN_TTD, jabatanTtd)
        editor.putString(NAMA_TTD, namaTtd)
        editor.putString(SIGNATURE_IMAGE, signatureImage)
        editor.apply()
    }

    // Fungsi untuk menyimpan daftar URI gambar
    fun saveImageUris(imageUris: List<Uri>) {
        val editor = sharedPreferences.edit()
        val uriStrings = imageUris.map { it.toString() }.toSet()  // Mengubah URI menjadi String
        editor.putStringSet(IMAGES_URI_LIST, uriStrings)
        editor.apply()
    }

    fun getImageUris(): List<Uri> {
        val uriStrings = sharedPreferences.getStringSet(IMAGES_URI_LIST, emptySet()) ?: emptySet()
        return uriStrings.map { Uri.parse(it) }
    }


    // Fungsi untuk mendapatkan Tanggal Tanda Tangan
    fun getTanggalTtd(): String? {
        return sharedPreferences.getString(TANGGAL_TTD, null)
    }

    // Fungsi untuk mendapatkan Jabatan Tanda Tangan
    fun getJabatanTtd(): String? {
        return sharedPreferences.getString(JABATAN_TTD, null)
    }

    // Fungsi untuk mendapatkan Nama Tanda Tangan
    fun getNamaTtd(): String? {
        return sharedPreferences.getString(NAMA_TTD, null)
    }

    // Fungsi untuk mendapatkan gambar tanda tangan dalam format Base64
    fun getSignatureImage(): String? {
        return sharedPreferences.getString(SIGNATURE_IMAGE, null)
    }

    // Fungsi untuk menghapus data tanda tangan
    fun clearSignatureData() {
        val editor = sharedPreferences.edit()
        editor.remove(TANGGAL_TTD)
        editor.remove(JABATAN_TTD)
        editor.remove(NAMA_TTD)
        editor.remove(SIGNATURE_IMAGE)
        editor.apply()
    }

    fun setExported(isExported: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_EXPORTED, isExported)
        editor.apply()
    }

    // Fungsi untuk memeriksa apakah dokumen sudah diekspor
    fun isExported(): Boolean {
        return sharedPreferences.getBoolean(IS_EXPORTED, false)
    }

//    // Fungsi untuk menyimpan data Dugaan Pelanggaran
//    fun saveDugaanPelanggaran(
//        peristiwa: String,
//        tempatKejadian: String,
//        waktuKejadian: String,
//        pelaku: String,
//        alamat: String,
//        pasalDilanggar: String,
//        namaSaksi1: String,
//        alamatSaksi1: String,
//        namaSaksi2: String,
//        alamatSaksi2: String,
//        bukti: String,
//        uraianPelanggaran: String,
//        jenisPelanggaran: String,
//        fakta: String,
//        analisa: String,
//        tindakLanjut: String
//    ) {
//        val editor = sharedPreferences.edit()
//        editor.putString(PERISTIWA, peristiwa)
//        editor.putString(TEMPAT_KEJADIAN_DUGAAN, tempatKejadian)
//        editor.putString(WAKTU_KEJADIAN_DUGAAN, waktuKejadian)
//        editor.putString(PELAKU, pelaku)
//        editor.putString(ALAMAT_DUGAAN, alamat)
//        editor.putString(PASAL_DILANGGAR, pasalDilanggar)
//        editor.putString(NAMA_SAKSI1, namaSaksi1)
//        editor.putString(ALAMAT_SAKSI1, alamatSaksi1)
//        editor.putString(NAMA_SAKSI2, namaSaksi2)
//        editor.putString(ALAMAT_SAKSI2, alamatSaksi2)
//        editor.putString(BUKTI, bukti)
//        editor.putString(URAIAN_PELANGGARAN, uraianPelanggaran)
//        editor.putString(JENIS_PELANGGARAN, jenisPelanggaran)
//        editor.putString(FAKTA, fakta)
//        editor.putString(ANALISA, analisa)
//        editor.putString(TINDAK_LANJUT, tindakLanjut)
//        editor.apply()
//    }
//
//    // Fungsi untuk mendapatkan data Dugaan Pelanggaran
//    fun getDugaanPelanggaran(): Map<String, String?> {
//        return mapOf(
//            "peristiwa" to sharedPreferences.getString(PERISTIWA, null),
//            "tempat_kejadian" to sharedPreferences.getString(TEMPAT_KEJADIAN_DUGAAN, null),
//            "waktu_kejadian" to sharedPreferences.getString(WAKTU_KEJADIAN_DUGAAN, null),
//            "pelaku" to sharedPreferences.getString(PELAKU, null),
//            "alamat" to sharedPreferences.getString(ALAMAT_DUGAAN, null),
//            "pasal_dilanggar" to sharedPreferences.getString(PASAL_DILANGGAR, null),
//            "nama_saksi1" to sharedPreferences.getString(NAMA_SAKSI1, null),
//            "alamat_saksi1" to sharedPreferences.getString(ALAMAT_SAKSI1, null),
//            "nama_saksi2" to sharedPreferences.getString(NAMA_SAKSI2, null),
//            "alamat_saksi2" to sharedPreferences.getString(ALAMAT_SAKSI2, null),
//            "bukti" to sharedPreferences.getString(BUKTI, null),
//            "uraian_pelanggaran" to sharedPreferences.getString(URAIAN_PELANGGARAN, null),
//            "jenis_pelanggaran" to sharedPreferences.getString(JENIS_PELANGGARAN, null),
//            "fakta" to sharedPreferences.getString(FAKTA, null),
//            "analisa" to sharedPreferences.getString(ANALISA, null),
//            "tindak_lanjut" to sharedPreferences.getString(TINDAK_LANJUT, null)
//        )
//    }
//
//    // Fungsi untuk menyimpan data Potensi Sengketa
//    fun savePotensiSengketa(
//        pesertaPemilihan: String,
//        tempatKejadian: String,
//        waktuKejadian: String,
//        bentukObjek: String,
//        identitasObjek: String,
//        hariTanggal: String,
//        kerugianLangsung: String,
//        uraianPotensiSengketa: String
//    ) {
//        val editor = sharedPreferences.edit()
//        editor.putString(PESERTA_PEMILIHAN, pesertaPemilihan)
//        editor.putString(TEMPAT_KEJADIAN, tempatKejadian)
//        editor.putString(WAKTU_KEJADIAN, waktuKejadian)
//        editor.putString(BENTUK_OBJEK, bentukObjek)
//        editor.putString(IDENTITAS_OBJEK, identitasObjek)
//        editor.putString(HARI_TANGGAL, hariTanggal)
//        editor.putString(KERUGIAN_LANGSUNG, kerugianLangsung)
//        editor.putString(URAIAN_POTENSI_SENGKETA, uraianPotensiSengketa)
//        editor.apply()
//    }
//
//    // Fungsi untuk mendapatkan data Potensi Sengketa
//    fun getPotensiSengketa(): Map<String, String?> {
//        return mapOf(
//            "peserta_pemilihan" to sharedPreferences.getString(PESERTA_PEMILIHAN, null),
//            "tempat_kejadian" to sharedPreferences.getString(TEMPAT_KEJADIAN, null),
//            "waktu_kejadian" to sharedPreferences.getString(WAKTU_KEJADIAN, null),
//            "bentuk_objek" to sharedPreferences.getString(BENTUK_OBJEK, null),
//            "identitas_objek" to sharedPreferences.getString(IDENTITAS_OBJEK, null),
//            "hari_tanggal" to sharedPreferences.getString(HARI_TANGGAL, null),
//            "kerugian_langsung" to sharedPreferences.getString(KERUGIAN_LANGSUNG, null),
//            "uraian_potensi_sengketa" to sharedPreferences.getString(URAIAN_POTENSI_SENGKETA, null)
//        )
//    }
}
