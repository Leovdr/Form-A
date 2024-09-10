package com.example.aplikasiforma

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class AttachmentUploader(private val context: Context) {

    private val REQUEST_CODE_UPLOAD_ATTACHMENT = 2

    fun uploadAttachment(activity: Activity){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, REQUEST_CODE_UPLOAD_ATTACHMENT)
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?, onResult: (Uri?) -> Unit){
        if (requestCode  == REQUEST_CODE_UPLOAD_ATTACHMENT && resultCode == Activity.RESULT_OK){
            val selectedImageUri = data?.data
            onResult(selectedImageUri)
        }else{
            onResult(null)
        }
    }



//    private lateinit var selectedAttachmentUri: Uri
//
//    fun uploadAttachment(activity: AppCompatActivity) {
//        val resultLauncher = activity.registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data: Intent? = result.data
//                data?.data?.let { uri ->
//                    selectedAttachmentUri = uri
//                    addAttachmentToDocument(selectedAttachmentUri)
//                }
//            }
//        }
//
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        resultLauncher.launch(intent)
//    }
//
//    private fun addAttachmentToDocument(imageUri: Uri) {
//        try {
//            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
//            if (inputStream != null) {
//                val fileName = "Laporan_Hasil_Pengawasan.docx"
//                val documentsDir = context.getExternalFilesDir(null)
//                val file = File(documentsDir, fileName)
//                val document = XWPFDocument(FileInputStream(file))
//
//                // Menambahkan gambar lampiran ke dokumen
//                val paragraph = document.createParagraph()
//                paragraph.alignment = ParagraphAlignment.CENTER
//                val run = paragraph.createRun()
//
//                run.addPicture(
//                    inputStream,
//                    XWPFDocument.PICTURE_TYPE_JPEG,
//                    "attachment.jpg",
//                    Units.toEMU(200.0),
//                    Units.toEMU(100.0)
//                )
//
//                // Simpan dokumen yang telah dimodifikasi
//                FileOutputStream(file).use { out ->
//                    document.write(out)
//                }
//                document.close()
//            }
//        } catch (e: Exception) {
//            Log.e("AttachmentUploader", "Error adding attachment: ${e.message}")
//        }
//    }
}
