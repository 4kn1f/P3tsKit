package com.application.p3tskit.data.remote.retrofit

import com.application.p3tskit.BuildConfig
import com.application.p3tskit.data.remote.response.HistoryResponses
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.application.p3tskit.data.remote.response.LoginRequest
import com.application.p3tskit.data.remote.response.LoginResponse
import com.application.p3tskit.data.remote.response.RegisterRequest
import com.application.p3tskit.data.remote.response.RegisterResponse
import com.application.p3tskit.remote.response.NewsResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @Multipart
    @POST("diagnose")
    suspend fun getDiagnosed(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<ModelScanResponse>

    @GET ("history")
    suspend fun getHistory(
        @Header ("Authorization") token: String?
    ): Response<HistoryResponses>

    @GET("everything")
    suspend fun getCancerNews(
        @Query("q") query: String = "veterinary",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_TOKEN
    ): Response<NewsResponse>
}
