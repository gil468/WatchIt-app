package com.example.watchit.data

import com.example.watchit.data.review.Review

class Model private constructor() {

    val reviews: MutableList<Review> = mutableListOf()

    companion object {
        val instance: Model = Model()
    }

    init {
        for (i in 0..10) {
            val review = Review(
                "id",
                5.0,
                "userId",
                0,
                "what an amazing movie",
                "The Hunger Games",
                "https://image.tmdb.org/t/p/w500/iTcNyLdz5FrnUtd9hkAkdE0WOO1.jpg"
            )
            reviews.add(review)
        }
    }
}