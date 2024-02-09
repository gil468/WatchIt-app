package com.example.watchit.modules.search

import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.review.PublishReviewDTO
import com.example.watchit.data.review.Review
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date
import java.util.UUID

class NewReview : Fragment() {
    private val args by navArgs<MovieFragmentArgs>()
    private var selectedImageURI: Uri? = null
    private lateinit var root: View

    private lateinit var ratingBar: EditText
    private lateinit var descriptionEditText: EditText
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

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

                        root.findViewById<ImageView>(R.id.movieImageView).setImageURI(imageUri)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error processing result", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_new_review, container, false)

        defineImageSelectionCallBack()
        createReview()

        return root
    }

    private fun createReview() {
        val movieId = args.selectedMovie.id
        val movieName = args.selectedMovie.title

        ratingBar = root.findViewById(R.id.ratingTextNumber)
        descriptionEditText = root.findViewById(R.id.editTextTextMultiLine)

        root.findViewById<Button>(R.id.uploadButton).setOnClickListener {
            val ratingInput = ratingBar.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            val syntaxChecksResult = validateReviewSyntax(description, ratingInput)
            if (syntaxChecksResult) {
                uploadReview(ratingInput.toDouble(), description, movieId, movieName)
            }
        }
    }

    private fun uploadReview(
        rating: Double,
        description: String,
        movieId: Int,
        movieName: String
    ) {
        val reviewId = UUID.randomUUID().toString()
        val storageRef = storage.reference
        val userId = auth.currentUser!!.uid

        val imageRef = storageRef.child("images/reviews/$reviewId")
        imageRef.putFile(selectedImageURI!!).addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "failed!",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            db.collection("reviews")
                .document(reviewId)
                .set(
                    PublishReviewDTO(
                        movieName,
                        rating,
                        userId,
                        description,
                        Timestamp(Date()),
                        movieId
                    )
                )

            val review = Review(
                reviewId,
                rating,
                userId,
                description,
                movieName,
                "asd"
            )

            Model.instance.addReview(review) {
                Navigation.findNavController(root).navigate(R.id.action_newReview_to_feed)
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
                requireContext(),
                "You must select Review Picture",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun defineImageSelectionCallBack() {
        root.findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionLauncher.launch(intent)
        }
    }
}