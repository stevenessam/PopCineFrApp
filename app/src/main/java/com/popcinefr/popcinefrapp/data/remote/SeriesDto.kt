package com.popcinefr.popcinefrapp.data.remote

import com.google.gson.annotations.SerializedName

// Series is almost identical to Movie but uses "name" instead of "title"
data class SeriesDto(
    val id: Int,
    val name: String,           // ← "name" not "title" for series
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("first_air_date")
    val firstAirDate: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?
)

data class SeriesResponseDto(
    val results: List<SeriesDto>,
    val page: Int,

    @SerializedName("total_pages")
    val totalPages: Int
)