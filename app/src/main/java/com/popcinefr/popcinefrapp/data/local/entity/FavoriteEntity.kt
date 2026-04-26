package com.popcinefr.popcinefrapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity tells Room this is a database table
// tableName is the actual name of the table in SQLite
@Entity(tableName = "favorites")
data class FavoriteEntity(

    // @PrimaryKey means this field uniquely identifies each row
    // Every movie and series has a unique TMDB id so we use that
    @PrimaryKey
    val id: Int,

    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String?,

    // We need to know if this is a movie or series
    // so we know which detail screen to open when tapped
    // Value will be either "movie" or "series"
    val mediaType: String
)