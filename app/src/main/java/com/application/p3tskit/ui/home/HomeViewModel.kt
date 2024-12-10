package com.application.p3tskit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.data.pref.AuthModel
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.data.remote.retrofit.ApiConfig
import com.application.p3tskit.remote.response.ArticlesItem
import kotlinx.coroutines.launch

class HomeViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _newsList = MutableLiveData<List<ArticlesItem>>()
    val newsList: LiveData<List<ArticlesItem>> get() = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        getNews()
    }

    private fun getNews() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getNewsService().getCancerNews()
                if (response.isSuccessful) {
                    _newsList.value = (response.body()?.articles?.take(4)?: emptyList()) as List<ArticlesItem>?
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to load news"
                }
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSession(): LiveData<AuthModel> {
        return authRepository.getSession().asLiveData()
    }
}