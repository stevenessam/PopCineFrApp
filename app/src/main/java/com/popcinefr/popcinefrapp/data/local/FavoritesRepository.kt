package com.popcinefr.popcinefrapp.data.local

import com.popcinefr.popcinefrapp.data.local.dao.FavoritesDao
import com.popcinefr.popcinefrapp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(private val dao: FavoritesDao) {

    // These return Flow — the UI observes them and updates automatically
    fun getFavoriteMovies(): Flow<List<FavoriteEntity>> = dao.getFavoriteMovies()
    fun getFavoriteSeries(): Flow<List<FavoriteEntity>> = dao.getFavoriteSeries()

    // Check if already in favorites
    suspend fun isFavorite(id: Int): Boolean {
        return dao.getFavoriteById(id) != null
    }

    // Toggle — if it's a favorite remove it, if not add it
    suspend fun toggleFavorite(entity: FavoriteEntity): Boolean {
        val existing = dao.getFavoriteById(entity.id)
        return if (existing != null) {
            dao.deleteFavoriteById(entity.id)
            false  // removed from favorites
        } else {
            dao.insertFavorite(entity)
            true   // added to favorites
        }
    }
}