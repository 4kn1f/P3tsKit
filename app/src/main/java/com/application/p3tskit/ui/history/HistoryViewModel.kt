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

    /**
     * Fetches history data from the repository and updates the LiveData objects.
     */
    fun getHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Fetch history data from the repository
                val historyItems = repository.getHistory()
                Log.d("HistoryViewModel", "Raw API Response: $historyItems")

                // Safely map diseaseInfo to DiseasesInfo type
                val updatedHistoryItems = historyItems
                    .mapNotNull { historyItem ->
                        try {
                            // Ensure that the diseaseInfo is of type DiseasesInfo
                            historyItem?.copy(diseaseInfo = historyItem.diseaseInfo as? DiseasesInfo)
                        } catch (e: Exception) {
                            Log.e("HistoryViewModel", "Error casting diseaseInfo: ", e)
                            null
                        }
                    }

                Log.d("HistoryViewModel", "Filtered History Items: $updatedHistoryItems")
                _historyResult.value = updatedHistoryItems

            } catch (e: Exception) {
                // Handle and log exceptions
                _errorMessage.value = "Failed to load history: ${e.message ?: "Unknown error"}"
                Log.e("HistoryViewModel", "Error loading history: ", e)
            } finally {
                // Hide loading indicator
                _isLoading.value = false
            }
        }
    }
}
