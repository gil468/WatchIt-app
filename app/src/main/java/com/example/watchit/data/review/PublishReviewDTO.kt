package com.example.watchit.data.review

import com.google.firebase.Timestamp

data class PublishReviewDTO(
    val movieName: String? = null,
    val score: Double? = null,
    val userId: String? = null,
    val description: String? = null,
    val timestamp: Timestamp? = null,
    val movieId: Int? = null,
    val isDeleted: Boolean = false
)
