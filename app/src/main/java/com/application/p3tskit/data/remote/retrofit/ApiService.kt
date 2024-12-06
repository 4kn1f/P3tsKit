package com.application.p3tskit.data.remote.retrofit

import com.application.p3tskit.data.remote.response.LoginRequest
import com.application.p3tskit.data.remote.response.LoginResponse
import com.application.p3tskit.data.remote.response.RegisterRequest
import com.application.p3tskit.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService{
//    @FormUrlEncoded
    @POST("register")
    suspend fun register(
//        @Field("name") name: String,
//        @Field("email") email: String,
//        @Field("password") password: String,
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

//    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @Multipart
    @POST("diagnosed")
    suspend fun getDiagnosed(
        @Part file: MultipartBody.Part
    )
}
