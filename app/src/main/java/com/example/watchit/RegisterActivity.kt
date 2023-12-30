package com.example.watchit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

class RegisterActivity : ComponentActivity() {

    private lateinit var btnPickImage: Button
    private lateinit var imageView: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        btnPickImage = findViewById(R.id.btnPickImage)
        imageView = findViewById(R.id.imageView)
        registerResult()

        imageView.setImageResource(R.drawable.profile_pic_placeholder)

        btnPickImage.setOnClickListener {
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        resultLauncher.launch(intent)
    }

    private fun registerResult() {
        resultLauncher = registerForActivityResult(
            StartActivityForResult(),
            { result: ActivityResult ->
                try {
                    val imageUri: Uri? = result.data?.data
                    if (imageUri != null) {
                        imageView.setImageURI(imageUri)
                    } else {
                        Toast.makeText(this@RegisterActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, "Error processing result", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}
