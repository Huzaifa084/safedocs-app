package org.devaxiom.safedocs.ui.document

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.DocumentRepository
import org.devaxiom.safedocs.data.model.Document
import org.devaxiom.safedocs.data.security.SessionManager

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val documentRepository = DocumentRepository(application)

    private val _documents = MutableLiveData<List<Document>>()
    val documents: LiveData<List<Document>> = _documents

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchDocuments(
        type: String,
        search: String? = null,
        category: String? = null,
        expiryFrom: String? = null,
        expiryTo: String? = null
    ) {
        viewModelScope.launch {
            // Global guest gating: do not hit APIs, clear errors, show empty state
            val isGuest = SessionManager(getApplication()).isGuest()
            if (isGuest) {
                _documents.postValue(emptyList())
                _error.postValue("")
                return@launch
            }
            try {
                val response = documentRepository.getDocuments(type, search, category, expiryFrom, expiryTo)
                if (response.isSuccessful && response.body()?.success == true) {
                    _documents.postValue(response.body()?.data?.items ?: emptyList())
                } else {
                    val errorMessage = response.body()?.message ?: "API Error: ${response.code()}"
                    Log.e("DocumentViewModel", "Error fetching documents: $errorMessage")
                    _error.postValue(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Network Exception: ${e.message}"
                Log.e("DocumentViewModel", "Exception fetching documents", e)
                _error.postValue(errorMessage)
            }
        }
    }
}
