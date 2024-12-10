package com.application.p3tskit.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.data.pref.AuthModel
import com.application.p3tskit.data.remote.repository.AuthRepository
import com.application.p3tskit.remote.repository.NewsRepository
import com.application.p3tskit.remote.response.ArticlesItem
import com.application.p3tskit.remote.response.NewsResponse
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepository: NewsRepository, private val authRepository: AuthRepository): ViewModel() {

    private val _newsResult = MutableLiveData<List<ArticlesItem?>>()
    val newsResult: LiveData<List<ArticlesItem?>> = _newsResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getNews(count: Int){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = newsRepository.getAllNews()
                result.onSuccess { newsResponse ->
                    _newsResult.value = newsResponse.articles?.take(count)
                }.onFailure { exception ->
                    _errorMessage.value = "Error: ${exception.message}"
                }
            }catch (e: Exception){
                _errorMessage.value ="${e.message}"
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun getSession(): LiveData<AuthModel> {
        return authRepository.getSession().asLiveData()
    }
}