package com.example.watchit.modules.search

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.watchit.R
import com.example.watchit.data.movie.Movie
import com.squareup.picasso.Picasso


class MovieFragment : Fragment() {

    private val args by navArgs<MovieFragmentArgs>()
    private lateinit var movie: Movie
    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movie = args.selectedMovie
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_movie, container, false)

        loadMovieDetails(root)
        root.findViewById<Button>(R.id.addReview).setOnClickListener {
            addAddReviewOnClickHandler()
        }

        return root
    }

    private fun addAddReviewOnClickHandler() {
        val action = MovieFragmentDirections.actionMovieFragmentToNewReview(movie)
        findNavController().navigate(action)
    }

    private fun loadMovieDetails(root: View) {
        val movieTitle: TextView = root.findViewById(R.id.movieTitle)
        movieTitle.text = movie.title

        val moviePoster: ImageView = root.findViewById(R.id.moviePoster)

        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500${movie.backdropPath}")
            .into(moviePoster)

        val movieDescription: TextView = root.findViewById(R.id.movieDescription)
        movieDescription.text = movie.overview
        movieDescription.movementMethod = ScrollingMovementMethod()
    }
}