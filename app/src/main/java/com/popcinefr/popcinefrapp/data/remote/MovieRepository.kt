package com.popcinefr.popcinefrapp.data.remote

class MovieRepository {

    private val api = RetrofitInstance.api

    // We wrap calls in try/catch so errors don't crash the app
    // We return Result<T> — Kotlin's built-in success/failure wrapper

    suspend fun getTrendingMovies(): Result<List<MovieDto>> {
        return try {
            val response = api.getTrendingMovies()
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingSeries(): Result<List<SeriesDto>> {
        return try {
            val response = api.getTrendingSeries()
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMovies(query: String): Result<List<MovieDto>> {
        return try {
            val response = api.searchMovies(query)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchSeries(query: String): Result<List<SeriesDto>> {
        return try {
            val response = api.searchSeries(query)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}