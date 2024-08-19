package com.example.aplikasiforma

import android.content.Context
import android.os.Environment
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DocumentGenerator(private val context: Context) {


    fun generateAndSaveDocument(data: DocumentData): Boolean {
        val document = XWPFDocument()
        val styler = DocumentStyler(document)

        styler.createTitleParagraph("FORMULIR MODEL A")
        styler.createTitleParagraph("LAPORAN HASIL PENGAWASAN PEMILU")
        addTitleParagraph(data, styler)
        styler.createTitleParagraph("")

        // Membuat tabel
        val table: XWPFTable = document.createTable()

        // Menghilangkan semua border dari tabel
        removeTableBorders(table)

        // Baris pertama: Data Pengawas Pemilihan
        var row = table.getRow(0)
        styler.styleTableCell(row.getCell(0), "", alignment = ParagraphAlignment.CENTER)
        styler.styleTableCell(row.addNewTableCell(), "I", isBold = true, fontSize = 12, alignment = ParagraphAlignment.LEFT)
        styler.styleTableCell(row.addNewTableCell(), "Data Pengawas Pemilihan", isBold = true, fontSize = 12, alignment = ParagraphAlignment.LEFT)
        styler.styleTableCell(row.addNewTableCell(), "", alignment = ParagraphAlignment.LEFT)
        styler.styleTableCell(row.addNewTableCell(), "", alignment = ParagraphAlignment.LEFT)

        // Baris berikutnya: Tahapan yang diawasi
        addStyledRowToTable(styler, table, listOf("", "", "Tahapan yang diawasi", ":", data.tahapan))

        // Baris berikutnya: Nama Pelaksana Tugas Pengawasan
        addStyledRowToTable(styler, table, listOf("", "", "Nama Pelaksana Tugas Pengawasan", ":", data.namaPengawas))

        // Baris berikutnya: Jabatan
        addStyledRowToTable(styler, table, listOf("", "", "Jabatan", ":", data.jabatan))

        // Baris berikutnya: Nomor Surat Tugas
        addStyledRowToTable(styler, table, listOf("", "", "Nomor Surat Tugas", ":", data.nomorSurat))

        // Baris berikutnya: Alamat
        addStyledRowToTable(styler, table, listOf("", "", "Alamat", ":", data.alamat))

        // Tambahkan bagian lain sesuai struktur dokumen yang diinginkan
        addKegiatanPengawasanSection(styler, table)
        addInformasiDugaanPelanggaranSection(styler, table)
        addInformasiPotensiSengketaSection(styler, table)

        // Simpan dokumen ke file
        return saveDocumentToFile(document)
    }

    private fun removeTableBorders(table: XWPFTable) {
        val tblBorders: CTTblBorders = table.ctTbl.tblPr.getTblBorders() ?: table.ctTbl.tblPr.addNewTblBorders()
        tblBorders.unsetInsideH()
        tblBorders.unsetInsideV()
        tblBorders.unsetTop()
        tblBorders.unsetLeft()
        tblBorders.unsetBottom()
        tblBorders.unsetRight()
    }

    private fun addStyledRowToTable(styler: DocumentStyler, table: XWPFTable, values: List<String>) {
        val row = table.createRow()
        for (i in values.indices) {
            if (i < row.tableCells.size) {
                styler.styleTableCell(row.getCell(i), values[i], fontSize = 12, alignment = ParagraphAlignment.BOTH)
            } else {
                styler.styleTableCell(row.addNewTableCell(), values[i], fontSize = 12, alignment = ParagraphAlignment.BOTH)
            }
        }
    }

    private fun addTitleParagraph (data: DocumentData, styler: DocumentStyler) {
        styler.createTitleParagraph("NOMOR: ${data.noSurat}")
    }

    private fun addKegiatanPengawasanSection(styler: DocumentStyler, table: XWPFTable) {
        // Membuat baris untuk bagian II. Kegiatan Pengawasan
        addStyledRowToTable(styler, table, listOf("", "II", "Kegiatan Pengawasan", "", ""))

        // Kegiatan
        addStyledRowToTable(styler, table, listOf("", "", "Kegiatan", "", ""))

        // Bentuk
        addStyledRowToTable(styler, table, listOf("", "", "Bentuk", ":", "Pengawasan melekat terhadap kegiatan Pleno Terbuka Rekapitulasi DPHP pada Pemilihan tahun 2024"))

        // Tujuan
        addStyledRowToTable(styler, table, listOf("", "", "Tujuan", ":", "Memastikan kegiatan Rapat Pleno Terbuka Rekapitulasi DPHP pada Pemilihan tahun 2024 sesuai dengan peraturan perundang-undangan yang berlaku."))

        // Sasaran
        addStyledRowToTable(styler, table, listOf("", "", "Sasaran", ":", "PPS Desa Punten"))

        // Waktu dan Tempat
        addStyledRowToTable(styler, table, listOf("", "", "Waktu dan Tempat", ":", "Jumat, 2 Agustus 2024 di Desa Punten"))
    }

    private fun addInformasiDugaanPelanggaranSection(styler: DocumentStyler, table: XWPFTable) {
        // Membuat baris untuk bagian IV. Informasi Dugaan Pelanggaran
        addStyledRowToTable(styler, table, listOf("", "IV", "Informasi Dugaan Pelanggaran", "", ""))

        // Peristiwa
        addStyledRowToTable(styler, table, listOf("", "", "Peristiwa", ":", "Nihil"))

        // Tempat Kejadian
        addStyledRowToTable(styler, table, listOf("", "", "Tempat Kejadian", ":", "Nihil"))

        // Waktu Kejadian
        addStyledRowToTable(styler, table, listOf("", "", "Waktu Kejadian", ":", "Nihil"))

        // Pelaku
        addStyledRowToTable(styler, table, listOf("", "", "Pelaku", ":", "Nihil"))

        // Alamat
        addStyledRowToTable(styler, table, listOf("", "", "Alamat", ":", "Nihil"))

        // Saksi-Saksi
        addStyledRowToTable(styler, table, listOf("", "", "Saksi-Saksi", "", ""))

        // Nama
        addStyledRowToTable(styler, table, listOf("", "", "Nama", ":", "Nihil"))

        // Alamat
        addStyledRowToTable(styler, table, listOf("", "", "Alamat", ":", "Nihil"))

        // Alat Bukti
        addStyledRowToTable(styler, table, listOf("", "", "Alat Bukti", ":", "Nihil"))

        // Barang Bukti
        addStyledRowToTable(styler, table, listOf("", "", "Barang Bukti", ":", "Nihil"))

        // Uraian Dugaan Pelanggaran
        addStyledRowToTable(styler, table, listOf("", "", "Uraian Dugaan Pelanggaran", ":", "Nihil"))

        // Fakta dan Keterangan
        addStyledRowToTable(styler, table, listOf("", "", "Fakta dan Keterangan", ":", "Nihil"))

        // Analisa
        addStyledRowToTable(styler, table, listOf("", "", "Analisa", ":", "Nihil"))
    }

    private fun addInformasiPotensiSengketaSection(styler: DocumentStyler, table: XWPFTable) {
        // Membuat baris untuk bagian V. Informasi Potensi Sengketa
        addStyledRowToTable(styler, table, listOf("", "V", "Informasi Potensi Sengketa", "", ""))

        // Peristiwa
        addStyledRowToTable(styler, table, listOf("", "", "Peristiwa", ":", "Nihil"))

        // Tempat Kejadian
        addStyledRowToTable(styler, table, listOf("", "", "Tempat Kejadian", ":", "Nihil"))

        // Waktu Kejadian
        addStyledRowToTable(styler, table, listOf("", "", "Waktu Kejadian", ":", "Nihil"))

        // Objek Sengketa
        addStyledRowToTable(styler, table, listOf("", "", "Objek Sengketa", "", ""))

        // Bentuk Objek Sengketa
        addStyledRowToTable(styler, table, listOf("", "", "Bentuk Objek Sengketa", ":", "Nihil"))

        // Identitas Objek Sengketa
        addStyledRowToTable(styler, table, listOf("", "", "Identitas Objek Sengketa", ":", "Nihil"))

        // Hari/Tanggal dikeluarkan
        addStyledRowToTable(styler, table, listOf("", "", "Hari/Tanggal dikeluarkan", ":", "Nihil"))

        // Kerugian Langsung
        addStyledRowToTable(styler, table, listOf("", "", "Kerugian Langsung", ":", "Nihil"))

        // Uraian Singkat Potensi Sengketa
        addStyledRowToTable(styler, table, listOf("", "", "Uraian Singkat Potensi Sengketa", ":", "Nihil"))
    }

    private fun saveDocumentToFile(document: XWPFDocument): Boolean {
        val fileName = "Laporan_Hasil_Pengawasan.docx"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, fileName)

        return try {
            FileOutputStream(file).use { out ->
                document.write(out)
            }
            document.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}
