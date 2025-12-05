package org.devaxiom.safedocs.ui.document

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.model.Document
import org.devaxiom.safedocs.network.ApiClient

class DocumentViewModel : ViewModel() {

    private val _documents = MutableLiveData<List<Document>>()
    val documents: LiveData<List<Document>> = _documents

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchDocuments(type: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.getDocuments(type)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Extract the list of documents from the new paginated structure
                    val documentList = response.body()?.data?.items ?: emptyList()
                    _documents.postValue(documentList)
                } else {
                    val errorMessage = response.body()?.message ?: "API Error: ${response.code()} ${response.message()}"
                    Log.e("DocumentViewModel", errorMessage)
                    _error.postValue(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Network Exception: ${e.message}"
                Log.e("DocumentViewModel", errorMessage, e)
                _error.postValue(errorMessage)
            }
        }
    }
}
