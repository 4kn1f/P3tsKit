package com.application.p3tskit.data.remote.repository

import android.content.Context
import android.graphics.pdf.models.ListItem
import android.net.Uri
import android.provider.MediaStore
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.response.HistoryItem
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.application.p3tskit.data.remote.retrofit.ApiConfig
import com.application.p3tskit.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MultipartBody
import retrofit2.Response

class DiagnoseRepository private constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences,
    private val context: Context
) {

    suspend fun diagnoseImage(authToken: String, imagePart: MultipartBody.Part): Response<ModelScanResponse>? {
        return apiService.getDiagnosed(authToken, imagePart)
    }

    suspend fun getHistory(): List<HistoryItem?> {
        val token = authPreferences.getSession().firstOrNull()?.token
        val remote = apiService.getHistory("Bearer $token")
        return if (remote.isSuccessful) {
            remote.body()?.history?.filterNotNull() ?: emptyList()
        } else {
            throw Exception("Failed to fetch history from the API")
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

