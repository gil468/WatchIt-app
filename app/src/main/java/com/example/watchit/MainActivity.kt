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

private lateinit var auth: FirebaseAuth
private lateinit var emailEditText: EditText
private lateinit var passwordEditText: EditText
private lateinit var logInButton: Button

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        val forgotpasswordtextView: TextView = findViewById(R.id.ForgotPassTextView)
        forgotpasswordtextView.setOnClickListener {
            val intent = Intent(this@MainActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val registertextView: TextView = findViewById(R.id.CreateAccountLinkTextView)
        registertextView.setOnClickListener {
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
            val syntaxChecksResult = validateUserRegistration(email, password)

            if (syntaxChecksResult) {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this@MainActivity, ForgotPasswordActivity::class.java)
//                        startActivity(intent)
                }.addOnFailureListener({
                    Toast.makeText(this@MainActivity, "Your Email or Password is incorrect!", Toast.LENGTH_SHORT).show()
                })
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload(currentUser)
        }
    }

    private fun reload(user: FirebaseUser) {
        user.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // The user data has been successfully refreshed
                // You can now access the updated user information
                val updatedUser = auth.currentUser
                // Update UI or perform other actions as needed
//                moveToOtherActivity(updatedUser?.email ?: "")
            } else {
                // Failed to reload user data
                // Handle the error if needed
                handleError(task.exception)
            }
        }
    }

    private fun moveToOtherActivity(userEmail: String) {
        // To change: move to other activity when it will created
//        val intent = Intent(this, OtherActivity::class.java)
        intent.putExtra("userEmail", userEmail)
        startActivity(intent)
        finish() // Optional: Finish the current activity if needed
    }

    private fun handleError(exception: Exception?) {
        // Handle the error here, you can log it, show a message, etc.
        // For simplicity, let's log the error message
        exception?.message?.let { errorMessage ->
            println("Error: $errorMessage")
        }
    }

    private fun validateUserRegistration(
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