package com.example.pratiqueretrofitsnackbar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object ApiClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private val retrofit by lazy {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

interface ApiService {
    @GET("todos")
    suspend fun getTodos(): List<Todo>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Int): Todo
}

class TodosViewModel : ViewModel() {
    val todos = MutableStateFlow<List<Todo>>(emptyList())
    fun loadTodos() {
        viewModelScope.launch {
            try {
                todos.value = ApiClient.apiService.getTodos()
            } catch (e: Exception) {
                Log.e(
                    "TodoVM", "Error: ${e.message}"
                )
            }
        }
    }
}
