package com.application.p3tskit.data.remote.retrofit

import com.application.p3tskit.data.remote.response.LoginRequest
import com.application.p3tskit.data.remote.response.LoginResponse
import com.application.p3tskit.data.remote.response.RegisterRequest
import com.application.p3tskit.data.remote.response.RegisterResponse
import com.application.p3tskit.data.remote.response.ModelScanResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
}
