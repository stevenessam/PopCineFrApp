package com.popcinefr.popcinefrapp.data.local.dao

import androidx.room.*
import com.popcinefr.popcinefrapp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    // Get all favorites as a Flow
    // Flow means the UI will automatically update when the list changes
    // e.g. when user adds or removes a favorite the screen updates instantly
    @Query("SELECT * FROM favorites ORDER BY title ASC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    // Get only movies from favorites
    @Query("SELECT * FROM favorites WHERE mediaType = 'movie' ORDER BY title ASC")
    fun getFavoriteMovies(): Flow<List<FavoriteEntity>>

    // Get only series from favorites
    @Query("SELECT * FROM favorites WHERE mediaType = 'series' ORDER BY title ASC")
    fun getFavoriteSeries(): Flow<List<FavoriteEntity>>

    // Check if a specific item is already in favorites
    // Returns null if not found
    @Query("SELECT * FROM favorites WHERE id = :id LIMIT 1")
    suspend fun getFavoriteById(id: Int): FavoriteEntity?

    // Save a favorite — if it already exists replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    // Remove a favorite by its ID
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)
}