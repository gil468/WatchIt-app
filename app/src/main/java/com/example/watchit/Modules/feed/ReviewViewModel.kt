package com.example.watchit.Modules.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class ReviewViewModel : ViewModel() {
    var reviews: LiveData<MutableList<Review>>? = null
    var users: LiveData<MutableList<User>>? = null
}