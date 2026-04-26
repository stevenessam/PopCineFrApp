package com.popcinefr.popcinefrapp.presentation.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.popcinefr.popcinefrapp.data.local.AppDatabase
import com.popcinefr.popcinefrapp.data.local.FavoritesRepository
import com.popcinefr.popcinefrapp.data.local.entity.FavoriteEntity
import com.popcinefr.popcinefrapp.data.remote.MovieDetailDto
import com.popcinefr.popcinefrapp.data.remote.MovieRepository
import com.popcinefr.popcinefrapp.data.remote.SeriesDetailDto
import com.popcinefr.popcinefrapp.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val movieRepository = MovieRepository()
    private val favoritesRepository = FavoritesRepository(
        AppDatabase.getDatabase(application).favoritesDao()
    )

    private val _movieDetail = MutableStateFlow<UiState<MovieDetailDto>>(UiState.Loading)
    val movieDetail: StateFlow<UiState<MovieDetailDto>> = _movieDetail

    private val _seriesDetail = MutableStateFlow<UiState<SeriesDetailDto>>(UiState.Loading)
    val seriesDetail: StateFlow<UiState<SeriesDetailDto>> = _seriesDetail

    // Tracks whether current item is a favorite
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _movieDetail.value = UiState.Loading
            // Check favorite status at the same time
            _isFavorite.value = favoritesRepository.isFavorite(movieId)

            movieRepository.getMovieDetail(movieId)
                .onSuccess { _movieDetail.value = UiState.Success(it) }
                .onFailure { _movieDetail.value = UiState.Error(it.message ?: "Error") }
        }
    }

    fun loadSeriesDetail(seriesId: Int) {
        viewModelScope.launch {
            _seriesDetail.value = UiState.Loading
            _isFavorite.value = favoritesRepository.isFavorite(seriesId)

            movieRepository.getSeriesDetail(seriesId)
                .onSuccess { _seriesDetail.value = UiState.Success(it) }
                .onFailure { _seriesDetail.value = UiState.Error(it.message ?: "Error") }
        }
    }

    fun toggleFavorite(entity: FavoriteEntity) {
        viewModelScope.launch {
            val isNowFavorite = favoritesRepository.toggleFavorite(entity)
            // Update the heart icon immediately
            _isFavorite.value = isNowFavorite
        }
    }
}