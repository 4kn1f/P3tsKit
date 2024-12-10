package com.application.p3tskit.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.remote.repository.NewsRepository
import com.application.p3tskit.remote.response.NewsResponse
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository): ViewModel() {

    private val _newsResult = MutableLiveData<NewsResponse>()
    val newsResult: LiveData<NewsResponse> = _newsResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getNews(){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getAllNews()
                result.onSuccess { newsResponse ->
                    _newsResult.value = newsResponse
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
}