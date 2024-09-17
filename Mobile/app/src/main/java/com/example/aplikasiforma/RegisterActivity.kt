package com.example.aplikasiforma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val registerButton = findViewById<Button>(R.id.registerButton)
        val emailField = findViewById<EditText>(R.id.emailEditTextreg)
        val passwordField = findViewById<EditText>(R.id.passwordEditTextreg)
        val fullnameField = findViewById<EditText>(R.id.etpersonreg)

        registerButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val fullname = fullnameField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && fullname.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userRef = database.getReference("users").child(userId!!)
                        val user = HashMap<String, String>()
                        user["email"] = email
                        user["fullname"] = fullname

                        userRef.setValue(user).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Link to LoginActivity
        val tvsignin = findViewById<LinearLayout>(R.id.tvsignin)
        tvsignin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
