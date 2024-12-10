package com.application.p3tskit.remote.repository

import com.application.p3tskit.data.remote.retrofit.ApiService
import com.application.p3tskit.remote.response.NewsResponse

class NewsRepository(private val apiService: ApiService) {

    suspend fun getAllNews(): Result<NewsResponse>{
        return try{
            val response = apiService.getCancerNews("veterinary", "en")

            if (response.isSuccessful){
//                Result.success(response.body()!!)
                response.body()?.let {
                    Result.success(it)
                }?:Result.failure(Exception("Empty"))
            }else{
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: NewsRepository? = null

        fun getInstance(apiService: ApiService): NewsRepository{
            return INSTANCE?: synchronized(this){
                INSTANCE?:NewsRepository(apiService).also { INSTANCE = it }
            }
        }
    }
}