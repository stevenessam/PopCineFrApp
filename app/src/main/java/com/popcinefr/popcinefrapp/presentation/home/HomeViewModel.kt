package com.popcinefr.popcinefrapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popcinefr.popcinefrapp.data.remote.MovieDto
import com.popcinefr.popcinefrapp.data.remote.MovieRepository
import com.popcinefr.popcinefrapp.data.remote.RetrofitInstance
import com.popcinefr.popcinefrapp.data.remote.SeriesDto
import com.popcinefr.popcinefrapp.util.Genre
import com.popcinefr.popcinefrapp.util.UiState
import com.popcinefr.popcinefrapp.util.movieGenres
import com.popcinefr.popcinefrapp.util.seriesGenres
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class HomeTab { MOVIES, SERIES }

class HomeViewModel : ViewModel() {

    private val repository = MovieRepository()
    private val api = RetrofitInstance.api

    val selectedTab = MutableStateFlow(HomeTab.MOVIES)

    // --- Movies ---
    private val _trendingMovies = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val trendingMovies: StateFlow<UiState<List<MovieDto>>> = _trendingMovies

    private val _topRatedMovies = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val topRatedMovies: StateFlow<UiState<List<MovieDto>>> = _topRatedMovies

    private val _nowPlayingMovies = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val nowPlayingMovies: StateFlow<UiState<List<MovieDto>>> = _nowPlayingMovies

    private val _moviesByGenre = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Success(emptyList()))
    val moviesByGenre: StateFlow<UiState<List<MovieDto>>> = _moviesByGenre

    // --- Series ---
    private val _trendingSeries = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)
    val trendingSeries: StateFlow<UiState<List<SeriesDto>>> = _trendingSeries

    private val _topRatedSeries = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)
    val topRatedSeries: StateFlow<UiState<List<SeriesDto>>> = _topRatedSeries

    private val _onTheAirSeries = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)
    val onTheAirSeries: StateFlow<UiState<List<SeriesDto>>> = _onTheAirSeries

    private val _seriesByGenre = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Success(emptyList()))
    val seriesByGenre: StateFlow<UiState<List<SeriesDto>>> = _seriesByGenre

    // --- See All ---
    private val _seeAllMovies = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val seeAllMovies: StateFlow<UiState<List<MovieDto>>> = _seeAllMovies

    private val _seeAllSeries = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)
    val seeAllSeries: StateFlow<UiState<List<SeriesDto>>> = _seeAllSeries

    private val _seeAllMoviesByGenre = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val seeAllMoviesByGenre: StateFlow<UiState<List<MovieDto>>> = _seeAllMoviesByGenre

    private val _seeAllSeriesByGenre = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)
    val seeAllSeriesByGenre: StateFlow<UiState<List<SeriesDto>>> = _seeAllSeriesByGenre

    // --- Genre selection ---
    val selectedMovieGenre = MutableStateFlow(movieGenres.first())
    val selectedSeriesGenre = MutableStateFlow(seriesGenres.first())

    init {
        loadMovies()
        loadSeries()
    }

    fun onTabSelected(tab: HomeTab) {
        selectedTab.value = tab
    }

    private fun loadMovies() {
        viewModelScope.launch {
            launch {
                _trendingMovies.value = UiState.Loading
                repository.getTrendingMovies()
                    .onSuccess { _trendingMovies.value = UiState.Success(it) }
                    .onFailure { _trendingMovies.value = UiState.Error(it.message ?: "Error") }
            }
            launch {
                _topRatedMovies.value = UiState.Loading
                repository.getTopRatedMovies()
                    .onSuccess { _topRatedMovies.value = UiState.Success(it) }
                    .onFailure { _topRatedMovies.value = UiState.Error(it.message ?: "Error") }
            }
            launch {
                _nowPlayingMovies.value = UiState.Loading
                repository.getNowPlayingMovies()
                    .onSuccess { _nowPlayingMovies.value = UiState.Success(it) }
                    .onFailure { _nowPlayingMovies.value = UiState.Error(it.message ?: "Error") }
            }
            loadMoviesByGenre(movieGenres.first())
        }
    }

    private fun loadSeries() {
        viewModelScope.launch {
            launch {
                _trendingSeries.value = UiState.Loading
                repository.getTrendingSeries()
                    .onSuccess { _trendingSeries.value = UiState.Success(it) }
                    .onFailure { _trendingSeries.value = UiState.Error(it.message ?: "Error") }
            }
            launch {
                _topRatedSeries.value = UiState.Loading
                repository.getTopRatedSeries()
                    .onSuccess { _topRatedSeries.value = UiState.Success(it) }
                    .onFailure { _topRatedSeries.value = UiState.Error(it.message ?: "Error") }
            }
            launch {
                _onTheAirSeries.value = UiState.Loading
                repository.getOnTheAirSeries()
                    .onSuccess { _onTheAirSeries.value = UiState.Success(it) }
                    .onFailure { _onTheAirSeries.value = UiState.Error(it.message ?: "Error") }
            }
            loadSeriesByGenre(seriesGenres.first())
        }
    }

    fun loadMoviesByGenre(genre: Genre) {
        selectedMovieGenre.value = genre
        viewModelScope.launch {
            _moviesByGenre.value = UiState.Loading
            repository.getMoviesByGenre(genre.id)
                .onSuccess { _moviesByGenre.value = UiState.Success(it) }
                .onFailure { _moviesByGenre.value = UiState.Error(it.message ?: "Error") }
        }
    }

    fun loadSeriesByGenre(genre: Genre) {
        selectedSeriesGenre.value = genre
        viewModelScope.launch {
            _seriesByGenre.value = UiState.Loading
            repository.getSeriesByGenre(genre.id)
                .onSuccess { _seriesByGenre.value = UiState.Success(it) }
                .onFailure { _seriesByGenre.value = UiState.Error(it.message ?: "Error") }
        }
    }

    fun loadSeeAllMovies(category: String) {
        viewModelScope.launch {
            _seeAllMovies.value = UiState.Loading
            try {
                val results = when (category) {
                    "trending" -> {
                        // Trending only has 1 page — load it once
                        api.getTrendingMovies().results
                            .distinctBy { it.id }
                    }
                    "top_rated" -> {
                        // Wait for ALL pages to finish before emitting Success
                        val p1 = async { api.getTopRatedMovies(1).results }
                        val p2 = async { api.getTopRatedMovies(2).results }
                        val p3 = async { api.getTopRatedMovies(3).results }
                        val p4 = async { api.getTopRatedMovies(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await())
                            .distinctBy { it.id }
                    }
                    "now_playing" -> {
                        val p1 = async { api.getNowPlayingMovies(1).results }
                        val p2 = async { api.getNowPlayingMovies(2).results }
                        val p3 = async { api.getNowPlayingMovies(3).results }
                        val p4 = async { api.getNowPlayingMovies(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await())
                            .distinctBy { it.id }
                    }
                    else -> api.getTrendingMovies().results.distinctBy { it.id }
                }
                // Only set Success AFTER all pages are combined and deduplicated
                _seeAllMovies.value = UiState.Success(results)
            } catch (e: Exception) {
                _seeAllMovies.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadSeeAllSeries(category: String) {
        viewModelScope.launch {
            _seeAllSeries.value = UiState.Loading
            try {
                val results = when (category) {
                    "trending" -> {
                        api.getTrendingSeries().results
                            .distinctBy { it.id }
                    }
                    "top_rated" -> {
                        val p1 = async { api.getTopRatedSeries(1).results }
                        val p2 = async { api.getTopRatedSeries(2).results }
                        val p3 = async { api.getTopRatedSeries(3).results }
                        val p4 = async { api.getTopRatedSeries(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await())
                            .distinctBy { it.id }
                    }
                    "on_the_air" -> {
                        val p1 = async { api.getOnTheAirSeries(1).results }
                        val p2 = async { api.getOnTheAirSeries(2).results }
                        val p3 = async { api.getOnTheAirSeries(3).results }
                        val p4 = async { api.getOnTheAirSeries(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await())
                            .distinctBy { it.id }
                    }
                    else -> api.getTrendingSeries().results.distinctBy { it.id }
                }
                _seeAllSeries.value = UiState.Success(results)
            } catch (e: Exception) {
                _seeAllSeries.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadSeeAllMoviesByGenre(genreId: Int) {
        viewModelScope.launch {
            _seeAllMoviesByGenre.value = UiState.Loading
            try {
                val p1 = async { api.getMoviesByGenre(genreId, 1).results }
                val p2 = async { api.getMoviesByGenre(genreId, 2).results }
                val p3 = async { api.getMoviesByGenre(genreId, 3).results }
                val p4 = async { api.getMoviesByGenre(genreId, 4).results }
                val combined = (p1.await() + p2.await() + p3.await() + p4.await())
                    .distinctBy { it.id }
                _seeAllMoviesByGenre.value = UiState.Success(combined)
            } catch (e: Exception) {
                _seeAllMoviesByGenre.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadSeeAllSeriesByGenre(genreId: Int) {
        viewModelScope.launch {
            _seeAllSeriesByGenre.value = UiState.Loading
            try {
                val p1 = async { api.getSeriesByGenre(genreId, 1).results }
                val p2 = async { api.getSeriesByGenre(genreId, 2).results }
                val p3 = async { api.getSeriesByGenre(genreId, 3).results }
                val p4 = async { api.getSeriesByGenre(genreId, 4).results }
                val combined = (p1.await() + p2.await() + p3.await() + p4.await())
                    .distinctBy { it.id }
                _seeAllSeriesByGenre.value = UiState.Success(combined)
            } catch (e: Exception) {
                _seeAllSeriesByGenre.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun refresh() {
        loadMovies()
        loadSeries()
    }
}