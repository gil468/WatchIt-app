package com.example.watchit.modules.search

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.UUID

class NewReviewViewModel : ViewModel() {
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var description: String? = null
    var rating: Int? = null
    var descriptionError = MutableLiveData("")
    var ratingError = MutableLiveData("")
    val auth = Firebase.auth

    fun createReview(
        movieName : String,
        createdReviewCallback: () -> Unit
    ) {
        if (validateReviewUpdate()) {
            val reviewId = UUID.randomUUID().toString()
            val userId = auth.currentUser!!.uid

            val review = Review(
                reviewId,
                rating!!,
                userId,
                description!!,
                movieName
            )

            Model.instance.addReview(review, selectedImageURI.value!!) {
                createdReviewCallback()
            }
        }
    }

    private fun validateReviewUpdate(
    ): Boolean {
        if (description != null && description!!.isEmpty()) {
            descriptionError.postValue("Description cannot be empty")
            return false
        }
        if (rating == null) {
            ratingError.postValue("Rating cannot be empty")
            return false
        }
        if (rating!! < 1 || rating!! > 10) {
            ratingError.postValue("Please rate the movie between 1-10")
            return false
        }
        return true
    }
}