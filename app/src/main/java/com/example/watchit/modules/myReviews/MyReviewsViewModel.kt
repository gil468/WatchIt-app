package com.example.watchit.modules.myReviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class MyReviewsViewModel : ViewModel() {
    var reviews: LiveData<MutableList<Review>>? = null
    var user: LiveData<User>? = null
}