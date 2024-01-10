package com.example.watchit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.watchit.model.Movie
import com.example.watchit.model.PublishReviewDTO
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso

class MovieActivity : ComponentActivity() {
    private lateinit var movie: Movie
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_main)

        movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("movie", Movie::class.java)!!
        } else {
            intent.getSerializableExtra("movie") as Movie
        }

        val movieTitle: TextView = findViewById(R.id.movieTitle)
        movieTitle.text = movie.title

        val moviePoster: ImageView = findViewById(R.id.moviePoster)

        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500${movie.backdropPath}")
            .into(moviePoster)

        findViewById<Button>(R.id.addReview).setOnClickListener {
            val intent = Intent(this@MovieActivity, ReviewActivity::class.java)
            intent.putExtra("movieId", movie.id)
            startActivity(intent)
        }

        val reviewsLayout: LinearLayout = findViewById(R.id.reviewsLayout)

        Firebase.firestore.collection("reviews")
            .whereEqualTo("movieId", movie.id)
            .orderBy("date",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { reviews ->
                for (reviewDocument in reviews) {
                    val review = reviewDocument.toObject<PublishReviewDTO>()

                    val layout = LinearLayout(this)
                    layout.orientation = LinearLayout.VERTICAL
                    val imageView = ImageView(this)

                    Firebase.storage.reference.child("images/reviews/${reviewDocument.id}")
                        .downloadUrl
                        .addOnSuccessListener {
                            Picasso.get().load(it).into(imageView)
                        }.addOnFailureListener {
                            //todo
                        }

                    val layoutParams = LinearLayout.LayoutParams(250, 250)
                        .apply {
                            weight = 1.0f
                            gravity = Gravity.CENTER
                        }
                    layoutParams.topMargin = 50

                    imageView.layoutParams = layoutParams

                    val reviewText = TextView(this)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1.0f
                        gravity = Gravity.CENTER
                    }
                    reviewText.textSize = 20f
                    reviewText.text = review.text
                    reviewText.layoutParams = params

                    val score = TextView(this)
                    score.textSize = 20f
                    score.text = getString(R.string.rating, review.score)
                    score.layoutParams = params


                    layout.addView(imageView)
                    layout.addView(reviewText)
                    layout.addView(score)

                    reviewsLayout.addView(layout)
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@MovieActivity,
                    "Error processing result",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}