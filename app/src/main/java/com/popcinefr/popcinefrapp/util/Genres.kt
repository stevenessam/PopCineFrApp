package com.popcinefr.popcinefrapp.util

data class Genre(val id: Int, val name: String)

// These IDs are fixed by TMDB — they never change
val movieGenres = listOf(
    Genre(28, "Action"),
    Genre(12, "Adventure"),
    Genre(16, "Animation"),
    Genre(35, "Comedy"),
    Genre(80, "Crime"),
    Genre(99, "Documentary"),
    Genre(18, "Drama"),
    Genre(10751, "Family"),
    Genre(14, "Fantasy"),
    Genre(36, "History"),
    Genre(27, "Horror"),
    Genre(10402, "Music"),
    Genre(9648, "Mystery"),
    Genre(10749, "Romance"),
    Genre(878, "Sci-Fi"),
    Genre(53, "Thriller"),
    Genre(10752, "War"),
    Genre(37, "Western")
)

val seriesGenres = listOf(
    Genre(10759, "Action & Adventure"),
    Genre(16, "Animation"),
    Genre(35, "Comedy"),
    Genre(80, "Crime"),
    Genre(99, "Documentary"),
    Genre(18, "Drama"),
    Genre(10751, "Family"),
    Genre(10762, "Kids"),
    Genre(9648, "Mystery"),
    Genre(10763, "News"),
    Genre(10764, "Reality"),
    Genre(10765, "Sci-Fi & Fantasy"),
    Genre(10766, "Soap"),
    Genre(10767, "Talk"),
    Genre(10768, "War & Politics"),
    Genre(37, "Western")
)