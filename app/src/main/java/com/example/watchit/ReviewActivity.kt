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

class ReviewActivity : ComponentActivity() {

    private lateinit var btnPickImage: Button
    private lateinit var imageView: ImageView
    private lateinit var ratingBar: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_main)

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
            val ratingInput = ratingBar.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            val syntaxChecksResult = validateReviewSyntax(description, ratingInput)

            if(syntaxChecksResult) {
                Toast.makeText(this@ReviewActivity, "Review upload Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        imageSelectionCallBack.launch(intent)
    }
    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            { result: ActivityResult ->
                try {
                    val imageUri: Uri? = result.data?.data
                    if (imageUri != null) {
                        imageView.setImageURI(imageUri)
                    } else {
                        Toast.makeText(this@ReviewActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ReviewActivity, "Error processing result", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun validateReviewSyntax(
        description: String,
        rating: String
    ): Boolean {
        // Basic checks
        if (description.isEmpty()) {
            descriptionEditText.error = "Description cannot be empty"
            return false
        }
        if (rating.isEmpty()) {
            ratingBar.error = "Rating cannot be empty"
            return false
        }
        if(rating.toInt() < 1 || rating.toInt()> 10) {
            ratingBar.error = "Please rate the movie between 1-10"
            return false
        }
        return true
    }
}