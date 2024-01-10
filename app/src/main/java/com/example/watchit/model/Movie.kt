package com.example.watchit.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Number,
    val title: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String
)