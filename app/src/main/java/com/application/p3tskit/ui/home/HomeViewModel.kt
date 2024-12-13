package com.application.p3tskit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.data.pref.AuthModel
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.data.remote.retrofit.ApiConfig
import com.application.p3tskit.remote.repository.NewsRepository
import com.application.p3tskit.remote.response.ArticlesItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val authRepository: AuthRepository, private val newsRepository: NewsRepository): ViewModel() {

    private val _newsList = MutableLiveData<List<ArticlesItem?>>()
    val newsList: LiveData<List<ArticlesItem?>> get() = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> get() = _logout

    init {
        getNews()
        checkSessionTimeout()
    }

    private fun getNews() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = newsRepository.getAllNews()
            result.onSuccess { newsResponse ->
                _newsList.value = newsResponse.articles?.take(4)
            }.onFailure { exception ->
                _errorMessage.value = "Error: ${exception.message}"
            }
            _isLoading.value = false
        }
    }

    fun getSession(): LiveData<AuthModel> {
        return authRepository.getSession().asLiveData()
    }

    fun checkSessionTimeout() {
        viewModelScope.launch {
            val session = authRepository.getSession().first()
            val currentTime = System.currentTimeMillis()
            val sessionAge = currentTime - session.loginTime

            if (sessionAge > 3600000) {
                logout()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logout.postValue(true)
        }
    }

    fun checkSession(): LiveData<Boolean> {
        val isSessionValid = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val session = authRepository.getSession().first()
            isSessionValid.postValue(session.token.isNotEmpty())
        }
        return isSessionValid
    }
}
