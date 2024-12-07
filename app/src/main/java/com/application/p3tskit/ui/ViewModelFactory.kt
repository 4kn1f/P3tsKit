package com.application.p3tskit.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.p3tskit.data.di.Injection
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.ui.home.HomeViewModel
import com.application.p3tskit.ui.login.LoginViewModel
import com.application.p3tskit.ui.profile.ProfileViewModel
import com.application.p3tskit.ui.register.RegisterViewModel

class ViewModelFactory(private val authRepository: AuthRepository): ViewModelProvider.NewInstanceFactory() {

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
                HomeViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->{
                ProfileViewModel(authRepository) as T
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
                    Injection.provideAuthRepository(context)
                ).also { INSTANCE = it }
            }
        }
    }
}