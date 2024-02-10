package com.example.watchit.modules.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.movie.Movie
import com.example.watchit.services.MovieServiceClient

class SearchViewModel : ViewModel() {
    var movies: MutableLiveData<MutableList<Movie>> = MutableLiveData()

    fun refreshMovies(query: String) {
        MovieServiceClient.instance.searchMovies(query) {
            movies.postValue(it)
        }
    }

    fun clearMovies() {
        movies.postValue(mutableListOf())
    }
}