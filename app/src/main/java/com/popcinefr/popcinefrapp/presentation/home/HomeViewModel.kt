package com.popcinefr.popcinefrapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popcinefr.popcinefrapp.data.remote.MediaItem
import com.popcinefr.popcinefrapp.data.remote.MovieDto
import com.popcinefr.popcinefrapp.data.remote.MovieRepository
import com.popcinefr.popcinefrapp.data.remote.RetrofitInstance
import com.popcinefr.popcinefrapp.data.remote.SeriesDto
import com.popcinefr.popcinefrapp.data.remote.toMediaItem
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

    // --- Hero (10 random popular items) ---
    private val _heroMovies = MutableStateFlow<List<MovieDto>>(emptyList())
    val heroMovies: StateFlow<List<MovieDto>> = _heroMovies

    private val _heroSeries = MutableStateFlow<List<SeriesDto>>(emptyList())
    val heroSeries: StateFlow<List<SeriesDto>> = _heroSeries

    // --- Mixed Trending (movies + series combined) ---
    private val _mixedTrending = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val mixedTrending: StateFlow<UiState<List<MediaItem>>> = _mixedTrending

    // --- Movies ---
    private val _trendingMovies = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val trendingMovies: StateFlow<UiState<List<MovieDto>>> = _trendingMovies

    private val _mostWatchedMovies = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val mostWatchedMovies: StateFlow<UiState<List<MovieDto>>> = _mostWatchedMovies

    private val _nowPlayingMovies = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Loading)
    val nowPlayingMovies: StateFlow<UiState<List<MovieDto>>> = _nowPlayingMovies

    private val _moviesByGenre = MutableStateFlow<UiState<List<MovieDto>>>(UiState.Success(emptyList()))
    val moviesByGenre: StateFlow<UiState<List<MovieDto>>> = _moviesByGenre

    // --- Series ---
    private val _trendingSeries = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)
    val trendingSeries: StateFlow<UiState<List<SeriesDto>>> = _trendingSeries

    private val _mostWatchedSeries = MutableStateFlow<UiState<List<SeriesDto>>>(UiState.Loading)
    val mostWatchedSeries: StateFlow<UiState<List<SeriesDto>>> = _mostWatchedSeries

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

    private val _seeAllMixed = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val seeAllMixed: StateFlow<UiState<List<MediaItem>>> = _seeAllMixed

    // --- Genre selection ---
    val selectedMovieGenre = MutableStateFlow(movieGenres.first())
    val selectedSeriesGenre = MutableStateFlow(seriesGenres.first())

    init {
        loadMovies()
        loadSeries()
        loadMixedTrending()
    }

    fun onTabSelected(tab: HomeTab) {
        selectedTab.value = tab
    }

    private fun loadMixedTrending() {
        viewModelScope.launch {
            _mixedTrending.value = UiState.Loading
            try {
                val moviesDeferred = async { api.getTrendingMovies().results }
                val seriesDeferred = async { api.getTrendingSeries().results }
                val movies = moviesDeferred.await().map { it.toMediaItem() }
                val series = seriesDeferred.await().map { it.toMediaItem() }
                // Interleave movies and series for a mixed feel
                val mixed = mutableListOf<MediaItem>()
                val maxSize = maxOf(movies.size, series.size)
                for (i in 0 until maxSize) {
                    if (i < movies.size) mixed.add(movies[i])
                    if (i < series.size) mixed.add(series[i])
                }
                _mixedTrending.value = UiState.Success(mixed.distinctBy { it.id })
            } catch (e: Exception) {
                _mixedTrending.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            // Hero — 10 random popular movies with backdrops
            launch {
                try {
                    val p1 = async { api.getPopularMovies(1).results }
                    val p2 = async { api.getPopularMovies(2).results }
                    val combined = (p1.await() + p2.await())
                        .filter { it.backdropPath != null }
                        .shuffled()
                        .take(10)
                    _heroMovies.value = combined
                } catch (e: Exception) { }
            }
            launch {
                _trendingMovies.value = UiState.Loading
                repository.getTrendingMovies()
                    .onSuccess { _trendingMovies.value = UiState.Success(it) }
                    .onFailure { _trendingMovies.value = UiState.Error(it.message ?: "Error") }
            }
            launch {
                _mostWatchedMovies.value = UiState.Loading
                repository.getPopularMovies()
                    .onSuccess { _mostWatchedMovies.value = UiState.Success(it) }
                    .onFailure { _mostWatchedMovies.value = UiState.Error(it.message ?: "Error") }
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
            // Hero — 10 random popular series with backdrops
            launch {
                try {
                    val p1 = async { api.getPopularSeries(1).results }
                    val p2 = async { api.getPopularSeries(2).results }
                    val combined = (p1.await() + p2.await())
                        .filter { it.backdropPath != null }
                        .shuffled()
                        .take(10)
                    _heroSeries.value = combined
                } catch (e: Exception) { }
            }
            launch {
                _trendingSeries.value = UiState.Loading
                repository.getTrendingSeries()
                    .onSuccess { _trendingSeries.value = UiState.Success(it) }
                    .onFailure { _trendingSeries.value = UiState.Error(it.message ?: "Error") }
            }
            launch {
                _mostWatchedSeries.value = UiState.Loading
                repository.getPopularSeries()
                    .onSuccess { _mostWatchedSeries.value = UiState.Success(it) }
                    .onFailure { _mostWatchedSeries.value = UiState.Error(it.message ?: "Error") }
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
                    "trending" -> api.getTrendingMovies().results.distinctBy { it.id }
                    "most_watched" -> {
                        val p1 = async { api.getPopularMovies(1).results }
                        val p2 = async { api.getPopularMovies(2).results }
                        val p3 = async { api.getPopularMovies(3).results }
                        val p4 = async { api.getPopularMovies(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await()).distinctBy { it.id }
                    }
                    "now_playing" -> {
                        val p1 = async { api.getNowPlayingMovies(1).results }
                        val p2 = async { api.getNowPlayingMovies(2).results }
                        val p3 = async { api.getNowPlayingMovies(3).results }
                        val p4 = async { api.getNowPlayingMovies(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await()).distinctBy { it.id }
                    }
                    else -> api.getTrendingMovies().results.distinctBy { it.id }
                }
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
                    "trending" -> api.getTrendingSeries().results.distinctBy { it.id }
                    "most_watched" -> {
                        val p1 = async { api.getPopularSeries(1).results }
                        val p2 = async { api.getPopularSeries(2).results }
                        val p3 = async { api.getPopularSeries(3).results }
                        val p4 = async { api.getPopularSeries(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await()).distinctBy { it.id }
                    }
                    "on_the_air" -> {
                        val p1 = async { api.getOnTheAirSeries(1).results }
                        val p2 = async { api.getOnTheAirSeries(2).results }
                        val p3 = async { api.getOnTheAirSeries(3).results }
                        val p4 = async { api.getOnTheAirSeries(4).results }
                        (p1.await() + p2.await() + p3.await() + p4.await()).distinctBy { it.id }
                    }
                    else -> api.getTrendingSeries().results.distinctBy { it.id }
                }
                _seeAllSeries.value = UiState.Success(results)
            } catch (e: Exception) {
                _seeAllSeries.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadSeeAllMixed() {
        viewModelScope.launch {
            _seeAllMixed.value = UiState.Loading
            try {
                val mp1 = async { api.getTrendingMovies().results }
                val sp1 = async { api.getTrendingSeries().results }
                val mp2 = async { api.getPopularMovies(1).results }
                val sp2 = async { api.getPopularSeries(1).results }
                val movies = (mp1.await() + mp2.await())
                    .distinctBy { it.id }
                    .map { it.toMediaItem() }
                val series = (sp1.await() + sp2.await())
                    .distinctBy { it.id }
                    .map { it.toMediaItem() }
                val mixed = mutableListOf<MediaItem>()
                val maxSize = maxOf(movies.size, series.size)
                for (i in 0 until maxSize) {
                    if (i < movies.size) mixed.add(movies[i])
                    if (i < series.size) mixed.add(series[i])
                }
                _seeAllMixed.value = UiState.Success(mixed.distinctBy { it.id })
            } catch (e: Exception) {
                _seeAllMixed.value = UiState.Error(e.message ?: "Error")
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
        loadMixedTrending()
    }
}