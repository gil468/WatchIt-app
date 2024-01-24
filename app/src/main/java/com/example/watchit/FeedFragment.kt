package com.example.watchit

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.watchit.model.PublishReviewDTO
import com.example.watchit.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FeedFragment : Fragment() {
    private lateinit var reviewsLayout: LinearLayout
    private lateinit var root: View
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_feed, container, false)

        reviewsLayout = root.findViewById(R.id.reviewsFeed)
        fetchReviews(root)

        return root
    }

    private fun fetchReviews(root: View) {
        Firebase.firestore
            .collection("reviews")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { reviewsDocuments ->
                reviewsDocuments.forEach { reviewDocument ->
                    val review = reviewDocument.toObject<PublishReviewDTO>()
                    val reviewId = reviewDocument.id

                    runBlocking { addReviewToFeed(review, reviewId, root) }
                }
            }
    }

    private suspend fun addReviewToFeed(
        review: PublishReviewDTO,
        reviewId: String,
        root: View
    ) {
        val userId = review.userId!!
        val user = db.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject<User>()!!

        val reviewDescription = review.description!!
        val reviewRating = review.rating!!
        //val Movie
        val date = review.timestamp
        val userFullName = "${user.firstName} ${user.lastName}"
        val userImage = storage.reference.child("images/users/$userId")
            .downloadUrl
            .await()
        val reviewImage = storage.reference.child("images/reviews/$reviewId")
            .downloadUrl
            .await()

        addReviewToLayout(userFullName, userImage, reviewDescription, reviewRating, reviewImage, root)
    }

    private fun addReviewToLayout(
        userFullName: String,
        userImage: Uri,
        reviewDescription: String,
        reviewRating: Double,
        reviewImage: Uri,
        root: View
    ) {
        val reviewLayout = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#EFEFEF"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
                .apply { setMargins(0, 20, 0, 0) }
        }

        val userLayout = LinearLayout(root.context)

        val userImageView = ImageView(root.context)
            .apply { layoutParams = LinearLayout.LayoutParams(150, 150) }

        Picasso.get().load(userImage).into(userImageView)

        val userNameView = TextView(root.context).apply {
            text = userFullName
            textSize = 16f
            setTextColor(Color.parseColor("#000000"))
            typeface = Typeface.DEFAULT_BOLD
        }
        val userNameLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0f
            gravity = Gravity.CENTER
        }
        userNameLayoutParams.setMargins(20, 0, 0, 0)
        userNameView.layoutParams = userNameLayoutParams

        val reviewFollowButton = Button(root.context).apply {
            text = "Follow"
            textSize = 20f
            setTextColor(Color.parseColor("#FFFF7B00"))
            layoutParams = LinearLayout.LayoutParams(420, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 20, 20, 0) }
            setBackgroundResource(R.drawable.mybutton)
        }

        userLayout.addView(userImageView)
        userLayout.addView(userNameView)
        userLayout.addView(reviewFollowButton)

        reviewLayout.addView(userLayout)

        val reviewImageView = ImageView(root.context)
        val reviewImageLayoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500)
        reviewImageLayoutParams.setMargins(0, 20, 0, 20)
        reviewImageView.layoutParams = reviewImageLayoutParams

        Picasso.get().load(reviewImage).into(reviewImageView)

        reviewLayout.addView(reviewImageView)

        val reviewTextView = TextView(root.context).apply {
            text = reviewDescription
            textSize = 18f
            setTextColor(Color.parseColor("#000000"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 20
            }
        }

        reviewLayout.addView(reviewTextView)

        val reviewRatingView = TextView(root.context).apply {
            text = "Rating: $reviewRating ★"
            textSize = 20f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 20
            }
        }

        reviewLayout.addView(reviewRatingView)

        val buttonsLayout = LinearLayout(root.context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val reviewLikeButton = Button(root.context).apply {
            text = "Like"
            textSize = 20f
            setTextColor(Color.parseColor("#FFFF7B00"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(20, 20, 20, 0) }
            setBackgroundResource(R.drawable.mybutton)
        }

        buttonsLayout.addView(reviewLikeButton)

        val reviewCommentButton = Button(root.context).apply {
            text = "Comment"
            textSize = 20f
            setTextColor(Color.parseColor("#FFFF7B00"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 20, 20, 0) }
            setBackgroundResource(R.drawable.mybutton)
        }

        buttonsLayout.addView(reviewCommentButton)

        val reviewAddToWatchlistButton = Button(root.context).apply {
            text = "Add to list"
            textSize = 20f
            setTextColor(Color.parseColor("#FFFF7B00"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 20, 20, 0) }
            setBackgroundResource(R.drawable.mybutton)
        }

        buttonsLayout.addView(reviewAddToWatchlistButton)

        reviewLayout.addView(buttonsLayout)

        reviewsLayout.addView(reviewLayout)
    }
}