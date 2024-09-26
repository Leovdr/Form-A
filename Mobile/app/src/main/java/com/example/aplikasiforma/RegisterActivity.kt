package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        auth = FirebaseAuth.getInstance()
        preferencesHelper = PreferencesHelper(this)

        val registerButton = findViewById<Button>(R.id.registerButton)
        val emailField = findViewById<EditText>(R.id.emailEditTextreg)
        val passwordField = findViewById<EditText>(R.id.passwordEditTextreg)
        val fullnameField = findViewById<EditText>(R.id.etpersonreg)

        registerButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val fullname = fullnameField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && fullname.isNotEmpty()) {
                if (password.length >= 6) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                // Simpan fullname ke SharedPreferences
                                preferencesHelper.saveFullname(fullname)

                                // Kirim data registrasi ke server
                                sendUserDataToServer(uid, email, fullname)

                                Toast.makeText(this, "Registered successfully. Please login.", Toast.LENGTH_SHORT).show()

                                // Setelah registrasi, arahkan pengguna ke LoginActivity untuk login ulang
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val tvsignin = findViewById<LinearLayout>(R.id.tvsignin)
        tvsignin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun sendUserDataToServer(uid: String?, email: String, fullname: String) {
        if (uid == null) return

        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("uid", uid)
            .add("email", email)
            .add("fullname", fullname)
            .build()

        val request = Request.Builder()
            .url("https://kaftapus.web.id/api/register.php")  // Ganti dengan URL API server kamu
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("RegisterActivity", "Error sending user data to server: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Failed to send data to server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("RegisterActivity", "User data sent to server successfully")
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Data successfully sent to server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("RegisterActivity", "Failed to send user data: ${response.message}")
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Server error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
