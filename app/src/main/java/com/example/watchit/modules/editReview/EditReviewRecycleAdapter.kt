package com.example.watchit.modules.editReview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.review.Review

class EditReviewRecycleAdapter(var description: String, var score: Double, var reviewImageUri: String?, var movieName: String, var userId: String) :
    RecyclerView.Adapter<EditReviewViewHolder>() {

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_edit_review, parent, false)
        return EditReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EditReviewViewHolder, position: Int) {
        val review = Review(description, score, reviewImageUri.toString(), movieName, userId)
        holder.bind(review)
    }
}