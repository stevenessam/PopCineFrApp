package com.popcinefr.popcinefrapp.util

import com.popcinefr.popcinefrapp.BuildConfig

fun String?.toImageUrl(size: String = "w342"): String {
    // If posterPath is null, return empty string — Coil will show nothing
    if (this == null) return ""
    return "${BuildConfig.TMDB_IMAGE_BASE_URL}$size$this"
}