package com.example.watchit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var logInButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        val forgotPasswordTextView: TextView = findViewById(R.id.ForgotPassTextView)
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this@MainActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        val registerTextView: TextView = findViewById(R.id.CreateAccountLinkTextView)
        registerTextView.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = Firebase.auth
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextPassword)
        logInButton = findViewById(R.id.LogInButton)

        logInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val syntaxChecksResult = validateUserCredentials(email, password)

            if (syntaxChecksResult) {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this@MainActivity, ReviewActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }.addOnFailureListener {
                    Toast.makeText(
                        this@MainActivity,
                        "Your Email or Password is incorrect!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

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

    //    private fun checkIfEmailExists(email: String) {
//        auth.fetchSignInMethodsForEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val signInMethods = task.result?.signInMethods
//                    if (signInMethods.isNullOrEmpty()) {
//                        // Email is not registered
//                        // You can handle this case accordingly
//                    } else {
//                        // Email is registered
//                        // You can handle this case accordingly
//                    }
//                } else {
//                    // An error occurred while checking the email
//                    // You can handle this case accordingly
//                }
//            }
//    }
}