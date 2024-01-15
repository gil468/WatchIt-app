package com.example.watchit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ForgotPasswordActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_main)

        sendResetPasswordLink()
        toRegisterActivity()
    }

    private fun toRegisterActivity() {
        findViewById<TextView>(R.id.CreateAccountLinkTextView).setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun sendResetPasswordLink() {
        auth = Firebase.auth
        findViewById<Button>(R.id.ResetPasswordButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextEmailAddress).text.toString().trim()
            val syntaxChecksResult = validateUserEmail(email)
            if (syntaxChecksResult) {
                db.collection("users").whereEqualTo("email", email).get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "This Email doesn't exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Reset password link has been sent, Check your Email",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(this@ForgotPasswordActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Error: " + it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }.addOnFailureListener { exception ->
                    Log.d("Fail", exception.message.toString())
                }
            }
        }
    }

    private fun validateUserEmail(
        email: String
    ): Boolean {
        if (email.isEmpty()) {
            findViewById<EditText>(R.id.editTextEmailAddress).error = "Email cannot be empty"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            findViewById<EditText>(R.id.editTextEmailAddress).error = "Invalid email format"
            return false
        }
        return true
    }
}