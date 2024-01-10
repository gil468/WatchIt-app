package com.example.watchit.model

import com.google.firebase.Timestamp

data class PublishReviewDTO(
    val score: Double? = null,
    val userId: String? = null,
    val text: String? = null,
    val date: Timestamp? = null,
    val movieId: Int? = null
)
