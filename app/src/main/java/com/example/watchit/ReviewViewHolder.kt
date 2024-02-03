package com.example.watchit

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.data.review.Review
import com.squareup.picasso.Picasso

class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val reviewImageView: ImageView?
    val profileImageView: ImageView?
    val profileName: TextView?
    val movieName: TextView?
    val reviewDescription: TextView?
    val reviewRating: TextView?


    init {
        reviewImageView = itemView.findViewById(R.id.CardImage)
        profileImageView = itemView.findViewById(R.id.ProfileImageView)
        profileName = itemView.findViewById(R.id.ProfileName)
        movieName = itemView.findViewById(R.id.MovieName)
        reviewDescription = itemView.findViewById(R.id.ReviewDescription)
        reviewRating = itemView.findViewById(R.id.ReviewRating)
    }

    fun bind(review: Review) {
        Picasso.get().load(review.reviewImage)
            .into(reviewImageView)
        Picasso.get().load(review.reviewImage)
            .into(profileImageView)
        profileName?.text = review.userId
        movieName?.text = review.movieName
        reviewDescription?.text = review.description
        reviewRating?.text = "Rating: ${review.score} ★"
    }
}