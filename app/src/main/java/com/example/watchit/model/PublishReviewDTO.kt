package com.example.watchit.model

import com.google.firebase.Timestamp

data class PublishReviewDTO(
    val movieName: String? = null,
    val rating: Double? = null,
    val userId: String? = null,
    val description: String? = null,
    val timestamp: Timestamp? = null,
    val movieId: Int? = null
)
