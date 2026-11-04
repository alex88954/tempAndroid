package com.example.pratiqueretrofitsnackbar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Livre
import data.ReponseLivre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.getValue


object BookApiClient {
    private const val BASE_URL = "https://openlibrary.org/"
    private val retrofit by lazy {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    val apiService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }
}

interface BookApiService {
    @GET("/search.json")
    suspend fun searchBooksByTitle(@Query("q") title: String): ReponseLivre
}
class BookViewModel : ViewModel() {
    val books = MutableStateFlow<List<Livre>>(emptyList())
    fun loadBooks(title: String) {
        viewModelScope.launch {
            try {
                books.value = BookApiClient.apiService.searchBooksByTitle(title).livres
            } catch (e: Exception) {
                Log.e(
                    "BookVM", "Error: ${e.message}"
                )
            }
        }
    }
}
