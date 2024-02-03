package com.example.watchit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.data.review.Review

class ReviewsRecycleAdapter(private val data: MutableList<Review>) :
    RecyclerView.Adapter<ReviewViewHolder>() {
    override fun getItemCount(): Int {
        return data.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reviews_feed_card, parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = data[position]
        holder.bind(review)
    }
}