package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var preferencesHelper: PreferencesHelper
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferencesHelper = PreferencesHelper(this)

        // Jika user sudah login, alihkan ke HomeActivity
        if (preferencesHelper.isLoggedIn()) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        auth = FirebaseAuth.getInstance()

        // Konfigurasi Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailField = findViewById<EditText>(R.id.emailEditText)
        val passwordField = findViewById<EditText>(R.id.passwordEditText)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            // Kirim data login ke server
                            sendLoginDataToServer(uid, email, "")
                        } else {
                            Toast.makeText(this, "Login Failed: UID is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val googleSignInButton = findViewById<Button>(R.id.logingoogle)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        val tvsignup = findViewById<LinearLayout>(R.id.tvsignup)
        tvsignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.w("LoginActivity", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign-In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val uid = user?.uid
                val email = user?.email ?: ""
                val fullname = account.displayName ?: ""  // Ambil fullname dari akun Google

                if (uid != null) {
                    // Kirim data login dan fullname ke server
                    sendLoginDataToServer(uid, email, fullname)
                }

                Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Google Sign-In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mengirim data login ke server (dengan fullname)
    private fun sendLoginDataToServer(uid: String, email: String, fullname: String) {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("uid", uid)
            .add("email", email)
            .add("fullname", fullname)  // Menambahkan fullname
            .build()

        val request = Request.Builder()
            .url("https://kaftapus.web.id/api/login.php")  // Ganti dengan URL API server kamu
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LoginActivity", "Error sending login data to server: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Failed to send data to server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Parsing JSON response
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "")
                    val fullnameFromServer = jsonObject.optString("fullname")  // Ambil fullname dari respons
                    val userId = jsonObject.optString("user_id")

                    // Simpan fullname dan userId di PreferencesHelper
                    preferencesHelper.saveFullname(fullnameFromServer)
                    preferencesHelper.saveUid(userId)

                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        // Pindah ke HomeActivity setelah login sukses
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.e("LoginActivity", "Failed to send login data: ${response.message}")
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Server error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
