package com.example.watchit.data.review

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Review(
    @PrimaryKey
    val id: String,
    val score: Double,
    val userId: String,
    val timestamp: Long,
    val description: String,
    val movieName: String,
    val reviewImage: String
)