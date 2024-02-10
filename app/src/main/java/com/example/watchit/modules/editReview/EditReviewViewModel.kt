package com.example.watchit.modules.editReview

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class EditReviewViewModel : ViewModel() {
    var imageChanged = false
    var ratingBarChanged = false
    var descriptionChanged = false
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var review: Review? = null

    var description: String? = null
    var rating: Int? = null
    var descriptionError = MutableLiveData("")
    var ratingError = MutableLiveData("")

    fun updateReview(
        invalidReviewCallback: () -> Unit,
        updatedReviewCallback: () -> Unit
    ) {
        if (validateReviewUpdate()) {
            this.viewModelScope.launch {
                val dataUpdate = async {
                    if (ratingBarChanged || descriptionChanged) updateReviewData()
                }
                val imageUpdate = async { if (imageChanged) updateReviewImage() }

                awaitAll(dataUpdate, imageUpdate)

                updatedReviewCallback()
            }


        } else {
            invalidReviewCallback()
        }
    }

    fun loadReview(review: Review) {
        this.review = review
        this.description = review.description
        this.rating = review.score

        Model.instance.getReviewImage(review.id) {
            selectedImageURI.postValue(it)
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

    private fun updateReviewData() {
        val updatedReview = Review(
            review!!.id,
            rating!!,
            review!!.userId,
            description!!,
            review!!.movieName
        )

        Model.instance.updateReview(updatedReview)
    }

    private fun updateReviewImage() {
        Model.instance.updateReviewImage(review!!.id, selectedImageURI.value!!)
    }
}