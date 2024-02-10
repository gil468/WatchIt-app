package com.example.watchit.modules.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.data.movie.Movie

class SearchRecycleAdapter(var movies: MutableList<Movie>?) :
    RecyclerView.Adapter<MovieSearchResultViewHolder>() {

    override fun getItemCount(): Int {
        return movies?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchResultViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_search_result, parent, false)
        return MovieSearchResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieSearchResultViewHolder, position: Int) {
        val movie = movies?.get(position)
        holder.bind(movie)
    }
}