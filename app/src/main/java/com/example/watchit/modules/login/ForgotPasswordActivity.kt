package com.example.watchit.modules.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

class ForgotPasswordActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

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
                auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Reset password link has been sent, Check your Email",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent =
                        Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
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