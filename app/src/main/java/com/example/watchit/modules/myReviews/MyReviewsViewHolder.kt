package com.example.watchit.modules.myReviews

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class MyReviewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val reviewImageView: ImageView?
    val profileImageView: ImageView?
    val profileName: TextView?
    val movieName: TextView?
    val reviewDescription: TextView?
    val reviewRating: TextView?
    val editButton: Button
    val deleteButton: Button


    init {
        reviewImageView = itemView.findViewById(R.id.CardImage)
        profileImageView = itemView.findViewById(R.id.ProfileImageView)
        profileName = itemView.findViewById(R.id.ProfileName)
        movieName = itemView.findViewById(R.id.MovieName)
        reviewDescription = itemView.findViewById(R.id.ReviewDescription)
        reviewRating = itemView.findViewById(R.id.ReviewRating)
        editButton = itemView.findViewById(R.id.EditButton)
        deleteButton = itemView.findViewById(R.id.DeleteButton)
    }

    @SuppressLint("SetTextI18n")
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
        deleteButton.setOnClickListener{
            MaterialAlertDialogBuilder(itemView.context)
                .setTitle("Delete Review")
                .setMessage("Do you want to delete this Review?")
                .setNeutralButton("Cancel") { dialog, which ->
                }
                .setPositiveButton("Delete") { dialog, which ->
                    Model.instance.deleteReview(review) {
                        Toast.makeText(
                            itemView.context,
                            "Review deleted!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .show()
        }
        editButton.setOnClickListener{
            val action = MyReviewsDirections.actionMyReviewsToEditReview(review!!)
            Navigation.findNavController(itemView).navigate(action)
        }
    }
}