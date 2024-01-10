package com.example.watchit.model

import java.time.ZonedDateTime

data class Review(
    val id: String,
    val score: Double,
    val userId: String,
    val text: String,
    val date: ZonedDateTime,
    val movie: Movie
)
