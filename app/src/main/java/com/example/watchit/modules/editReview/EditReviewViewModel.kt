package com.example.watchit.modules.editReview

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review

class EditReviewViewModel : ViewModel() {
    var imageChanged = false
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var review: Review? = null

    var description: String? = null
    var rating: Int? = null
    var descriptionError = MutableLiveData("")
    var ratingError = MutableLiveData("")

    fun loadReview(review: Review) {
        this.review = review
        this.description = review.description
        this.rating = review.score

        Model.instance.getReviewImage(review.id) {
            selectedImageURI.postValue(it)
        }
    }

    fun updateReview(
        updatedReviewCallback: () -> Unit
    ) {
        if (validateReviewUpdate()) {
            val updatedReview = Review(
                review!!.id,
                rating!!,
                review!!.userId,
                description!!,
                review!!.movieName
            )

            Model.instance.updateReview(updatedReview) {
                if (imageChanged) {
                    Model.instance.updateReviewImage(review!!.id, selectedImageURI.value!!) {
                        updatedReviewCallback()
                    }
                } else {
                    updatedReviewCallback()
                }
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