package com.example.watchit

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.watchit.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import io.ktor.util.rootCause
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class EditMyProfile : Fragment() {

    private var selectedImageURI: Uri? = null
    private lateinit var root: View

    private val imageSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    val imageSize = getImageSize(imageUri)
                    val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                    if (imageSize > maxCanvasSize) {
                        Toast.makeText(
                            requireContext(),
                            "Selected image is too large",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        selectedImageURI = imageUri
                        root.findViewById<ImageView>(R.id.ProfileImageView).setImageURI(imageUri)
                    }
                }
            } catch (e: Exception) {
                Log.d("Gil", "Error: ${e}")
                Toast.makeText(requireContext(), "Error processing result", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        root = inflater.inflate(R.layout.fragment_edit_my_profile, container, false)

        root.findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionLauncher.launch(intent)
        }

//        defineImageSelectionCallBack(root)
        initFields(root)
        root.findViewById<Button>(R.id.UpdateButton).setOnClickListener {
            updateUser()
            uploadImage(auth.currentUser, selectedImageURI!!)
        }

        firstNameEditText = root.findViewById(R.id.editTextFirstName)
        lastNameEditText = root.findViewById(R.id.editTextLastName)
        emailEditText = root.findViewById(R.id.editTextEmailAddress)
        passwordEditText = root.findViewById(R.id.editTextTextPassword)
        confirmPasswordEditText = root.findViewById(R.id.editTextTextConfirmPassword)

        return root
    }

    private fun initFields(root: View) {
        val currentUser = auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid).get().addOnSuccessListener {
                if(it.exists()) {
                    val user = it.toObject<User>()!!
                    val imageRef = storage.reference.child("images/users/${currentUser.uid}")

                    firstNameEditText.setText(user.firstName)
                    lastNameEditText.setText(user.lastName)
                    emailEditText.setText(user.email)
                    passwordEditText.setText(user.password)
                    confirmPasswordEditText.setText(user.password)

                    imageRef.downloadUrl.addOnSuccessListener {uri ->
                        selectedImageURI = uri
                        Picasso.get().load(uri).into(root.findViewById<ImageView>(R.id.ProfileImageView))
                    }.addOnFailureListener{exception ->
                        Log.d("FirebaseStorage", "Error getting download image URI: $exception")
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "User document isn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener{
                Toast.makeText(
                    requireContext(),
                    "Can't get user document: " + it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateUser() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        val syntaxChecksResult =
            validateUserUpdate(firstName, lastName, email, password, confirmPassword)

        if (syntaxChecksResult) {
            val currentUser = auth.currentUser

            if (currentUser != null) {
                currentUser.updateEmail(email)
                currentUser.updatePassword(password)
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .setPhotoUri(selectedImageURI)
                    .build()
                currentUser.updateProfile(profileUpdates)


                lifecycleScope.launch {
//                    val imageRef = storage.reference.child("images/users/${currentUser.uid}")
                    try {
//                        if (selectedImageURI != null) {
//                            uploadImage(
//                                imageRef,
//                                selectedImageURI!!
//                            )
//                        }
                        db.collection("users")
                            .document(currentUser.uid ?: "")
                            .update(
                                mapOf(
                                    "firstName" to firstName,
                                    "lastName" to lastName,
                                    "email" to email,
                                    "password" to password
                                )
                            ).await()
                        Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.d("Gil", "Error: ${e.message}")
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Update Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Got here a exception when i try yo update the user or just the image, the solution is download the file to local storage first and then upload it
    // Maybe can be solved with the sqllite cache
    private fun uploadImage(
//        imageRef: StorageReference,
        currentUser: FirebaseUser?,
        selectedImageURI: Uri,
    ) {
        lifecycleScope.launch {
            val imageRef = storage.reference.child("images/users/${currentUser?.uid}")
            Log.d("Gil", "imageUri: ${selectedImageURI}")
            try {
                // Use withContext to switch to the IO dispatcher for the putFile operation
                withContext(Dispatchers.IO) {

                    val uploadTask: UploadTask = imageRef.putFile(selectedImageURI)
                    val snapshot: UploadTask.TaskSnapshot = uploadTask.await()
                }
                // Handle the success case
                Toast.makeText(requireContext(), "Upload Image Successful", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                // Handle failures
                Log.d("Gil", "Error: ${e.message}\n trace: ${e.stackTrace}")
                // Toast.makeText(requireContext(), "failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun uploadImageAndUpdateUser(
        imageRef: StorageReference,
        selectedImageURI: Uri,
        currentUser: FirebaseUser?,
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        try {
            // Use withContext to switch to the IO dispatcher for the putFile operation
            withContext(Dispatchers.IO) {
                val uploadTask: UploadTask = imageRef.putFile(selectedImageURI)
                val snapshot: UploadTask.TaskSnapshot = uploadTask.await()

                // Continue with the logic after the upload is successful
                db.collection("users")
                    .document(currentUser?.uid ?: "")
                    .update(
                        mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "password" to password
                        )
                    ).await()
            }

            // Handle the success case
            Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Handle failures
            Log.d("Gil", "Error: ${e.message}\n trace: ${e.stackTrace}")
            // Toast.makeText(requireContext(), "failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateUserUpdate(
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
        if (selectedImageURI == null) {
            Toast.makeText(
                requireContext(),
                "You must select Profile Image",
                Toast.LENGTH_SHORT
            ).show()
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

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

//    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
//    private fun defineImageSelectionCallBack(root: View) {
//        root.findViewById<Button>(R.id.btnPickImage).setOnClickListener {
//            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
//            imageSelectionLauncher.launch(intent)
//        }
//    }
}