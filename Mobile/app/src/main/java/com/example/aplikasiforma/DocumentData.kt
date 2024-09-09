package com.example.aplikasiforma

data class DocumentData(
    val noSurat: String,
    val namaPelaksana: String,
    val jabatan: String,
    val nomorSuratperintah: String,
    val alamat: String,
    val bentuk : String,
    val tujuan: String,
    val sasaran: String,
    val waktuTempat: String,
    val hasilPengawasan: String,
    val dugaanPelanggaran: Map<String, String>,
    val potensiSengketa: String,
    val tahapanPemilihan: String,
    val jenisPemilihan: String
)
