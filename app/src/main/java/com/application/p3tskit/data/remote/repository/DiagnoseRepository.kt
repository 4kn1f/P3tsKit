package com.application.p3tskit.data.remote.repository

import ModelScanResponse
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import retrofit2.Response

class DiagnoseRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences,
    private val context: Context
) {
    suspend fun diagnoseImage(authToken: String, imagePart: MultipartBody.Part): Response<ModelScanResponse>? {
        return apiService.getDiagnosed(authToken, imagePart)
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return cursor?.getString(columnIndex!!) ?: ""
    }
}
