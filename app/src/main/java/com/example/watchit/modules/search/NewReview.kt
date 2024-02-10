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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.databinding.FragmentEditReviewBinding
import com.example.watchit.databinding.FragmentNewReviewBinding
import com.example.watchit.modules.editReview.EditReviewViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.UUID

class NewReview : Fragment() {
    private var _binding: FragmentNewReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewReviewViewModel
    private val args by navArgs<MovieFragmentArgs>()

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
                    binding.movieImageView.setImageURI(imageUri)
                }
            } catch (e: Exception) {
                Log.d("NewReview", "Error: $e")
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
        _binding = FragmentNewReviewBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[NewReviewViewModel::class.java]

        defineUploadButtonClickListener()
        definePickImageClickListener()

        return view
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun definePickImageClickListener() {
        binding.btnPickImage.setOnClickListener {
            defineImageSelectionCallBack()
        }
    }

    private fun defineUploadButtonClickListener() {
        binding.editTextTextMultiLine.addTextChangedListener {
            viewModel.description = it.toString().trim()
        }
        binding.ratingTextNumber.addTextChangedListener {
            viewModel.rating = it.toString().toIntOrNull()
        }

        viewModel.descriptionError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.editTextTextMultiLine.error = it
        }
        viewModel.ratingError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.ratingTextNumber.error = it
        }

        binding.uploadButton.setOnClickListener {
            viewModel.createReview(args.selectedMovie.title) {
                findNavController().navigate(R.id.action_newReview_to_feed)
            }
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