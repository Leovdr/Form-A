package com.example.aplikasiforma

import android.content.Context
import android.os.Environment
import android.util.Log
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger

class DocumentGenerator(private val context: Context) {

    fun generateAndSaveDocument(data: DocumentData): Boolean {
        val document = XWPFDocument()
        val styler = DocumentStyler(document)

        // Menambahkan judul dokumen
        styler.createTitleParagraph("FORMULIR MODEL A")
        styler.createTitleParagraph("")
        styler.createTitleParagraph("LAPORAN HASIL PENGAWASAN PEMILU")
        styler.createTitleParagraph("NOMOR: ${data.noSurat}")
        styler.createTitleParagraph(" ")

        // Membuat tabel utama
        val table: XWPFTable = document.createTable()
        removeTableBorders(table)
        setColumnWidths(table, listOf(500, 1000, 3000, 500, 2000))

        // Mengisi data sesuai format dokumen
        addStyledRowToTable(styler, table, listOf(" ", "I.", "Data Pengawas Pemilihan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Nama Pelaksana Tugas Pengawasan", ":", data.namaPelaksana))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Jabatan", ":", data.jabatan))
        addStyledRowToTable(styler, table, listOf(" ", "", "c. Nomor Surat Perintah Tugas", ":", data.nomorSuratperintah))
        addStyledRowToTable(styler, table, listOf(" ", "", "d. Alamat", ":", data.alamat))

        // Jenis dan Tahapan Pemilihan
        addStyledRowToTable(styler, table, listOf(" ", "II.", "Jenis dan Tahapan Pemilihan yang Diawasi", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Jenis Pemilihan", ":", data.jenisPemilihan))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Tahapan Pemilihan", ":", data.tahapanPemilihan))

        // Kegiatan Pengawasan
        addStyledRowToTable(styler, table, listOf(" ", "III.", "Kegiatan Pengawasan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "Kegiatan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Bentuk", ":", data.bentuk))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Tujuan", ":", data.tujuan))
        addStyledRowToTable(styler, table, listOf(" ", "", "c. Sasaran", ":", data.sasaran))
        addStyledRowToTable(styler, table, listOf(" ", "", "d. Waktu dan Tempat", ":", data.waktuTempat))

        // Uraian Singkat Hasil Pengawasan
        addStyledRowToTable(styler, table, listOf(" ", "IV.", "Uraian Singkat Hasil Pengawasan", "", data.hasilPengawasan))

        // Informasi Dugaan Pelanggaran
        addStyledRowToTable(styler, table, listOf(" ", "V.", "Informasi Dugaan Pelanggaran", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "1. Peristiwa", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Peristiwa", ":", data.dugaanPelanggaran["Peristiwa"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Tempat Kejadian", ":", data.dugaanPelanggaran["Tempat Kejadian"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "c. Waktu Kejadian", ":", data.dugaanPelanggaran["Waktu Kejadian"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "d. Pelaku", ":", data.dugaanPelanggaran["Pelaku"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "e. Alamat", ":", data.dugaanPelanggaran["Alamat"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "2. Pasal yang Diduga Dilanggar", "", ""))

        // Saksi-saksi
        addStyledRowToTable(styler, table, listOf(" ", "", "3. Saksi-saksi", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Nama", ":", data.dugaanPelanggaran["Nama Saksi"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "   Alamat", ":", data.dugaanPelanggaran["Alamat Saksi"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Nama", ":", data.dugaanPelanggaran["Nama Saksi"] ?: "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "   Alamat", ":", data.dugaanPelanggaran["Alamat Saksi"] ?: "Nihil"))


        // Bukti
        addStyledRowToTable(styler, table, listOf(" ", "", "4. Bukti", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. ", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. ", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "c. ", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "5. Uraian singkat dugaan pelanggaran Pemilihan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "6. Jenis dugaan pelanggaran", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "7. Fakta dan Keterangan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "8. Analisa", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "9. Tindak Lanjut", "", ""))

        // Informasi Potensi Sengketa
        addStyledRowToTable(styler, table, listOf(" ", "VI.", "Informasi Potensi Sengketa Pemilihan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "1. Peristiwa", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Peserta Pemilihan", ":", "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Tempat Kejadian", ":", "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "c. Waktu Kejadian", ":", "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "2. Objek Sengketa", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Bentuk Objek Sengketa", ":", "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Identitas objek sengketa", ":", "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "c. Hari/tanggal dikeluarkan", ":", "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "d. Kerugian langsung", ":", "Nihil"))
        addStyledRowToTable(styler, table, listOf(" ", "", "3. Uraian singkat potensi sengketa pilihan", "", ""))

        // Bagian Tanda Tangan
        addSignatureSection(document, styler)

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

    private fun setColumnWidths(table: XWPFTable, widths: List<Int>) {
        for (i in 0 until table.numberOfRows) {
            val row = table.getRow(i)
            for (j in widths.indices) {
                val cell = row.getCell(j) ?: row.createCell()
                val cellCTTc = cell.ctTc
                val tcPr = cellCTTc.addNewTcPr()
                tcPr.addNewTcW().w = BigInteger.valueOf(widths[j].toLong())
            }
        }
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

    private fun addSignatureSection(document: XWPFDocument, styler: DocumentStyler) {
        val table: XWPFTable = document.createTable(1, 5)
        setColumnWidths(table, listOf(500, 600, 1500, 600, 4000))
        removeTableBorders(table)

        val row: XWPFTableRow = table.getRow(0)

        // Mengatur tanda tangan pada kolom kelima
        val cell5: XWPFTableCell = row.getCell(4) ?: row.createCell()
        val paragraph = cell5.addParagraph()
        styler.styleParagraph(paragraph, text = "Batu, 4 September", isBold = false, fontSize = 12, alignment = ParagraphAlignment.CENTER)
        styler.styleParagraph(paragraph, text = "\nKasursip Kearsipan", isBold = false, fontSize = 12, alignment = ParagraphAlignment.CENTER)

        // Menambahkan gambar tanda tangan
        val signatureParagraph = cell5.addParagraph()
        signatureParagraph.alignment = ParagraphAlignment.CENTER
        val signatureRun = signatureParagraph.createRun()
        val signatureImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/pp.jpg"
        try {
            val signatureFile = File(signatureImagePath)
            if (signatureFile.exists()) {
                FileInputStream(signatureFile).use { signatureInputStream ->
                    signatureRun.addPicture(signatureInputStream, XWPFDocument.PICTURE_TYPE_JPEG, signatureImagePath, 100 * 9525, 50 * 9525)
                }
            } else {
                Log.e("DocumentGenerator", "Signature image file does not exist: $signatureImagePath")
            }
        } catch (e: IOException) {
            Log.e("DocumentGenerator", "Error adding signature: ${e.message}")
        }

        val nameParagraph = cell5.addParagraph()
        styler.styleParagraph(nameParagraph, text = "(Tegar Alam)", isBold = true, fontSize = 12, alignment = ParagraphAlignment.CENTER)
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
            Log.e("DocumentGenerator", "Failed to save document: ${e.message}")
            false
        }
    }
}
