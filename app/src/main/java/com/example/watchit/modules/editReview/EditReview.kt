package com.example.watchit.modules.editReview

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.databinding.FragmentEditReviewBinding
import com.squareup.picasso.Picasso

class EditReview : Fragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedImageURI: Uri
    private var imageChanged = false
    private var ratingBarChanged = false
    private var descriptionChanged = false

    private val args by navArgs<EditReviewArgs>()

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
                    binding.movieImageView.setImageURI(imageUri)
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
        _binding = FragmentEditReviewBinding.inflate(inflater, container, false)
        val view = binding.root

        initFields()

        defineUpdateButtonClickListener()
        definePickImageClickListener()

        return view
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun definePickImageClickListener() {
        binding.btnPickImage.setOnClickListener {
            defineImageSelectionCallBack()
        }
    }

    private fun defineUpdateButtonClickListener() {
        binding.updateButton.setOnClickListener {
            if (validateReviewUpdate()) {
                if (ratingBarChanged || descriptionChanged) updateReview()
                if (imageChanged) updateReviewImage()

                findNavController().navigate(R.id.action_editReview_to_myReviews)
            } else {
                Toast.makeText(
                    requireContext(), "invalid details", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initFields() {
        val currentReview = args.selectedReview

        binding.descriptionEditText.setText(currentReview.description)
        binding.ratingTextNumber.setText(currentReview.score.toString())

        binding.descriptionEditText.addTextChangedListener { descriptionChanged = true }
        binding.ratingTextNumber.addTextChangedListener { ratingBarChanged = true }


        Model.instance.getReviewImage(currentReview.id) { uri ->
            selectedImageURI = uri
            Picasso.get().load(uri).into(binding.movieImageView)
        }
    }

    private fun updateReview() {
        val description = binding.descriptionEditText.text.toString().trim()
        val score = binding.ratingTextNumber.text.toString().trim().toDouble()

        val updatedReview = Review(
            args.selectedReview.id,
            score,
            args.selectedReview.userId,
            description,
            args.selectedReview.movieName
        )

        Model.instance.updateReview(updatedReview) {
            Toast.makeText(
                context,
                "Review updated!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateReviewImage() {
        val reviewId = args.selectedReview.id

        Model.instance.updateReviewImage(reviewId, selectedImageURI) {
            Toast.makeText(requireContext(), "Upload Image Successful", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun validateReviewUpdate(
    ): Boolean {
        val description = binding.descriptionEditText.text.toString().trim()
        val rating = binding.ratingTextNumber.text.toString().trim()

        if (description.isEmpty()) {
            binding.descriptionEditText.error = "Description cannot be empty"
            return false
        }
        if (rating.isEmpty()) {
            binding.descriptionEditText.error = "Rating cannot be empty"
            return false
        } else if (rating.toDouble() < 1 || rating.toDouble() > 10) {
            binding.descriptionEditText.error = "Please rate the movie between 1-10"
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
        binding.btnPickImage.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionLauncher.launch(intent)
        }
    }
}