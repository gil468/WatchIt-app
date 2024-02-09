package com.example.watchit.modules.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class FeedViewModel : ViewModel() {
    var reviews: LiveData<MutableList<Review>>? = null
    var users: LiveData<MutableList<User>>? = null
}