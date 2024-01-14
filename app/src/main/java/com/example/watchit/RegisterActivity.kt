package com.example.watchit

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.example.watchit.model.UserDTO
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class RegisterActivity : ComponentActivity() {

    private lateinit var btnPickImage: Button
    private lateinit var imageView: ImageView
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private lateinit var selectedImageURI: Uri
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        btnPickImage = findViewById(R.id.btnPickImage)
        imageView = findViewById(R.id.profileimageView)
        defineImageSelectionCallBack()

        btnPickImage.setOnClickListener {
            openGallery()
        }

        val loginTextView: TextView = findViewById(R.id.LogInTextView)
        loginTextView.setOnClickListener {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        firstNameEditText = findViewById(R.id.editTextFirstName)
        lastNameEditText = findViewById(R.id.editTextLastName)
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextTextPassword)
        confirmPasswordEditText = findViewById(R.id.editTextTextConfirmPassword)
        signUpButton = findViewById(R.id.SignUpButton)

        signUpButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            val syntaxChecksResult =
                validateUserRegistration(firstName, lastName, email, password, confirmPassword)

            if (syntaxChecksResult) {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    val user = it.user!!

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                Log.d("Success", "User updated Successfully")
                            } else {
                                Log.d("Fail", "User updated Failed")
                            }
                        }

                    val imageRef =
                        storage.reference.child("images/users/${user.uid}")
                    val uploadTask = imageRef.putFile(selectedImageURI)

                    uploadTask.addOnFailureListener {
                        Toast.makeText(
                            this@RegisterActivity,
                            "failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnSuccessListener {
                        db.collection("users")
                            .document(user.uid)
                            .set(UserDTO(firstName, lastName))
                    }

                    Toast.makeText(this@RegisterActivity, "Register Successful", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@RegisterActivity, SearchActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Register Failed, " + it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateUserRegistration(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Basic checks
        if (firstName.isEmpty()) {
            firstNameEditText.error = "First name cannot be empty"
            return false
        }

        if (lastName.isEmpty()) {
            lastNameEditText.error = "Last name cannot be empty"
            return false
        }
        if (email.isEmpty()) {
            emailEditText.error = "Email cannot be empty"
            return false
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Password cannot be empty"
            return false
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Confirm password cannot be empty"
            return false
        }

        // Advanced checks
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email format"
            return false
        }
        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            return false
        }
//        if (!password.any { it.isUpperCase() }) {
//            passwordEditText.error =
//                "Password must contain at least one uppercase letter"
//            return false
//        }
//        if (!password.any { it.isLowerCase() }) {
//            passwordEditText.error = "Password must contain at least one lowercase letter."
//            return false
//        }
        if (!password.any { it.isDigit() }) {
            passwordEditText.error = "Password must contain at least one digit"
            return false
        }
        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match."
            return false
        }
        // All checks passed
        return true
    }

    private fun openGallery() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        imageSelectionCallBack.launch(intent)
    }

    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            try {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    selectedImageURI = imageUri
                    imageView.setImageURI(imageUri)
                } else {
                    Toast.makeText(this@RegisterActivity, "No Image Selected", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error processing result", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}