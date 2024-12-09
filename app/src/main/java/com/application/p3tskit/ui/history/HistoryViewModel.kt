package com.application.p3tskit.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.data.remote.response.DiseasesInfo
import com.application.p3tskit.data.remote.response.HistoryItem
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: DiagnoseRepository) : ViewModel() {

    private val _historyResult = MutableLiveData<List<HistoryItem>>()
    val historyResult: LiveData<List<HistoryItem>> = _historyResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val historyItems = repository.getHistory()

                Log.d("HistoryViewModel", "API Response: $historyItems")

                val updatedHistoryItems = historyItems
                    .map { historyItem ->
                        historyItem?.copy(diseaseInfo = historyItem.diseaseInfo as? DiseasesInfo) // Cast to DiseasesInfo
                    }
                    .filterNotNull()

                _historyResult.value = updatedHistoryItems

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load history: ${e.message}"
                Log.e("HistoryViewModel", "Error loading history: ", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}
