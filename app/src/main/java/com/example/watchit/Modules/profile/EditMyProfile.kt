package com.example.watchit.Modules.profile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.watchit.R
import com.example.watchit.data.user.PublishUserDTO
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditMyProfile : Fragment() {

    private lateinit var selectedImageURI: Uri
    private lateinit var root: View
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var profileImageView: ImageView
    private var imageChanged = false
    private var firstNameChanged = false
    private var lastNameChanged = false
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    private val imageSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val imageUri: Uri = result.data?.data!!
                selectedImageURI = imageUri
                imageChanged = true
                profileImageView.setImageURI(imageUri)
            } catch (e: Exception) {
                Log.d("EditMyProfile", "Error: $e")
                Toast.makeText(
                    requireContext(), "Error processing result", Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_edit_my_profile, container, false)

        firstNameEditText = root.findViewById(R.id.editTextFirstName)
        lastNameEditText = root.findViewById(R.id.editTextLastName)
        profileImageView = root.findViewById(R.id.ProfileImageView)

        initFields(root)

        root.findViewById<Button>(R.id.UpdateButton).setOnClickListener {
            if (validateUserUpdate()) {
                lifecycleScope.launch {
                    val changes = listOf(
                        async { if (firstNameChanged || lastNameChanged) updateUser() },
                        async { if (imageChanged) updateUserImage() }
                    )

                    changes.awaitAll()
                }.invokeOnCompletion {
                    findNavController().navigate(R.id.action_editMyProfile_to_profile)
                }

            } else {
                Toast.makeText(
                    requireContext(), "invalid details", Toast.LENGTH_SHORT
                ).show()
            }
        }

        root.findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            imagePickLogic()
        }

        return root
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun imagePickLogic() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        imageSelectionLauncher.launch(intent)
    }

    private fun initFields(root: View) {
        val currentUser = auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid).get().addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject<PublishUserDTO>()!!
                    firstNameEditText.setText(user.firstName)
                    lastNameEditText.setText(user.lastName)

                    firstNameEditText.addTextChangedListener { firstNameChanged = true }
                    lastNameEditText.addTextChangedListener { lastNameChanged = true }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "User document isn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Can't get user document: " + it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        val imageRef = storage.reference.child("images/users/${currentUser.uid}")

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            selectedImageURI = uri
            Picasso.get().load(uri)
                .into(root.findViewById<ImageView>(R.id.ProfileImageView))
        }.addOnFailureListener { exception ->
            Log.d("FirebaseStorage", "Error getting download image URI: $exception")
        }
    }

    private suspend fun updateUser() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()

        val currentUser = auth.currentUser!!

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$firstName $lastName")
            .setPhotoUri(selectedImageURI)
            .build()
        currentUser.updateProfile(profileUpdates)


        try {
            db.collection("users")
                .document(currentUser.uid)
                .update(
                    mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                    )
                ).await()
            Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Log.d("UpdateUserInfo", "Error: ${e.message}")
        }
    }

    private suspend fun updateUserImage() {
        val currentUser = auth.currentUser!!
        val imageRef = storage.reference.child("images/users/${currentUser.uid}")
        try {

            val uploadTask: UploadTask = imageRef.putFile(selectedImageURI)
            uploadTask.await()

            Toast.makeText(requireContext(), "Upload Image Successful", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Log.d("UserImageUpdate", "Error: ${e.message}\n trace: ${e.stackTrace}")
            Toast.makeText(requireContext(), "Profile image update failed!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateUserUpdate(
    ): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()

        if (firstName.isEmpty()) {
            firstNameEditText.error = "First name cannot be empty"
            return false
        }
        if (lastName.isEmpty()) {
            lastNameEditText.error = "Last name cannot be empty"
            return false
        }
        if (!this::selectedImageURI.isInitialized) {
            Toast.makeText(
                requireContext(),
                "You must select Profile Image",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
}