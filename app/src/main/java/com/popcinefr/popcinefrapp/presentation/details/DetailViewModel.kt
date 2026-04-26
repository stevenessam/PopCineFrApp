package com.popcinefr.popcinefrapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popcinefr.popcinefrapp.data.remote.MovieDetailDto
import com.popcinefr.popcinefrapp.data.remote.MovieRepository
import com.popcinefr.popcinefrapp.data.remote.SeriesDetailDto
import com.popcinefr.popcinefrapp.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _movieDetail = MutableStateFlow<UiState<MovieDetailDto>>(UiState.Loading)
    val movieDetail: StateFlow<UiState<MovieDetailDto>> = _movieDetail

    private val _seriesDetail = MutableStateFlow<UiState<SeriesDetailDto>>(UiState.Loading)
    val seriesDetail: StateFlow<UiState<SeriesDetailDto>> = _seriesDetail

    fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _movieDetail.value = UiState.Loading
            repository.getMovieDetail(movieId)
                .onSuccess { _movieDetail.value = UiState.Success(it) }
                .onFailure { _movieDetail.value = UiState.Error(it.message ?: "Error") }
        }
    }

    fun loadSeriesDetail(seriesId: Int) {
        viewModelScope.launch {
            _seriesDetail.value = UiState.Loading
            repository.getSeriesDetail(seriesId)
                .onSuccess { _seriesDetail.value = UiState.Success(it) }
                .onFailure { _seriesDetail.value = UiState.Error(it.message ?: "Error") }
        }
    }
}