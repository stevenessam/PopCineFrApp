package com.popcinefr.popcinefrapp.data.remote

import com.google.gson.annotations.SerializedName

// This represents a single movie from the API
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,

    // "poster_path" in JSON → posterPath in Kotlin
    // @SerializedName tells Gson how to match the JSON field name
    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?
)

// This represents the full API response which wraps a list of movies
data class MoviesResponseDto(
    val results: List<MovieDto>,
    val page: Int,

    @SerializedName("total_pages")
    val totalPages: Int
)