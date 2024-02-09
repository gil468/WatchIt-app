package com.example.watchit.modules.editReview

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.review.Review

class EditReviewViewModel : ViewModel() {
    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    private val _score = MutableLiveData<String>()
    val score: LiveData<String> get() = _score

    private val _reviewImageUri = MutableLiveData<Uri>()
    val reviewImageUri: LiveData<Uri> get() = _reviewImageUri

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setScore(score: String) {
        _score.value = score
    }

    fun setReviewImageUri(uri: Uri) {
        _reviewImageUri.value = uri
    }
}