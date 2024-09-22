package com.example.aplikasiforma

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException

abstract class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    private val errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    private var responseHeaders: Map<String, String>? = null

    // Gunakan lazy untuk inisialisasi dinamis saat dibutuhkan
    private val boundary: String by lazy { "apiclient-${System.currentTimeMillis()}" }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Content-Type"] = "multipart/form-data; boundary=$boundary"
        return headers
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data;boundary=$boundary"
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        try {
            // Menambahkan parameter biasa
            val params = getParams()
            if (params != null && params.isNotEmpty()) {
                for ((key, value) in params.entries) {
                    buildTextPart(dos, key, value)
                }
            }

            // Menambahkan file
            val data = getByteData()
            if (data != null && data.isNotEmpty()) {
                for ((key, value) in data.entries) {
                    buildFilePart(dos, key, value)
                }
            }

            // Akhir dari multipart form-data
            dos.writeBytes("--$boundary--\r\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bos.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        responseHeaders = response.headers
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    override fun deliverError(error: VolleyError) {
        errorListener.onErrorResponse(error)
    }

    abstract fun getByteData(): Map<String, DataPart>?

    @Throws(IOException::class)
    private fun buildTextPart(dos: DataOutputStream, parameterName: String, parameterValue: String) {
        dos.writeBytes("--$boundary\r\n")
        dos.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"\r\n\r\n")
        dos.writeBytes("$parameterValue\r\n")
    }

    @Throws(IOException::class)
    private fun buildFilePart(dos: DataOutputStream, parameterName: String, dataFile: DataPart) {
        dos.writeBytes("--$boundary\r\n")
        dos.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"; filename=\"${dataFile.fileName}\"\r\n")
        dos.writeBytes("Content-Type: ${dataFile.type}\r\n\r\n")
        dos.write(dataFile.content)
        dos.writeBytes("\r\n")
    }

    data class DataPart(val fileName: String, val content: ByteArray, val type: String)
}