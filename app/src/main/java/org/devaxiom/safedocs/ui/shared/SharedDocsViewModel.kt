package org.devaxiom.safedocs.ui.shared

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.DocumentRepository
import org.devaxiom.safedocs.data.model.Document

class SharedDocsViewModel(application: Application) : AndroidViewModel(application) {

    private val documentRepository = DocumentRepository(application)

    private val _documents = MutableLiveData<List<Document>>()
    val documents: LiveData<List<Document>> = _documents

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Correctly calls GET /api/documents?type=SHARED
    fun fetchSharedByMeDocuments() {
        viewModelScope.launch {
            try {
                val response = documentRepository.getDocuments("SHARED")
                if (response.isSuccessful && response.body()?.success == true) {
                    _documents.postValue(response.body()?.data?.items ?: emptyList())
                } else {
                    val errorMessage = response.body()?.message ?: "API Error: ${response.code()}"
                    Log.e("SharedDocsViewModel", "Error fetching shared by me: $errorMessage")
                    _error.postValue(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Network Exception: ${e.message}"
                Log.e("SharedDocsViewModel", "Exception fetching shared by me", e)
                _error.postValue(errorMessage)
            }
        }
    }

    // Correctly calls GET /api/documents/shared/with-me
    fun fetchSharedWithMeDocuments() {
        viewModelScope.launch {
            try {
                val response = documentRepository.getSharedWithMeDocuments()
                if (response.isSuccessful && response.body()?.success == true) {
                    _documents.postValue(response.body()?.data?.items ?: emptyList())
                } else {
                    val errorMessage = response.body()?.message ?: "API Error: ${response.code()}"
                    Log.e("SharedDocsViewModel", "Error fetching shared with me: $errorMessage")
                    _error.postValue(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Network Exception: ${e.message}"
                Log.e("SharedDocsViewModel", "Exception fetching shared with me", e)
                _error.postValue(errorMessage)
            }
        }
    }
}
