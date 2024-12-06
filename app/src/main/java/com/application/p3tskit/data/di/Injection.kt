package com.application.p3tskit.data.di

import android.content.Context
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.data.remote.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        // Pass context directly to getInstance
        val pref = AuthPreferences.getInstance(context) // Use the context directly
        return AuthRepository.getInstance(pref, apiService)
    }
}
