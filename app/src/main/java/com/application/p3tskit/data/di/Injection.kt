package com.application.p3tskit.data.di

import android.content.Context
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.pref.dataStore
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.data.remote.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository{
        val apiService = ApiConfig.getApiService()
        val pref = AuthPreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(pref, apiService)
    }
}