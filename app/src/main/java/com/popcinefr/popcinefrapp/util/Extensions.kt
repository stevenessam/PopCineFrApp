package com.popcinefr.popcinefrapp.util

fun String?.toImageUrl(size: String = "w342"): String {
    // If posterPath is null, return empty string — Coil will show nothing
    if (this == null) return ""
    return "https://image.tmdb.org/t/p/$size$this"
}