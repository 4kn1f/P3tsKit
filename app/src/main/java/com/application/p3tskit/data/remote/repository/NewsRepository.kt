package com.application.p3tskit.remote.repository

import com.application.p3tskit.data.remote.retrofit.ApiConfig
import com.application.p3tskit.data.remote.retrofit.ApiService
import com.application.p3tskit.remote.response.NewsResponse

class NewsRepository {

    private val apiService = ApiConfig.getNewsService()

    suspend fun getAllNews(): Result<NewsResponse> {
        return try {
            val response = apiService.getCancerNews("veterinary", "en")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty Response"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: NewsRepository? = null

        fun getInstance(): NewsRepository{
            return INSTANCE?: synchronized(this){
                INSTANCE?:NewsRepository().also { INSTANCE = it }
            }
        }
    }
}