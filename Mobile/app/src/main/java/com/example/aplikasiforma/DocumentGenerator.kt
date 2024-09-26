package com.example.aplikasiforma

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.BreakType
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger

class DocumentGenerator(private val context: Context) {

    private val preferencesHelper = PreferencesHelper(context) // Inisialisasi PreferencesHelper

    fun generateAndSaveDocument(
        data: DocumentData,
        signatureFilePath: String?,
        signatureBitmap: Bitmap?,
        selectedImages: List<Bitmap>
    ): Boolean {
        // Ambil data dari SharedPreferences menggunakan PreferencesHelper (Bagian I-IV)
        val noSurat = preferencesHelper.getNomorSurat() ?: "Tidak Ada Nomor Surat"
        val namaPelaksana = preferencesHelper.getNamaPelaksana() ?: "Tidak Ada Nama Pelaksana"
        val jabatan = preferencesHelper.getJabatan() ?: "Tidak Ada Jabatan"
        val nomorSuratPerintah = preferencesHelper.getNomorSuratPerintah() ?: "Tidak Ada Nomor Surat Perintah"
        val alamat = preferencesHelper.getAlamat() ?: "Tidak Ada Alamat"
        val jenisPemilihan = preferencesHelper.getJenisPemilihan() ?: "Tidak Ada Jenis Pemilihan"
        val tahapanPemilihan = preferencesHelper.getTahapanPemilihan() ?: "Tidak Ada Tahapan Pemilihan"
        val bentukPengawasan = preferencesHelper.getBentukPengawasan() ?: "Tidak Ada Bentuk Pengawasan"
        val tujuanPengawasan = preferencesHelper.getTujuanPengawasan() ?: "Tidak Ada Tujuan Pengawasan"
        val sasaranPengawasan = preferencesHelper.getSasaranPengawasan() ?: "Tidak Ada Sasaran Pengawasan"
        val waktuTempatPengawasan = preferencesHelper.getWaktuTempatPengawasan() ?: "Tidak Ada Waktu dan Tempat Pengawasan"
        val uraianSingkat = preferencesHelper.getUraianSingkat() ?: "Tidak Ada Uraian Singkat"
        val tanggalTtd = preferencesHelper.getTanggalTtd() ?: "Tidak Ada Tanggal TTD"
        val jabatanTtd = preferencesHelper.getJabatanTtd() ?: "Tidak Ada Jabatan TTD"
        val namaTtd = preferencesHelper.getNamaTtd() ?: "Tidak Ada Nama TTD"

        val document = XWPFDocument()
        val styler = DocumentStyler(document)

        // Menambahkan judul dokumen
        styler.createTitleParagraph("FORMULIR MODEL A")
        styler.createTitleParagraph("")
        styler.createTitleParagraph("LAPORAN HASIL PENGAWASAN PEMILU")
        styler.createTitleParagraph("NOMOR: $noSurat")
        styler.createTitleParagraph(" ")

        // Membuat tabel utama
        val table: XWPFTable = document.createTable()
        removeTableBorders(table)
        setColumnWidths(
            table,
            listOf(500, 700, 3500, 300, 3000)
        )  // Mengatur ukuran kolom lebih besar dan tetap

        // Bagian I-IV dari SharedPreferences
        addStyledRowToTable(styler, table, listOf(" ", "I.", "Data Pengawas Pemilihan", "", ""))
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "a. Nama Pelaksana Tugas Pengawasan", ":", namaPelaksana)
        )
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Jabatan", ":", jabatan))
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "c. Nomor Surat Perintah Tugas", ":", nomorSuratPerintah)
        )
        addStyledRowToTable(styler, table, listOf(" ", "", "d. Alamat", ":", alamat))

        // Jenis dan Tahapan Pemilihan
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "II.", "Jenis dan Tahapan Pemilihan yang Diawasi", "", "")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "a. Jenis Pemilihan", ":", jenisPemilihan)
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "b. Tahapan Pemilihan", ":", tahapanPemilihan)
        )

        // Kegiatan Pengawasan
        addStyledRowToTable(styler, table, listOf(" ", "III.", "Kegiatan Pengawasan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "Kegiatan", "", ""))
        addStyledRowToTable(styler, table, listOf(" ", "", "a. Bentuk", ":", bentukPengawasan))
        addStyledRowToTable(styler, table, listOf(" ", "", "b. Tujuan", ":", tujuanPengawasan))
        addStyledRowToTable(styler, table, listOf(" ", "", "c. Sasaran", ":", sasaranPengawasan))
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "d. Waktu dan Tempat", ":", waktuTempatPengawasan)
        )

        // Uraian Singkat Hasil Pengawasan
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "IV.", "Uraian Singkat Hasil Pengawasan", ":", uraianSingkat)
        )

        // Bagian V-VI dari DocumentData (data yang diterima dari activity sebelumnya)
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "V.", "Informasi Dugaan Pelanggaran", "", "")
        )
        addStyledRowToTable(styler, table, listOf(" ", "", "1. Peristiwa", "", ""))
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "a. Peristiwa", ":", data.dugaanPelanggaran["Peristiwa"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "b. Tempat Kejadian",
                ":",
                data.dugaanPelanggaran["Tempat Kejadian"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "c. Waktu Kejadian",
                ":",
                data.dugaanPelanggaran["Waktu Kejadian"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "d. Pelaku", ":", data.dugaanPelanggaran["Pelaku"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "e. Alamat", ":", data.dugaanPelanggaran["Alamat"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "2. Pasal yang Diduga Dilanggar",
                ":",
                data.dugaanPelanggaran["Pasal yang dilanggar"] ?: "Nihil"
            )
        )

        // Saksi-saksi
        addStyledRowToTable(styler, table, listOf(" ", "", "3. Saksi-saksi", "", ""))
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "a. Nama", ":", data.dugaanPelanggaran["Nama Saksi"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "   Alamat", ":", data.dugaanPelanggaran["Alamat Saksi"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "b. Nama", ":", data.dugaanPelanggaran["Nama Saksi2"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "   Alamat", ":", data.dugaanPelanggaran["Alamat Saksi2"] ?: "Nihil")
        )

        // Bukti
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "4. Bukti", ":", data.dugaanPelanggaran["Bukti"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "5. Uraian singkat dugaan pelanggaran Pemilihan",
                ":",
                data.dugaanPelanggaran["Uraian Singkat"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "6. Jenis dugaan pelanggaran",
                ":",
                data.dugaanPelanggaran["Jenis Dugaan"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "7. Fakta dan Keterangan",
                ":",
                data.dugaanPelanggaran["Fakta dan keterangan"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "", "8. Analisa", ":", data.dugaanPelanggaran["Analisa"] ?: "Nihil")
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "9. Tindak Lanjut",
                ":",
                data.dugaanPelanggaran["Tindak lanjut"] ?: "Nihil"
            )
        )

        // Informasi Potensi Sengketa (Bagian VI)
        addStyledRowToTable(
            styler,
            table,
            listOf(" ", "VI.", "Informasi Potensi Sengketa Pemilihan", "", "")
        )
        addStyledRowToTable(styler, table, listOf(" ", "", "1. Peristiwa", "", ""))
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "a. Peserta Pemilihan",
                ":",
                data.potensiSengketa["Peserta Pemilihan"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "b. Tempat Kejadian",
                ":",
                data.potensiSengketa["Tempat Kejadian"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "c. Waktu Kejadian",
                ":",
                data.potensiSengketa["Waktu Kejadian"] ?: "Nihil"
            )
        )
        addStyledRowToTable(styler, table, listOf(" ", "", "2. Objek Sengketa", "", ""))
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "a. Bentuk Objek Sengketa",
                ":",
                data.potensiSengketa["Bentuk Objek Sengketa"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "b. Identitas objek sengketa",
                ":",
                data.potensiSengketa["Identitas Objek Sengketa"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "c. Hari/tanggal dikeluarkan",
                ":",
                data.potensiSengketa["Hari/Tanggal Dikeluarkan"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "d. Kerugian langsung",
                ":",
                data.potensiSengketa["Kerugian Langsung"] ?: "Nihil"
            )
        )
        addStyledRowToTable(
            styler,
            table,
            listOf(
                " ",
                "",
                "3. Uraian singkat potensi sengketa pilihan",
                ":",
                data.potensiSengketa["Uraian Singkat Potensi Sengketa"] ?: "Nihil"
            )
        )

        // Add Signature Section with Signature Image if Available
        addSignatureSection(document, styler, signatureBitmap, tanggalTtd, jabatanTtd, namaTtd)

        // Tambahkan gambar lampiran ke dokumen jika ada
        if (selectedImages.isNotEmpty()) {
            addImagesToDocument(document, selectedImages)
        }

        // Simpan dokumen ke file
        return saveDocumentToFile(document)
    }

    private fun setColumnWidths(table: XWPFTable, widths: List<Int>) {
        val tblPr = table.ctTbl.tblPr ?: table.ctTbl.addNewTblPr()
        tblPr.addNewTblLayout().type = STTblLayoutType.FIXED // Set fixed layout for the table

        for (i in 0 until table.numberOfRows) {
            val row = table.getRow(i)
            for (j in widths.indices) {
                val cell = row.getCell(j) ?: row.createCell()
                val cellCTTc = cell.ctTc
                val tcPr = cellCTTc.addNewTcPr()
                val tcW = tcPr.addNewTcW()
                tcW.w = BigInteger.valueOf(widths[j].toLong())
                tcW.type = STTblWidth.DXA // Set width type to DXA (twips)

                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)
            }
        }
    }

    private fun removeTableBorders(table: XWPFTable) {
        val tblBorders: CTTblBorders =
            table.ctTbl.tblPr.getTblBorders() ?: table.ctTbl.tblPr.addNewTblBorders()
        tblBorders.unsetInsideH()
        tblBorders.unsetInsideV()
        tblBorders.unsetTop()
        tblBorders.unsetLeft()
        tblBorders.unsetBottom()
        tblBorders.unsetRight()
    }

    private fun addStyledRowToTable(
        styler: DocumentStyler,
        table: XWPFTable,
        values: List<String>
    ) {
        val row = table.createRow()
        for (i in values.indices) {
            if (i < row.tableCells.size) {
                styler.styleTableCell(
                    row.getCell(i),
                    values[i],
                    fontSize = 12,
                    alignment = ParagraphAlignment.BOTH
                )
            } else {
                styler.styleTableCell(
                    row.addNewTableCell(),
                    values[i],
                    fontSize = 12,
                    alignment = ParagraphAlignment.BOTH
                )
            }
        }
    }

    private fun addSignatureSection(
        document: XWPFDocument,
        styler: DocumentStyler,
        signatureBitmap: Bitmap?,
        signatureDate: String,
        signaturePosition: String,
        signatureName: String
    ) {
        val table: XWPFTable = document.createTable(1, 5)
        setColumnWidths(table, listOf(500, 600, 1500, 600, 4000))
        removeTableBorders(table)

        val row: XWPFTableRow = table.getRow(0)

        // Mengatur tanda tangan pada kolom kelima
        val cell5: XWPFTableCell = row.getCell(4) ?: row.createCell()

        // Tambahkan "signatureDate" sebagai paragraf baru
        val dateParagraph = cell5.addParagraph()
        styler.styleParagraph(
            dateParagraph,
            text = signatureDate,
            isBold = false,
            fontSize = 12,
            alignment = ParagraphAlignment.CENTER
        )

        // Tambahkan "signaturePosition" sebagai paragraf baru
        val positionParagraph = cell5.addParagraph()
        styler.styleParagraph(
            positionParagraph,
            text = signaturePosition,
            isBold = false,
            fontSize = 12,
            alignment = ParagraphAlignment.CENTER
        )

        // Add Signature Image from Bitmap if Available
        if (signatureBitmap != null) {
            val signatureParagraph = cell5.addParagraph()
            signatureParagraph.alignment = ParagraphAlignment.CENTER
            val signatureRun = signatureParagraph.createRun()
            val outputStream = ByteArrayOutputStream()
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val imageData = outputStream.toByteArray()
            try {
                signatureRun.addPicture(
                    ByteArrayInputStream(imageData),
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "Signature.png",
                    Units.toEMU(100.0),
                    Units.toEMU(50.0)
                )
            } catch (e: IOException) {
                Log.e("DocumentGenerator", "Error adding signature image: ${e.message}")
            }
        }

        // Tambahkan Nama sebagai paragraf baru
        val nameParagraph = cell5.addParagraph()
        styler.styleParagraph(
            nameParagraph,
            text = signatureName,
            isBold = true,
            fontSize = 12,
            alignment = ParagraphAlignment.CENTER
        )


        val paragraphBreak = document.createParagraph()
        val runBreak = paragraphBreak.createRun()
        runBreak.addBreak(BreakType.PAGE)

        val lampiranParagraph = document.createParagraph()
        lampiranParagraph.alignment = ParagraphAlignment.CENTER // Atur alignment ke center

        val lampiranRun = lampiranParagraph.createRun()
        lampiranRun.isBold = true // Atur teks menjadi bold
        lampiranRun.fontSize = 14 // Atur ukuran font
        lampiranRun.fontFamily = "Times New Roman"
        lampiranRun.setText("LAMPIRAN") // Set teks paragraf

    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }


    private fun addImagesToDocument(document: XWPFDocument, images: List<Bitmap>) {
        for (bitmap in images) {
            // Resize bitmap to reduce memory usage
            val resizedBitmap = resizeBitmap(bitmap, 800, 600)

            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream) // Change to JPEG and reduce quality
            val imageData = outputStream.toByteArray()

            try {
                // Create a new paragraph and set alignment to center
                val paragraph = document.createParagraph()
                paragraph.alignment = ParagraphAlignment.CENTER

                val run = paragraph.createRun()
                run.addBreak() // Tambahkan line break sebelum gambar jika diperlukan

                // Add picture to the run
                run.addPicture(
                    ByteArrayInputStream(imageData),
                    XWPFDocument.PICTURE_TYPE_JPEG, // Changed to JPEG
                    "Image.jpg", // Changed to jpg
                    Units.toEMU(300.0), // Sesuaikan ukuran gambar di sini
                    Units.toEMU(300.0)
                )
            } catch (e: Exception) {
                Log.e("DocumentGenerator", "Error adding image: ${e.message}")
            } finally {
                // Recycle bitmap to free memory
                resizedBitmap.recycle()
            }
        }
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace("/", "-")  // Ganti "/" dengan "-"
    }

    private fun saveDocumentToFile(document: XWPFDocument): Boolean {
        val nomorSurat = preferencesHelper.getNomorSurat() ?: "Laporan_Hasil_Pengawasan"
        val fileName = "${sanitizeFileName(nomorSurat)}.docx"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, fileName)

        return try {
            FileOutputStream(file).use { out ->
                document.write(out)
            }
            document.close()

            // Log untuk memverifikasi bahwa file sudah disimpan
            Log.d("FILE_SAVED", "File berhasil disimpan di path: ${file.absolutePath}, ukuran: ${file.length()} bytes")

            true
        } catch (e: IOException) {
            Log.e("DocumentGenerator", "Gagal menyimpan dokumen: ${e.message}")
            false
        }
    }

}
