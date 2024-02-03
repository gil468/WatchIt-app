package com.example.watchit.modules.myReviews

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User
import com.example.watchit.modules.feed.ReviewViewHolder

class MyReviewsRecycleAdapter(var reviews: MutableList<Review>?, var user: User?) :
    RecyclerView.Adapter<ReviewViewHolder>() {

    override fun getItemCount(): Int {
        return reviews?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reviews_feed_card, parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews?.get(position)
        Log.d("TAG", "reviews size ${reviews?.size}")
        holder.bind(review, user)
    }
}