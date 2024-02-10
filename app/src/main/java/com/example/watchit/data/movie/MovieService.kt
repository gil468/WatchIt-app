package com.example.watchit.data.movie

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieService {
    @GET("search/movie")
    fun searchMovies(
        @Query("query") query: String,
        @Header("Authorization") authorization: String
    ): Call<MovieApiResponse>
}
