package com.example.watchit.modules.register

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresExtension
import com.example.watchit.MainActivity
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.user.User
import com.example.watchit.modules.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class RegisterActivity : ComponentActivity() {

    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private var selectedImageURI: Uri? = null
    private lateinit var firstNameInputLayout: TextInputLayout
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameInputLayout: TextInputLayout
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var emailAddressInputLayout: TextInputLayout
    private lateinit var emailAddressEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private val auth = Firebase.auth

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        defineImageSelectionCallBack()
        openGallery()
        toLoginActivity()
        createNewUser()
    }

    private fun createNewUser() {
        firstNameInputLayout = findViewById(R.id.layoutFirstName)
        firstNameEditText = findViewById(R.id.editTextFirstName)
        lastNameInputLayout = findViewById(R.id.layoutLastName)
        lastNameEditText = findViewById(R.id.editTextLastName)
        emailAddressInputLayout = findViewById(R.id.layoutEmailAddress)
        emailAddressEditText = findViewById(R.id.editTextEmailAddress)
        passwordInputLayout = findViewById(R.id.layoutPassword)
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordInputLayout = findViewById(R.id.layoutConfirmPassword)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword)

        findViewById<Button>(R.id.SignUpButton).setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailAddressEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            val syntaxChecksResult =
                validateUserRegistration(firstName, lastName, email, password, confirmPassword)

            if (syntaxChecksResult) {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    val authenticatedUser = it.user!!

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(selectedImageURI)
                        .setDisplayName("$firstName $lastName")
                        .build()

                    authenticatedUser.updateProfile(profileUpdates)

                    Model.instance.addUser(
                        User(authenticatedUser.uid, firstName, lastName),
                        selectedImageURI!!
                    ) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Register Successful",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
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

    private fun toLoginActivity() {
        findViewById<TextView>(R.id.LogInTextView).setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateUserRegistration(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (firstName.isEmpty()) {
            firstNameInputLayout.error = "First name cannot be empty"
            return false
        } else {
            firstNameInputLayout.error = null
        }
        if (lastName.isEmpty()) {
            lastNameInputLayout.error = "Last name cannot be empty"
            return false
        } else {
            lastNameInputLayout.error = null
        }
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
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            return false
        } else if (!password.any { it.isDigit() }) {
            passwordInputLayout.error = "Password must contain at least one digit"
            return false
        } else {
            passwordInputLayout.error = null
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.error = "Confirm password cannot be empty"
            return false
        } else if (password != confirmPassword) {
            confirmPasswordInputLayout.error = "Passwords do not match."
            return false
        } else {
            confirmPasswordInputLayout.error = null
        }
        if (selectedImageURI == null) {
            Toast.makeText(
                this@RegisterActivity,
                "You must select Profile Image",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
        findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionCallBack.launch(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            try {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    val imageSize = getImageSize(imageUri)
                    val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                    if (imageSize > maxCanvasSize) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Selected image is too large",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        selectedImageURI = imageUri
                        findViewById<ImageView>(R.id.profileImageView).setImageURI(imageUri)
                    }
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