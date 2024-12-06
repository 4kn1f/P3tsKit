package com.application.p3tskit.ui.scan

import ModelScanResponse
import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.data.pref.AuthPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.FileOutputStream

class DetailScanViewModel(
    private val diagnoseRepository: DiagnoseRepository,
    private val authPreferences: AuthPreferences,
    private val context: Application
) : ViewModel() {

    private val _scanResult = MutableLiveData<ModelScanResponse?>()
    val scanResult: LiveData<ModelScanResponse?> get() = _scanResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun analyzeImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageFile = uriToFile(imageUri)

                if (imageFile.exists()) {
                    val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

                    val authModel = authPreferences.getSession().first()
                    val token = "Bearer ${authModel.token}"

                    val response = diagnoseRepository.diagnoseImage(token, imagePart)

                    if (response?.isSuccessful == true) {
                        val modelScanResponse = response.body()
                        if (modelScanResponse != null) {
                            _scanResult.postValue(modelScanResponse)
                        } else {
                            _errorMessage.postValue("Error: No data received.")
                        }
                    } else {
                        _errorMessage.postValue("Error: ${response?.message() ?: "Unknown error"}")
                    }
                } else {
                    _errorMessage.postValue("Error: Image file does not exist.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream: OutputStream = FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } != -1) {
                    output.write(buffer, 0, length)
                }
            }
        }

        return tempFile
    }
}
