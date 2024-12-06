package com.application.p3tskit.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun register(name: String, email: String, password: String){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.register(name, email, password)
                result.onSuccess{ registerResponse ->
                    _registerResult.value = registerResponse
                    _isLoading.value = false
                }.onFailure { exception ->
                    _errorMessage.value = "${exception.message}"
                    _isLoading.value = false
                }
            }catch (e: Exception){
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
                e.printStackTrace()
            }
        }
    }
}