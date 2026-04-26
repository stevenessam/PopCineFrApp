package com.popcinefr.popcinefrapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popcinefr.popcinefrapp.data.remote.MovieDto
import com.popcinefr.popcinefrapp.data.remote.MovieRepository
import com.popcinefr.popcinefrapp.data.remote.SeriesDto
import com.popcinefr.popcinefrapp.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Our repository — this is where we get data from
    private val repository = MovieRepository()

    // MutableStateFlow is private — only the ViewModel can change it
    private val _moviesState = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    private val _seriesState = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)

    // StateFlow is public — the UI can only READ it, not change it
    // This is important: the UI never modifies data directly
    val moviesState: StateFlow<UiState<List<MovieDto>>> = _moviesState
    val seriesState: StateFlow<UiState<List<SeriesDto>>> = _seriesState

    // This block runs automatically when the ViewModel is created
    init {
        getTrendingMovies()
        getTrendingSeries()
    }

    private fun getTrendingMovies() {
        // viewModelScope is a coroutine scope tied to the ViewModel's life
        // When the ViewModel is destroyed, all coroutines inside are cancelled automatically
        viewModelScope.launch {
            _moviesState.value = UiState.Loading

            // Result is Kotlin's built-in success/failure wrapper
            val result = repository.getTrendingMovies()

            // onSuccess and onFailure handle both cases cleanly
            result
                .onSuccess { movies ->
                    _moviesState.value = UiState.Success(movies)
                }
                .onFailure { error ->
                    _moviesState.value = UiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    private fun getTrendingSeries() {
        viewModelScope.launch {
            _seriesState.value = UiState.Loading

            val result = repository.getTrendingSeries()

            result
                .onSuccess { series ->
                    _seriesState.value = UiState.Success(series)
                }
                .onFailure { error ->
                    _seriesState.value = UiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    // Called when user pulls to refresh
    fun refresh() {
        getTrendingMovies()
        getTrendingSeries()
    }
}