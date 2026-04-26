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
                // async launches coroutines in parallel and waits for all results
                val page1 = async {
                    when (category) {
                        "trending" -> api.getTrendingMovies()
                        "top_rated" -> api.getTopRatedMovies(1)
                        "now_playing" -> api.getNowPlayingMovies(1)
                        else -> api.getTrendingMovies()
                    }
                }
                val page2 = async {
                    when (category) {
                        "trending" -> api.getTrendingMovies()
                        "top_rated" -> api.getTopRatedMovies(2)
                        "now_playing" -> api.getNowPlayingMovies(2)
                        else -> api.getTrendingMovies()
                    }
                }
                val page3 = async {
                    when (category) {
                        "trending" -> api.getTrendingMovies()
                        "top_rated" -> api.getTopRatedMovies(3)
                        "now_playing" -> api.getNowPlayingMovies(3)
                        else -> api.getTrendingMovies()
                    }
                }
                val page4 = async {
                    when (category) {
                        "trending" -> api.getTrendingMovies()
                        "top_rated" -> api.getTopRatedMovies(4)
                        "now_playing" -> api.getNowPlayingMovies(4)
                        else -> api.getTrendingMovies()
                    }
                }
                // await() waits for each page to finish then we combine all results
                val combined = page1.await().results +
                        page2.await().results +
                        page3.await().results +
                        page4.await().results

                _seeAllMovies.value = UiState.Success(combined)
            } catch (e: Exception) {
                _seeAllMovies.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadSeeAllSeries(category: String) {
        viewModelScope.launch {
            _seeAllSeries.value = UiState.Loading
            try {
                val page1 = async {
                    when (category) {
                        "trending" -> api.getTrendingSeries()
                        "top_rated" -> api.getTopRatedSeries(1)
                        "on_the_air" -> api.getOnTheAirSeries(1)
                        else -> api.getTrendingSeries()
                    }
                }
                val page2 = async {
                    when (category) {
                        "trending" -> api.getTrendingSeries()
                        "top_rated" -> api.getTopRatedSeries(2)
                        "on_the_air" -> api.getOnTheAirSeries(2)
                        else -> api.getTrendingSeries()
                    }
                }
                val page3 = async {
                    when (category) {
                        "trending" -> api.getTrendingSeries()
                        "top_rated" -> api.getTopRatedSeries(3)
                        "on_the_air" -> api.getOnTheAirSeries(3)
                        else -> api.getTrendingSeries()
                    }
                }
                val page4 = async {
                    when (category) {
                        "trending" -> api.getTrendingSeries()
                        "top_rated" -> api.getTopRatedSeries(4)
                        "on_the_air" -> api.getOnTheAirSeries(4)
                        else -> api.getTrendingSeries()
                    }
                }
                val combined = page1.await().results +
                        page2.await().results +
                        page3.await().results +
                        page4.await().results

                _seeAllSeries.value = UiState.Success(combined)
            } catch (e: Exception) {
                _seeAllSeries.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun refresh() {
        loadMovies()
        loadSeries()
    }
}