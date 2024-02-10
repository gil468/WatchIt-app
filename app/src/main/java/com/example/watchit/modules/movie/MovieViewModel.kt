package com.example.watchit.modules.movie

import androidx.lifecycle.ViewModel
import com.example.watchit.data.movie.Movie

class MovieViewModel : ViewModel() {
    var movieDetailsData: Movie? = null

    fun setMovieDetails(movie: Movie) {
        movieDetailsData = movie
    }
}