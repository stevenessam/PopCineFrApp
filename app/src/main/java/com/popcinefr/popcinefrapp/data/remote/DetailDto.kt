package com.popcinefr.popcinefrapp.data.remote

import com.google.gson.annotations.SerializedName

// --- Shared ---

data class GenreDto(
    val id: Int,
    val name: String
)

data class VideoDto(
    val key: String,       // YouTube video key e.g. "abc123"
    val site: String,      // "YouTube", "Vimeo", etc.
    val type: String       // "Trailer", "Teaser", "Clip", etc.
)

data class VideosWrapperDto(
    val results: List<VideoDto>
)

data class CastMemberDto(
    val id: Int,
    val name: String,

    @SerializedName("character")
    val character: String,

    @SerializedName("profile_path")
    val profilePath: String?
)

data class CreditsDto(
    val cast: List<CastMemberDto>
)

// --- Movie Detail ---

data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("runtime")
    val runtime: Int?,          // duration in minutes

    val genres: List<GenreDto>,
    val videos: VideosWrapperDto,
    val credits: CreditsDto
)

// --- Series Detail ---

data class SeriesDetailDto(
    val id: Int,
    val name: String,           // series uses "name" not "title"
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("first_air_date")
    val firstAirDate: String?,

    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int?,

    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int?,

    val genres: List<GenreDto>,
    val videos: VideosWrapperDto,
    val credits: CreditsDto
)