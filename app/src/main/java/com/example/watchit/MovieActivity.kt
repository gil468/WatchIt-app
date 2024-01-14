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
    }

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