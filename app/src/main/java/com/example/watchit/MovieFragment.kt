package com.example.watchit

import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.watchit.model.Movie
import com.squareup.picasso.Picasso


class MovieFragment : Fragment() {

    private val args by navArgs<MovieFragmentArgs>()
    private lateinit var movie: Movie
    private lateinit var root: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movie = args.selectedMovie

//        arguments?.let {
//            movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                it.getSerializable("movie") as? Movie ?: throw IllegalArgumentException("Invalid movie type")
//            } else {
//                @Suppress("DEPRECATION")
//                it.getSerializable("movie") as? Movie ?: throw IllegalArgumentException("Invalid movie type")
//            }
//        }
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
        val movieId = movie.id
        val movieName = movie.title
//        val moviePoster = "https://image.tmdb.org/t/p/w500${movie.backdropPath}"
        val fragment = NewReview()
        val bundle = Bundle()

        bundle.putInt("movieId", movieId)
        bundle.putString("movieName", movieName)
//        bundle.putString("moviePoster", moviePoster)
        fragment.arguments = bundle

        val action = MovieFragmentDirections.actionMovieFragmentToNewReview(movie)
        findNavController().navigate(action)

        Navigation.findNavController(root).navigate(R.id.action_movieFragment_to_newReview)

//        activity?.supportFragmentManager?.beginTransaction()?.apply {
//            replace(R.id.FragmentLayout, fragment)
//            addToBackStack(null)
//            commit()
//        }
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