package com.popcinefr.popcinefrapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popcinefr.popcinefrapp.data.remote.MovieDto
import com.popcinefr.popcinefrapp.data.remote.MovieRepository
import com.popcinefr.popcinefrapp.data.remote.SeriesDto
import com.popcinefr.popcinefrapp.util.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {

    private val repository = MovieRepository()

    // Holds what the user is currently typing
    val searchQuery = MutableStateFlow("")

    private val _moviesState = MutableStateFlow<UiState<List<MovieDto>>>(
        UiState.Success(emptyList())
    )
    private val _seriesState = MutableStateFlow<UiState<List<SeriesDto>>>(
        UiState.Success(emptyList())
    )

    val moviesState: StateFlow<UiState<List<MovieDto>>> = _moviesState
    val seriesState: StateFlow<UiState<List<SeriesDto>>> = _seriesState

    init {
        // We observe the searchQuery flow here
        // Instead of searching on every keystroke we use a pipeline:
        viewModelScope.launch {
            searchQuery
                // Wait 500ms after user stops typing before continuing
                .debounce(500)
                // Only continue if the value actually changed
                // Prevents duplicate API calls for same query
                .distinctUntilChanged()
                // Only search if query has at least 2 characters
                .filter { it.length >= 2 }
                .collect { query ->
                    // This runs only after debounce + filter pass
                    searchMovies(query)
                    searchSeries(query)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query

        // If user clears the search bar reset results immediately
        // No need to wait for debounce
        if (query.isEmpty()) {
            _moviesState.value = UiState.Success(emptyList())
            _seriesState.value = UiState.Success(emptyList())
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            _moviesState.value = UiState.Loading
            repository.searchMovies(query)
                .onSuccess { _moviesState.value = UiState.Success(it) }
                .onFailure { _moviesState.value = UiState.Error(it.message ?: "Error") }
        }
    }

    private fun searchSeries(query: String) {
        viewModelScope.launch {
            _seriesState.value = UiState.Loading
            repository.searchSeries(query)
                .onSuccess { _seriesState.value = UiState.Success(it) }
                .onFailure { _seriesState.value = UiState.Error(it.message ?: "Error") }
        }
    }
}