package com.application.p3tskit.data.remote.repository

import com.application.p3tskit.data.pref.AuthModel
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.response.LoginRequest
import com.application.p3tskit.data.remote.response.LoginResponse
import com.application.p3tskit.data.remote.response.RegisterRequest
import com.application.p3tskit.data.remote.response.RegisterResponse
import com.application.p3tskit.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlin.math.E

class AuthRepository private constructor(
    private val authPreferences: AuthPreferences,
    private val apiService: ApiService
){

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse>{
        return try{
            val request = RegisterRequest(name, email, password)
            val response =  apiService.register(request)
            if(response.isSuccessful){
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                }else{
                    Result.failure(Exception("Empty response"))
                }
            }else {
                Result.failure(Exception("Resgistration failed ${response.message()}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse>{
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            if (response.isSuccessful){
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                }else{
                    Result.failure(Exception("Empty response"))
                }
            }else{
                when(response.code()){
                    401 -> Result.failure(Exception("Wrong Password"))
                    404 -> Result.failure(Exception("User not found"))
                    else -> Result.failure(Exception("Login failed ${response.message()}"))
                }
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun saveSession(user: AuthModel){
        authPreferences.saveSession(user)
    }

    fun getSession(): Flow<AuthModel>{
        return authPreferences.getSession()
    }

    suspend fun logout(){
        authPreferences.logout()
    }

    companion object{

        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            authPreferences: AuthPreferences,
            apiService: ApiService
        ): AuthRepository =
            instance?: synchronized(this){
                instance?: AuthRepository(authPreferences, apiService)
            }.also {
                instance = it }
    }
}