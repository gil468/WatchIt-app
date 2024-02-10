package com.example.watchit.modules.profile

import EditMyProfileViewModel
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
import com.example.watchit.R
import com.example.watchit.databinding.FragmentEditMyProfileBinding
import com.squareup.picasso.Picasso

class EditMyProfile : Fragment() {

    private var _binding: FragmentEditMyProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditMyProfileViewModel

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
                    binding.ProfileImageView.setImageURI(imageUri)
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
        _binding = FragmentEditMyProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[EditMyProfileViewModel::class.java]

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
            viewModel.updateUser {
                findNavController().navigate(R.id.action_editMyProfile_to_profile)
            }
        }
    }

    private fun initFields() {
        viewModel.loadUser()

        binding.editTextFirstName.addTextChangedListener {
            viewModel.firstName = it.toString().trim()
        }
        binding.editTextLastName.addTextChangedListener {
            viewModel.lastName = it.toString().trim()
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.editTextFirstName.setText(user.firstName)
            binding.editTextLastName.setText(user.lastName)
        }

        viewModel.selectedImageURI.observe(viewLifecycleOwner) { uri ->
            Picasso.get().load(uri).into(binding.ProfileImageView)
        }

        viewModel.firstNameError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.editTextFirstName.error = it
        }
        viewModel.lastNameError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.editTextLastName.error = it
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