package com.example.watchit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class Feed : Fragment() {
    private lateinit var reviewsLayout: RecyclerView
    private lateinit var root: View
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private var reviews: MutableList<Review> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_feed, container, false)


        reviews = Model.instance.reviews

        reviewsLayout = root.findViewById(R.id.reviewsFeed)
        reviewsLayout.setHasFixedSize(true)

        reviewsLayout.layoutManager = LinearLayoutManager(context)
        reviewsLayout.adapter = ReviewsRecycleAdapter(reviews)

        //fetchReviews()

        return root
    }

    //region Fetch Reviews
//    private fun fetchReviews() {
//        Firebase.firestore
//            .collection("reviews")
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { reviewsDocuments ->
//                Log.d("asd", "Reviews fetched, ${reviewsDocuments.size()}")
//                reviewsDocuments.forEach { reviewDocument ->
//                    val review = reviewDocument.toObject<PublishReviewDTO>()
//                    val reviewId = reviewDocument.id
//
//                    runBlocking { addReviewToFeed(review, reviewId) }
//                    Log.d("asd", "Review added to feed")
//                }
//            }
//    }
//
//    private suspend fun addReviewToFeed(
//        review: PublishReviewDTO,
//        reviewId: String
//    ) {
//        val userId = review.userId!!
//
//        val userTask = db.collection("users")
//            .document(userId)
//            .get()
//
//        val userImageTask = storage.reference.child("images/users/$userId")
//            .downloadUrl
//
//        val reviewImageTask = storage.reference.child("images/reviews/$reviewId")
//            .downloadUrl
//
//
//        val user = userTask
//            .await()
//            .toObject<PublishUserDTO>()!!
//
//        val reviewDescription = review.description!!
//        val reviewRating = review.score!!
//        //val Movie
//        val movieName = review.movieName!!
//        val date = review.timestamp
//        val userFullName = "${user.firstName} ${user.lastName}"
//
//        val userImage = userImageTask
//            .await()
//
//        val reviewImage = reviewImageTask
//            .await()
//
//        val reviewCardRoot = layoutInflater.inflate(R.layout.reviews_feed_card, null)
//
//        populateReviewCard(
//            movieName,
//            userFullName,
//            userImage,
//            reviewDescription,
//            reviewRating,
//            reviewImage,
//            reviewCardRoot
//        )
//        reviewsLayout.addView(reviewCardRoot)
//    }
//
//    private fun populateReviewCard(
//        movieName: String,
//        userFullName: String,
//        userImage: Uri,
//        reviewDescription: String,
//        reviewRating: Double,
//        reviewImage: Uri,
//        reviewCardRoot: View
//    ) {
//
//        Picasso.get().load(reviewImage).into(reviewCardRoot.findViewById<ImageView>(R.id.CardImage))
//        Picasso.get().load(userImage)
//            .into(reviewCardRoot.findViewById<ImageView>(R.id.ProfileImageView))
//        reviewCardRoot.findViewById<TextView>(R.id.ProfileName).text = userFullName
//        reviewCardRoot.findViewById<TextView>(R.id.MovieName).text = movieName
//        reviewCardRoot.findViewById<TextView>(R.id.ReviewDescription).text = reviewDescription
//        reviewCardRoot.findViewById<TextView>(R.id.ReviewRating).text = "Rating: $reviewRating ★"
//    }

    //endregion

}