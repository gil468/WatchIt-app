package com.example.watchit.Modules.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.watchit.MainActivity
import com.example.watchit.Modules.register.RegisterActivity
import com.example.watchit.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private var auth = Firebase.auth
    private lateinit var emailAddressInputLayout: TextInputLayout
    private lateinit var emailAddressEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
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
        emailAddressInputLayout = findViewById(R.id.layoutEmailAddress)
        emailAddressEditText = findViewById(R.id.editTextEmailAddress)
        passwordInputLayout = findViewById(R.id.layoutPassword)
        passwordEditText = findViewById(R.id.editTextPassword)

        findViewById<Button>(R.id.LogInButton).setOnClickListener {
            val email = emailAddressEditText.text.toString().trim()
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
        Toast.makeText(
            this@LoginActivity,
            "Welcome ${auth.currentUser?.displayName}!",
            Toast.LENGTH_SHORT
        ).show()
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
            emailAddressInputLayout.error = "Email cannot be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddressInputLayout.error = "Invalid email format"
            return false
        } else {
            emailAddressInputLayout.error = null
        }
        if (password.isEmpty()) {
            passwordInputLayout.error = "Password cannot be empty"
            return false
        } else {
            passwordInputLayout.error = null
        }
        return true
    }
}