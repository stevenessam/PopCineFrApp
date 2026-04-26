package com.popcinefr.popcinefrapp.presentation.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.popcinefr.popcinefrapp.data.local.AppDatabase
import com.popcinefr.popcinefrapp.data.local.FavoritesRepository
import com.popcinefr.popcinefrapp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// We use AndroidViewModel instead of ViewModel
// because we need the Application context to create the database
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FavoritesRepository(
        AppDatabase.getDatabase(application).favoritesDao()
    )

    // stateIn converts a Flow into a StateFlow that the UI can observe
    // SharingStarted.WhileSubscribed means it only runs while UI is visible
    val favoriteMovies: StateFlow<List<FavoriteEntity>> = repository
        .getFavoriteMovies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteSeries: StateFlow<List<FavoriteEntity>> = repository
        .getFavoriteSeries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(entity: FavoriteEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entity)
        }
    }
}