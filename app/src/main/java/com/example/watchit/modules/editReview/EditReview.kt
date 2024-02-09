package com.example.watchit.modules.editReview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.watchit.R
import com.example.watchit.data.review.PublishReviewDTO
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditReview : Fragment() {

    private lateinit var selectedImageURI: Uri
    private lateinit var root: View
    private lateinit var ratingBarEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var movieImageView: ImageView
    private var imageChanged = false
    private var ratingBarChanged = false
    private var descriptionChanged = false
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val args by navArgs<EditReviewArgs>()
    private var adapter: EditReviewRecycleAdapter? = null

    private lateinit var viewModel: EditReviewViewModel

    private val imageSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val imageUri: Uri = result.data?.data!!
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
                    imageChanged = true
                    movieImageView.setImageURI(imageUri)
                }
            } catch (e: Exception) {
                Log.d("EditMyReview", "Error: $e")
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
        root = inflater.inflate(R.layout.fragment_edit_review, container, false)

        ratingBarEditText = root.findViewById(R.id.ratingTextNumber)
        descriptionEditText = root.findViewById(R.id.descriptionEditText)
        movieImageView = root.findViewById(R.id.movieImageView)

        viewModel = ViewModelProvider(this)[EditReviewViewModel::class.java]

        viewModel.description.observe(viewLifecycleOwner) { description ->
            descriptionEditText.setText(description)
        }

        viewModel.score.observe(viewLifecycleOwner) { score ->
            ratingBarEditText.setText(score)
        }

        viewModel.reviewImageUri.observe(viewLifecycleOwner) { uri ->
            Picasso.get().load(uri).into(movieImageView)
        }

        adapter = EditReviewRecycleAdapter(args.selectedReview.description, args.selectedReview.score, args.selectedReview.reviewImage, args.selectedReview.movieName, args.selectedReview.userId)

        initFields()

        root.findViewById<Button>(R.id.updateButton).setOnClickListener {
            if (validateReviewUpdate()) {
                lifecycleScope.launch {
                    val changes = listOf(
                        async { if (ratingBarChanged || descriptionChanged) updateReview() },
                        async { if (imageChanged) updateReviewImage() }
                    )
                    changes.awaitAll()
                }.invokeOnCompletion {
                    findNavController().navigate(R.id.action_editReview_to_myReviews)
                }
            } else {
                Toast.makeText(
                    requireContext(), "invalid details", Toast.LENGTH_SHORT
                ).show()
            }
        }
        root.findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            defineImageSelectionCallBack()
        }

        return root
    }

    private fun initFields() {
        val currentReview = args.selectedReview
        db.collection("reviews")
            .document(currentReview.id).get().addOnSuccessListener {
                if (it.exists()) {
                    val review = it.toObject<PublishReviewDTO>()!!
                    viewModel.setDescription(review.description.toString())
                    viewModel.setScore(review.score.toString())
                    descriptionEditText.addTextChangedListener { descriptionChanged = true }
                    ratingBarEditText.addTextChangedListener { ratingBarChanged = true }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Review document isn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Can't get review document: " + it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        val imageRef = storage.reference.child("images/reviews/${currentReview.id}")

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            selectedImageURI = uri
            viewModel.setReviewImageUri(uri)
        }.addOnFailureListener { exception ->
            Log.d("FirebaseStorage", "Error getting download image URI: $exception")
        }
    }

    private suspend fun updateReview() {
        val description = descriptionEditText.text.toString().trim()
        val score = ratingBarEditText.text.toString().trim()
        val currentReview = args.selectedReview

        try {
            db.collection("reviews")
                .document(currentReview.id)
                .update(
                    mapOf(
                        "description" to description,
                        "score" to score,
                    )
                ).await()
            Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Log.d("UpdateUserInfo", "Error: ${e.message}")
        }
    }

    private suspend fun updateReviewImage() {
        val currentReview = args.selectedReview
        val imageRef = storage.reference.child("images/reviews/${currentReview.id}")
        try {
            val uploadTask: UploadTask = imageRef.putFile(selectedImageURI)
            uploadTask.await()

            Toast.makeText(requireContext(), "Upload Image Successful", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Log.d("ReviewImageUpdate", "Error: ${e.message}\n trace: ${e.stackTrace}")
            Toast.makeText(requireContext(), "Profile image update failed!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateReviewUpdate(
    ): Boolean {
        val description = descriptionEditText.text.toString().trim()
        val rating = ratingBarEditText.text.toString().trim()

        if (description.isEmpty()) {
            descriptionEditText.error = "Description cannot be empty"
            return false
        }
        if (rating.isEmpty()) {
            descriptionEditText.error = "Rating cannot be empty"
            return false
        } else if (rating.toDouble() < 1 || rating.toDouble() > 10) {
            descriptionEditText.error = "Please rate the movie between 1-10"
            return false
        }
        viewModel.setDescription(descriptionEditText.text.toString().trim())
        viewModel.setScore(ratingBarEditText.text.toString().trim())
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