package com.popcinefr.popcinefrapp.util

// At any moment, the UI is in one of these 3 states:
sealed class UiState<out T> {
    // We are waiting for data — show a loading spinner
    object Loading : UiState<Nothing>()

    // Data arrived successfully — show the content
    data class Success<T>(val data: T) : UiState<T>()

    // Something went wrong — show an error message
    data class Error(val message: String) : UiState<Nothing>()
}