package com.example.watchit.modules.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.movie.Movie
import com.squareup.picasso.Picasso

class MovieSearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val movieImageView: ImageView?
    private val movieTitleView: TextView?

    init {
        movieImageView = itemView.findViewById(R.id.movieResultImage)
        movieTitleView = itemView.findViewById(R.id.movieResultTitle)
    }

    fun bind(movie: Movie?) {
        if (movie == null) {
            return
        }
        itemView.setOnClickListener {
            val action = SearchDirections.actionSearchToMovieFragment(movie)
            Navigation.findNavController(itemView).navigate(action)
        }

        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(movieImageView)
        movieTitleView?.text = movie.title
    }
}