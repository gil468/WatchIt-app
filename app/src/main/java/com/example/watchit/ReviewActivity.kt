package com.example.watchit

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresExtension
import com.example.watchit.model.PublishReviewDTO
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date
import java.util.UUID

class ReviewActivity : ComponentActivity() {

    private var selectedImageURI: Uri? = null
    private lateinit var ratingBar: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_main)

        defineImageSelectionCallBack()
        openGallery()
        createReview()
    }

    private fun createReview() {
        val movieId = intent.getIntExtra("movieId", 0)
        ratingBar = findViewById(R.id.ratingTextNumber)
        descriptionEditText = findViewById(R.id.editTextTextMultiLine)

        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            val ratingInput = ratingBar.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            val syntaxChecksResult = validateReviewSyntax(description, ratingInput)
            if (syntaxChecksResult) {
                uploadReview(ratingInput.toDouble(), description, movieId)
            }
        }
    }

    private fun uploadReview(rating: Double, description: String, movieId: Int) {
        val reviewId = UUID.randomUUID().toString()
        val storageRef = storage.reference
        val userId = auth.currentUser!!.uid

        val imageRef = storageRef.child("images/reviews/$reviewId")
        imageRef.putFile(selectedImageURI!!).addOnFailureListener {
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
            Toast.makeText(
                this@ReviewActivity,
                "Review uploaded Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
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
                            this@ReviewActivity,
                            "Selected image is too large",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        selectedImageURI = imageUri
                        findViewById<ImageView>(R.id.movieImageView).setImageURI(imageUri)
                    }
                } else {
                    Toast.makeText(this@ReviewActivity, "No Image Selected", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReviewActivity, "Error processing result", Toast.LENGTH_SHORT)
                    .show()
                Log.d("Gil", e.message.toString())
            }
        }
    }

    private fun validateReviewSyntax(
        description: String,
        rating: String
    ): Boolean {
        if (description.isEmpty()) {
            descriptionEditText.error = "Description cannot be empty"
            return false
        }
        if (rating.isEmpty()) {
            ratingBar.error = "Rating cannot be empty"
            return false
        } else if (rating.toDouble() < 1 || rating.toDouble() > 10) {
            ratingBar.error = "Please rate the movie between 1-10"
            return false
        }
        if (selectedImageURI == null) {
            Toast.makeText(
                this@ReviewActivity,
                "You must select Review Picture",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
}