package com.example.watchit.modules.myReviews

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class MyReviewsRecycleAdapter(var reviews: MutableList<Review>?, var user: User?) :
    RecyclerView.Adapter<MyReviewsViewHolder>() {

    override fun getItemCount(): Int {
        return reviews?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReviewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_my_reviews_card, parent, false)
        return MyReviewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyReviewsViewHolder, position: Int) {
        val review = reviews?.get(position)
        Log.d("TAG", "reviews size ${reviews?.size}")
        holder.bind(review, user, {
            val action = MyReviewsDirections.actionMyReviewsToEditReview(review!!)
            Navigation.findNavController(holder.itemView).navigate(action)
        },
            {
                Model.instance.deleteReview(review) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Review deleted!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}