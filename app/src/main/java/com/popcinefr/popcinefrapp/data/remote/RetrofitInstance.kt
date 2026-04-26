package com.popcinefr.popcinefrapp.data.remote

import com.popcinefr.popcinefrapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // The logging interceptor prints every API request and response
    // in your Logcat — extremely useful for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttp is the HTTP client that does the actual networking
    // Retrofit sits on top of it
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // This interceptor adds api_key to EVERY request automatically
        .addInterceptor { chain ->
            val originalUrl = chain.request().url
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
            chain.proceed(
                chain.request().newBuilder().url(newUrl).build()
            )
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // The actual Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.TMDB_BASE_URL)
        .client(okHttpClient)
        // Gson converts JSON → Kotlin data classes automatically
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // This is what the rest of the app will use
    // "lazy" means it's only created the first time it's accessed
    val api: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }
}