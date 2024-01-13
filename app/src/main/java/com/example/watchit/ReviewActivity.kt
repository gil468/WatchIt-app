package com.example.watchit

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.watchit.model.PublishReviewDTO
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date
import java.util.UUID

class ReviewActivity : ComponentActivity() {

    private lateinit var btnPickImage: Button
    private lateinit var imageView: ImageView
    private lateinit var selectedImageURI: Uri
    private lateinit var ratingBar: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_main)
        val movieId = intent.getIntExtra("movieId", 0)

        btnPickImage = findViewById(R.id.btnPickImage)
        imageView = findViewById(R.id.movieImageView)
        defineImageSelectionCallBack()

        btnPickImage.setOnClickListener {
            openGallery()
        }

        uploadButton = findViewById(R.id.uploadButton)
        ratingBar = findViewById(R.id.ratingTextNumber)
        descriptionEditText = findViewById(R.id.editTextTextMultiLine)

        uploadButton.setOnClickListener {
            val ratingInput = ratingBar.text.toString().trim().toDouble()
            val description = descriptionEditText.text.toString().trim()

            val syntaxChecksResult = validateReviewSyntax(description, ratingInput)

            uploadReview(ratingInput, description, movieId)

            if (syntaxChecksResult) {
                Toast.makeText(
                    this@ReviewActivity,
                    "Review upload Successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadReview(rating: Double, description: String, movieId: Int) {
        val reviewId = UUID.randomUUID().toString()
        val storageRef = storage.reference

        val userId = auth.currentUser!!.uid

        val imageRef = storageRef.child("images/reviews/$reviewId")
        val uploadTask = imageRef.putFile(selectedImageURI)

        uploadTask.addOnFailureListener {
            Toast.makeText(
                this@ReviewActivity,
                "failed!",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            db.collection("reviews")
                .document(reviewId)
                .set(
                    PublishReviewDTO(
                        rating,
                        userId,
                        description,
                        Timestamp(Date()),
                        movieId
                    )
                )
        }
    }

    private fun openGallery() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        imageSelectionCallBack.launch(intent)
    }

    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            try {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    selectedImageURI = imageUri
                    imageView.setImageURI(imageUri)
                } else {
                    Toast.makeText(this@ReviewActivity, "No Image Selected", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ReviewActivity,
                    "Error processing result",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateReviewSyntax(
        description: String,
        rating: Double
    ): Boolean {
        // Basic checks
        if (description.isEmpty()) {
            descriptionEditText.error = "Description cannot be empty"
            return false
        }
        if (rating < 1 || rating > 10) {
            ratingBar.error = "Please rate the movie between 1-10"
            return false
        }
        return true
    }
}