package com.application.p3tskit.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.p3tskit.data.di.Injection
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.remote.repository.NewsRepository
import com.application.p3tskit.ui.history.HistoryViewModel
import com.application.p3tskit.ui.home.HomeViewModel
import com.application.p3tskit.ui.login.LoginViewModel
import com.application.p3tskit.ui.profile.ProfileViewModel
import com.application.p3tskit.ui.register.RegisterViewModel

class ViewModelFactory(private val authRepository: AuthRepository, private val diagnoseRepository: DiagnoseRepository, private val newsRepository: NewsRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->{
                RegisterViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->{
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->{
                HomeViewModel(authRepository, newsRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->{
                ProfileViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->{
                HistoryViewModel(diagnoseRepository) as T
            }
            modelClass.isAssignableFrom(NewsViewModel::class.java) ->{
                NewsViewModel(newsRepository, authRepository) as T
            }
            else -> throw IllegalArgumentException("Error")
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory{
            return INSTANCE ?: synchronized(this){
                INSTANCE?: ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideDiagnoseRepository(context),
                    Injection.provideNewsRepository()
                ).also { INSTANCE = it }
            }
        }
    }
}