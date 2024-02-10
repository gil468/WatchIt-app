package com.example.watchit.modules.myReviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class MyReviewsViewModel : ViewModel() {
    val reviews: LiveData<MutableList<Review>> = Model.instance.getMyReviews()
    val user: LiveData<User> = Model.instance.getCurrentUser()
    val reviewsListLoadingState: MutableLiveData<Model.LoadingState> =
        Model.instance.reviewsListLoadingState

    fun reloadData() {
        Model.instance.refreshAllUsers()
        Model.instance.refreshAllReviews()
    }
}