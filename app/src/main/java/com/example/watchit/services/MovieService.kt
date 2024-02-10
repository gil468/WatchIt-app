package com.example.watchit.services

import com.example.watchit.data.movie.MovieApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieService {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Header("Authorization") authorization: String
    ): MovieApiResponse
}
