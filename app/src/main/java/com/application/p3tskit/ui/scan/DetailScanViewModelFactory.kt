package com.application.p3tskit.ui.scan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.data.pref.AuthPreferences

class DetailScanViewModelFactory(
    private val diagnoseRepository: DiagnoseRepository,
    private val authPreferences: AuthPreferences,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailScanViewModel::class.java)) {
            return DetailScanViewModel(diagnoseRepository, authPreferences, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
