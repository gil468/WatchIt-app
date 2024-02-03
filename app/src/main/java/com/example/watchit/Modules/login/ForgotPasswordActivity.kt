package com.example.watchit.Modules.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.watchit.MainActivity
import com.example.watchit.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
        setContentView(R.layout.activity_forgot_password)

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
            val email =
                findViewById<TextInputEditText>(R.id.editTextEmailAddress).text.toString().trim()
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
        val lastNameInputLayout = findViewById<TextInputLayout>(R.id.layoutEmailAddress)
        if (email.isEmpty()) {
            lastNameInputLayout.error = "Email cannot be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            lastNameInputLayout.error = "Invalid email format"
            return false
        } else {
            lastNameInputLayout.error = null
        }
        return true
    }
}