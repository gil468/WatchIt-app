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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.watchit.R
import com.example.watchit.databinding.FragmentEditReviewBinding
import com.squareup.picasso.Picasso

class EditReview : Fragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditReviewViewModel
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
                    viewModel.selectedImageURI.postValue(imageUri)
                    viewModel.imageChanged = true
                    binding.movieImageView.setImageURI(imageUri)
                }
            } catch (e: Exception) {
                Log.d("EditReview", "Error: $e")
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

        viewModel = ViewModelProvider(this)[EditReviewViewModel::class.java]

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
            viewModel.updateReview {
                findNavController().navigate(R.id.action_editReview_to_myReviews)
            }
        }
    }

    private fun initFields() {
        val currentReview = args.selectedReview
        viewModel.loadReview(currentReview)

        binding.descriptionEditText.setText(currentReview.description)
        binding.ratingTextNumber.setText(currentReview.score.toString())

        binding.descriptionEditText.addTextChangedListener {
            viewModel.description = it.toString().trim()
        }
        binding.ratingTextNumber.addTextChangedListener {
            viewModel.rating = it.toString().toIntOrNull()
        }

        viewModel.selectedImageURI.observe(viewLifecycleOwner) { uri ->
            Picasso.get().load(uri).into(binding.movieImageView)
        }

        viewModel.descriptionError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.descriptionEditText.error = it
        }
        viewModel.ratingError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.ratingTextNumber.error = it
        }
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