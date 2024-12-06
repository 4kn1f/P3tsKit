package com.application.p3tskit.data.remote.repository

import android.net.Uri
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.application.p3tskit.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class DiagnoseRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    suspend fun diagnoseImage(imageUri: Uri): Response<ModelScanResponse>? {
        val authModel = authPreferences.getSession().first()
        val token = "Bearer ${authModel.token}"

        val imageFile = File(imageUri.path!!)
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

        return apiService.getDiagnosed(token, body)
    }
}
