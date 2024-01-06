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
    private lateinit var emailEditText: EditText
    private lateinit var resetPasswordButton: Button
    private val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_main)

        auth = Firebase.auth
        emailEditText = findViewById(R.id.editTextEmailAddress)
        resetPasswordButton = findViewById(R.id.ResetPasswordButton)

        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val syntaxChecksResult = validateUserEmail(email)

            db.collection("Users").whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                if(documents.isEmpty) {
                    Toast.makeText(this@ForgotPasswordActivity, "This Email does not exist", Toast.LENGTH_SHORT).show()
                } else if (syntaxChecksResult) {
                    auth.sendPasswordResetEmail(email).addOnSuccessListener ({
                        Toast.makeText(this@ForgotPasswordActivity, "Reset Password link has been sent", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }).addOnFailureListener({
                        Toast.makeText(this@ForgotPasswordActivity, "Error: " + it.message, Toast.LENGTH_SHORT).show()
                    })
                }
            }.addOnFailureListener{ exception ->
                Log.d("Fail", exception.message.toString())
            }
        }

        val rememberPasswordTextView: TextView = findViewById(R.id.CreateAccountLinkTextView)
        rememberPasswordTextView.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateUserEmail(
        email: String
    ): Boolean {
        // Basic checks
        if (email.isEmpty()) {
            emailEditText.error = "Email cannot be empty"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email format"
            return false
        }
        return true
    }
}