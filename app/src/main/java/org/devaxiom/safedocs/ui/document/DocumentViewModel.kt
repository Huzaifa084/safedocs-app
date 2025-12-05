package org.devaxiom.safedocs.ui.document

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

    fun fetchDocuments(type: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.getDocuments(type)
                if (response.isSuccessful) {
                    _documents.postValue(response.body())
                } else {
                    // TODO: Handle error
                }
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}
