package com.example.watchit.modules.feed

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User
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

    fun bind(review: Review?, reviewer: User?) {
        Log.d("TAG", "review ${review?.score}")
        Picasso.get()
            .load(review?.reviewImage)
            .into(reviewImageView)
        Picasso.get()
            .load(reviewer?.profileImage)
            .into(profileImageView)
        val reviewerName = "${reviewer?.firstName ?: ""} ${reviewer?.lastName ?: ""}"
        profileName?.text = reviewerName
        movieName?.text = review?.movieName
        reviewDescription?.text = review?.description
        reviewRating?.text = "Rating: ${review?.score} ★"
    }
}