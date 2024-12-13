package com.application.p3tskit.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.data.pref.AuthModel
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)
                val loginResponse = result.getOrNull()

                if (loginResponse != null) {
                    _loginResult.value = loginResponse
                    loginResponse.token?.let { token ->
                        saveSession(AuthModel(email = email, token = token, true, loginTime = System.currentTimeMillis()))
                    } ?: run {
                        _errorMessage.value = "Token not found"
                    }
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Login failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun saveSession(user: AuthModel) {
        viewModelScope.launch {
            authRepository.saveSession(user)
        }
    }
}
