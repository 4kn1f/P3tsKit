package com.application.p3tskit.data.remote.repository

import com.application.p3tskit.data.pref.AuthModel
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.response.LoginRequest
import com.application.p3tskit.data.remote.response.LoginResponse
import com.application.p3tskit.data.remote.response.RegisterRequest
import com.application.p3tskit.data.remote.response.RegisterResponse
import com.application.p3tskit.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class AuthRepository private constructor(
    private val authPreferences: AuthPreferences,
    private val apiService: ApiService
) {

    /**
     * Handles user registration by sending a request to the API.
     * Returns a [Result] indicating success or failure.
     */
    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val request = RegisterRequest(name, email, password)
            val response = apiService.register(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Handles user login by sending a request to the API.
     * Returns a [Result] indicating success or failure.
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                val error = when (response.code()) {
                    401 -> "Wrong Password"
                    404 -> "User not found"
                    else -> "Login failed: ${response.message()}"
                }
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Saves the user's session to shared preferences.
     */
    suspend fun saveSession(user: AuthModel) {
        authPreferences.saveSession(user)
    }

    /**
     * Retrieves the current session as a Flow.
     */
    fun getSession(): Flow<AuthModel> {
        return authPreferences.getSession()
    }

    /**
     * Logs out the user by clearing their session.
     */
    suspend fun logout() {
        authPreferences.logout()
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        /**
         * Provides a singleton instance of [AuthRepository].
         * Requires [AuthPreferences] and [ApiService] as dependencies.
         */
        fun getInstance(
            authPreferences: AuthPreferences,
            apiService: ApiService
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(authPreferences, apiService)
            }.also {
                instance = it
            }
    }
}
