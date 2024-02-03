package com.example.watchit.Modules.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.watchit.R
import com.example.watchit.data.review.PublishReviewDTO
import com.example.watchit.data.user.PublishUserDTO
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class MyReviews : Fragment() {
    private lateinit var reviewsLayout: LinearLayout
    private lateinit var root: View
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_my_reviews, container, false)

        reviewsLayout = root.findViewById(R.id.myReviewsFeed)
        fetchReviews()

        return root
    }

    private fun fetchReviews() {
        Firebase.firestore
            .collection("reviews")
            .whereEqualTo("userId", auth.currentUser?.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { reviewsDocuments ->
                reviewsDocuments.forEach { reviewDocument ->
                    val review = reviewDocument.toObject<PublishReviewDTO>()
                    val reviewId = reviewDocument.id

                    runBlocking { addReviewToFeed(review, reviewId) }
                }
            }
    }

    private suspend fun addReviewToFeed(
        review: PublishReviewDTO,
        reviewId: String
    ) {
        val userId = review.userId!!
        val user = db.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject<PublishUserDTO>()!!

        val reviewDescription = review.description!!
        val reviewRating = review.score!!
        //val Movie
        val movieName = review.movieName!!
        val date = review.timestamp
        val userFullName = "${user.firstName} ${user.lastName}"
        val userImage = storage.reference.child("images/users/$userId")
            .downloadUrl
            .await()
        val reviewImage = storage.reference.child("images/reviews/$reviewId")
            .downloadUrl
            .await()

        val reviewCardRoot = layoutInflater.inflate(R.layout.my_reviews_feed_card, null)

        populateReviewCard(
            movieName,
            userFullName,
            userImage,
            reviewDescription,
            reviewRating,
            reviewImage,
            reviewCardRoot
        )
        reviewsLayout.addView(reviewCardRoot)
    }

    private fun populateReviewCard(
        movieName: String,
        userFullName: String,
        userImage: Uri,
        reviewDescription: String,
        reviewRating: Double,
        reviewImage: Uri,
        reviewCardRoot: View
    ) {
        Picasso.get().load(reviewImage).into(reviewCardRoot.findViewById<ImageView>(R.id.CardImage))
        Picasso.get().load(userImage)
            .into(reviewCardRoot.findViewById<ImageView>(R.id.ProfileImageView))
        reviewCardRoot.findViewById<TextView>(R.id.ProfileName).text = userFullName
        reviewCardRoot.findViewById<TextView>(R.id.MovieName).text = movieName
        reviewCardRoot.findViewById<TextView>(R.id.ReviewDescription).text = reviewDescription
        reviewCardRoot.findViewById<TextView>(R.id.ReviewRating).text = "Rating: $reviewRating ★"
    }
}