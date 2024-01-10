package com.example.watchit.model

import com.google.firebase.Timestamp

data class PublishReviewDTO(
    val score: Double,
    val userId: String,
    val text: String,
    val date: Timestamp,
    val movieId: Int
)
