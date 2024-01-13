package com.example.watchit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.watchit.model.Movie
import com.squareup.picasso.Picasso

class MovieActivity : ComponentActivity() {
    private lateinit var movie: Movie
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_main)

        defineMovie()
        loadMovieDetails()
        addAddReviewOnClickHandler()
        //fetchReviews()
    }

//    private fun fetchReviews() {
//        Firebase.firestore.collection("reviews")
//            .whereEqualTo("movieId", movie.id)
//            .orderBy("date", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener(::showMovieReviews)
//            .addOnFailureListener {
//                Log.e("reviews", it.message, it)
//                Toast.makeText(
//                    this@MovieActivity,
//                    "Error processing reviews",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }

    private fun addAddReviewOnClickHandler() {
        findViewById<Button>(R.id.addReview).setOnClickListener {
            val intent = Intent(this@MovieActivity, ReviewActivity::class.java)
            intent.putExtra("movieId", movie.id)
            startActivity(intent)
        }
    }

    private fun defineMovie() {
        movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("movie", Movie::class.java)!!
        } else {
            intent.getSerializableExtra("movie") as Movie
        }
    }

//    private fun showMovieReviews(reviews: QuerySnapshot) {
//        val reviewsLayout: LinearLayout = findViewById(R.id.reviewsLayout)
//
//        for (reviewDocument in reviews) {
//            val review = reviewDocument.toObject<PublishReviewDTO>()
//
//            val layout = LinearLayout(this)
//            layout.orientation = LinearLayout.VERTICAL
//            val imageView = ImageView(this)
//
//            Firebase.storage.reference.child("images/reviews/${reviewDocument.id}")
//                .downloadUrl
//                .addOnSuccessListener {
//                    Picasso.get().load(it).into(imageView)
//                }.addOnFailureListener {
//                    //todo
//                }
//
//            val layoutParams = LinearLayout.LayoutParams(800, 250)
//                .apply {
//                    weight = 1.0f
//                    gravity = Gravity.CENTER
//                }
//            layoutParams.topMargin = 50
//
//            imageView.layoutParams = layoutParams
//
//            val reviewText = TextView(this)
//            val params = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                weight = 1.0f
//                gravity = Gravity.CENTER
//            }
//            reviewText.textSize = 20f
//            reviewText.text = review.text
//            reviewText.layoutParams = params
//
//            val score = TextView(this)
//            score.textSize = 20f
//            score.text = getString(R.string.rating, review.score)
//            score.layoutParams = params
//
//
//            layout.addView(imageView)
//            layout.addView(reviewText)
//            layout.addView(score)
//
//            reviewsLayout.addView(layout)
//        }
//    }

    private fun loadMovieDetails() {
        val movieTitle: TextView = findViewById(R.id.movieTitle)
        movieTitle.text = movie.title

        val moviePoster: ImageView = findViewById(R.id.moviePoster)

        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500${movie.backdropPath}")
            .into(moviePoster)

        val movieDescription: TextView = findViewById(R.id.movieDescription)
        movieDescription.text = movie.overview
        movieDescription.movementMethod = ScrollingMovementMethod()
    }
}