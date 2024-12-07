package com.application.p3tskit.data.remote.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.application.p3tskit.data.remote.retrofit.ApiConfig
import com.application.p3tskit.data.remote.retrofit.ApiService
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

