package com.example.watchit.modules.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class FeedViewModel : ViewModel() {
    val reviews: LiveData<MutableList<Review>> = Model.instance.getAllReviews()
    val users: LiveData<MutableList<User>> = Model.instance.getAllUsers()
    val reviewsListLoadingState: MutableLiveData<Model.LoadingState> =
        Model.instance.reviewsListLoadingState

    fun reloadData() {
        Model.instance.refreshAllUsers()
        Model.instance.refreshAllReviews()
    }
}