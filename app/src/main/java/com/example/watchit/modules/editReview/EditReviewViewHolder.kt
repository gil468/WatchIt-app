package com.example.watchit.modules.editReview

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User
import com.squareup.picasso.Picasso

class EditReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val movieImageView: ImageView?
    private val reviewRating: TextView?
    private val updateButton: Button

    init {
        movieImageView = itemView.findViewById(R.id.movieImageView)
        reviewRating = itemView.findViewById(R.id.ReviewRating)
        updateButton = itemView.findViewById(R.id.updateButton)
    }

    @SuppressLint("SetTextI18n")
    fun bind(review: Review?) {
        Picasso.get()
            .load(review?.reviewImage)
            .into(movieImageView)
        reviewRating?.text = "Rating: ${review?.score} ★"
        updateButton.setOnClickListener{
            Model.instance.updateReview(review) {
                Toast.makeText(
                    itemView.context,
                    "Review updated!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}