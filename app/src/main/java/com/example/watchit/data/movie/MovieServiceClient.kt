package com.example.watchit.data.movie

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder

class MovieServiceClient private constructor() {

    companion object {
        val instance: MovieServiceClient = MovieServiceClient()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiKey =
        "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlNjhiZTA1NzNhMzY2MTBjMmFhZjMzZDI2NjYxMGMwMSIsInN1YiI6IjY1OWIwM2E4N2Q1NTA0MDI2MTdhMjA4YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.94z8lgf6EOsxtgyOVyjb_I9AzU_dV5ZUicdRjR6S0EM"

    private val movieService = retrofit.create(MovieService::class.java)

    fun searchMovies(
        query: String,
        callback: (MutableList<Movie>) -> Unit
    ) {

        val encodedSearch = URLEncoder.encode(query, "UTF-8")
        val searchMovies = movieService.searchMovies(encodedSearch, "Bearer $apiKey")

        searchMovies.enqueue(object : retrofit2.Callback<MovieApiResponse> {
            override fun onResponse(
                call: retrofit2.Call<MovieApiResponse>,
                response: retrofit2.Response<MovieApiResponse>
            ) {
                if (response.isSuccessful) {
                    val moviesList = response.body()?.results
                        ?.filter { movie -> movie.popularity > 30 }
                        ?.sortedByDescending { movie -> movie.popularity }
                    callback(moviesList as? MutableList<Movie> ?: mutableListOf())
                } else {
                    throw Exception("Failed to fetch movies")
                }
            }

            override fun onFailure(call: retrofit2.Call<MovieApiResponse>, t: Throwable) {
                throw t
            }
        })
    }
}