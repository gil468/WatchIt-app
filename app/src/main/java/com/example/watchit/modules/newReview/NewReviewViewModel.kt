package com.example.watchit.modules.newReview

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.review.Review
import com.example.watchit.data.review.ReviewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.UUID

class NewReviewViewModel : ViewModel() {
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var description: String = ""
    var rating: Int? = null
    var descriptionError = MutableLiveData("")
    var ratingError = MutableLiveData("")
    var imageError = MutableLiveData("")
    private val auth = Firebase.auth

    fun createReview(
        movieName: String,
        createdReviewCallback: () -> Unit
    ) {
        if (validateReviewUpdate()) {
            val reviewId = UUID.randomUUID().toString()
            val userId = auth.currentUser!!.uid

            val review = Review(
                reviewId,
                rating!!,
                userId,
                description,
                movieName
            )

            ReviewModel.instance.addReview(review, selectedImageURI.value!!) {
                createdReviewCallback()
            }
        }
    }

    private fun validateReviewUpdate(
    ): Boolean {
        var valid = true

        if (description.isEmpty()) {
            descriptionError.postValue("Description cannot be empty")
            valid = false
        }
        Log.d("NewReviewViewModel", "Rating: $rating")
        if (rating == null) {
            ratingError.postValue("Rating cannot be empty")
            valid = false
        } else if (rating!! < 1 || rating!! > 10) {
            ratingError.postValue("Please rate the movie between 1-10")
            valid = false
        }

        if (selectedImageURI.value == null) {
            imageError.postValue("Please select an image")
            valid = false
        }

        return valid
    }
}