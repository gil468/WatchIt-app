package com.example.watchit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private var auth = Firebase.auth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (auth.currentUser != null) {
            loggedInHandler()
        }

        toForgotPasswordActivity()
        toRegisterActivity()
        logInUser()
    }

    private fun logInUser() {
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextPassword)

        findViewById<Button>(R.id.LogInButton).setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val syntaxChecksResult = validateUserCredentials(email, password)

            if (syntaxChecksResult) {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    loggedInHandler()
                }.addOnFailureListener {
                    Toast.makeText(
                        this@LoginActivity,
                        "Your Email or Password is incorrect!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun toRegisterActivity() {
        findViewById<TextView>(R.id.CreateAccountLinkTextView).setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun toForgotPasswordActivity() {
        findViewById<TextView>(R.id.ForgotPassTextView).setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loggedInHandler() {
        Toast.makeText(this@LoginActivity, "Welcome ${auth.currentUser?.displayName}!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateUserCredentials(
        email: String,
        password: String
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
        if (password.isEmpty()) {
            passwordEditText.error = "Password cannot be empty"
            return false
        }
        return true
    }
}