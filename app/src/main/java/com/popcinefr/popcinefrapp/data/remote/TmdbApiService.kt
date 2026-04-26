package com.popcinefr.popcinefrapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    // Trending movies this week
    // This becomes: GET https://api.themoviedb.org/3/trending/movie/week
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("language") language: String = "en-US"
    ): MoviesResponseDto

    // Trending series this week
    @GET("trending/tv/week")
    suspend fun getTrendingSeries(
        @Query("language") language: String = "en-US"
    ): SeriesResponseDto

    // Search — works for both movies and series
    // ?query=batman  ← this is what @Query("query") adds
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = "en-US"
    ): MoviesResponseDto

    @GET("search/tv")
    suspend fun searchSeries(
        @Query("query") query: String,
        @Query("language") language: String = "en-US"
    ): SeriesResponseDto
}