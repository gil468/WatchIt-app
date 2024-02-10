package com.example.watchit.modules.myReviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.review.Review
import com.example.watchit.data.review.ReviewModel
import com.example.watchit.data.user.User
import com.example.watchit.data.user.UserModel

class MyReviewsViewModel : ViewModel() {
    val reviews: LiveData<MutableList<Review>> = ReviewModel.instance.getMyReviews()
    val user: LiveData<User> = UserModel.instance.getCurrentUser()
    val reviewsListLoadingState: MutableLiveData<ReviewModel.LoadingState> =
        ReviewModel.instance.reviewsListLoadingState

    fun reloadData() {
        UserModel.instance.refreshAllUsers()
        ReviewModel.instance.refreshAllReviews()
    }
}