package com.example.watchit.modules.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class FeedViewModel : ViewModel() {
    var reviews: LiveData<MutableList<Review>> = Model.instance.getAllReviews()
    var users: LiveData<MutableList<User>> = Model.instance.getAllUsers()

    fun reloadData() {
        Model.instance.refreshAllUsers()
        Model.instance.refreshAllReviews()
    }
}