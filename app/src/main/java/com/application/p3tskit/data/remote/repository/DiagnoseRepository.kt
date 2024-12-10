package com.application.p3tskit.data.remote.repository

import android.content.Context
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.response.HistoryItem
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.application.p3tskit.data.remote.retrofit.ApiConfig
import com.application.p3tskit.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MultipartBody
import retrofit2.Response
import android.util.Log

class DiagnoseRepository private constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences,
    private val context: Context
) {

    suspend fun diagnoseImage(authToken: String, imagePart: MultipartBody.Part): Response<ModelScanResponse>? {
        return try {
            apiService.getDiagnosed(authToken, imagePart)
        } catch (e: Exception) {
            Log.e("DiagnoseRepository", "Error diagnosing image: ${e.message}", e)
            null
        }
    }

    suspend fun getHistory(): List<HistoryItem?> {
        val token = authPreferences.getSession().firstOrNull()?.token
        if (token.isNullOrEmpty()) {
            throw Exception("No valid token found in preferences")
        }

        return try {
            val response = apiService.getHistory("Bearer $token")
            if (response.isSuccessful) {
                val historyList = response.body()?.history?.filterNotNull()
                Log.d("DiagnoseRepository", "History fetched successfully: $historyList")
                historyList ?: emptyList()
            } else {
                Log.e("DiagnoseRepository", "Failed to fetch history: ${response.errorBody()?.string()}")
                throw Exception("Failed to fetch history: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("DiagnoseRepository", "Error fetching history: ${e.message}", e)
            throw e
        }
    }

    companion object {
        @Volatile
        private var instance: DiagnoseRepository? = null

        fun getInstance(context: Context): DiagnoseRepository {
            return instance ?: synchronized(this) {
                instance ?: DiagnoseRepository(
                    apiService = ApiConfig.getApiService(),
                    authPreferences = AuthPreferences.getInstance(context),
                    context = context
                )
            }.also { instance = it }
        }
    }
}
