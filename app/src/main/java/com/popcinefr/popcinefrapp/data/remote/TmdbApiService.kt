package com.popcinefr.popcinefrapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
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


    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
        // This tells TMDB to include videos and credits in the same response
        // Instead of making 3 separate API calls we get everything in one go
        @Query("append_to_response") appendToResponse: String = "videos,credits"
    ): MovieDetailDto

    @GET("tv/{series_id}")
    suspend fun getSeriesDetail(
        @Path("series_id") seriesId: Int,
        @Query("language") language: String = "en-US",
        @Query("append_to_response") appendToResponse: String = "videos,credits"
    ): SeriesDetailDto
}

