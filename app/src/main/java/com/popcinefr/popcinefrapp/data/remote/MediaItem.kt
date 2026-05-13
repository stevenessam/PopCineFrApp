package com.popcinefr.popcinefrapp.data.remote

// A unified model that can represent either a movie or a series
// This is used for the mixed trending section on the home screen
data class MediaItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String?,
    val mediaType: String  // "movie" or "series"
)

// Extension functions to convert DTOs to MediaItem
fun MovieDto.toMediaItem() = MediaItem(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    mediaType = "movie"
)

fun SeriesDto.toMediaItem() = MediaItem(
    id = id,
    title = name,
    posterPath = posterPath,
    backdropPath = backdropPath,
    voteAverage = voteAverage,
    releaseDate = firstAirDate,
    mediaType = "series"
)