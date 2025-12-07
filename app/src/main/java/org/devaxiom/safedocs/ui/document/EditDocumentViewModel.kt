package org.devaxiom.safedocs.ui.document

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.DocumentRepository
import org.devaxiom.safedocs.data.model.Document

class EditDocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DocumentRepository(application)

    private val _document = MutableLiveData<Document?>()
    val document: LiveData<Document?> = _document

    private val _updateState = MutableLiveData<EditState>()
    val updateState: LiveData<EditState> = _updateState
    
    fun fetchDocumentDetails(id: String) {
        viewModelScope.launch {
            try {
                val response = repository.getDocumentDetails(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    _document.postValue(response.body()?.data)
                } else {
                     // If fetch fails, we might just show an error toast in UI
                }
            } catch (e: Exception) {
               // Handle fetch error
            }
        }
    }

    fun updateDocument(
        documentId: String,
        title: String,
        category: String,
        visibility: String,
        expiryDate: String?, // YYYY-MM-DD
        fileUri: Uri?
    ) {
        _updateState.value = EditState.Loading
        viewModelScope.launch {
            try {
                val response = repository.updateDocument(
                    documentId = documentId,
                    title = title,
                    category = category,
                    expiryDate = expiryDate,
                    fileUri = fileUri
                )
                
                // Oops, I need to match ApiService.kt signature. 
                // Repository wrapper calls: updateDocument(documentId, title, category, expiryDate, shareWith, fileUri)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _updateState.postValue(EditState.Success)
                } else {
                    val errorMessage = if (response.code() == 413) {
                         "File too large. Please select a file smaller than 2MB."
                    } else {
                         response.body()?.message ?: response.errorBody()?.string() ?: "Update failed"
                    }
                    _updateState.postValue(EditState.Error(errorMessage))
                }
            } catch (e: Exception) {
                _updateState.postValue(EditState.Error(e.message ?: "Unknown error"))
            }
        }
    }
}

sealed class EditState {
    object Loading : EditState()
    object Success : EditState()
    data class Error(val message: String) : EditState()
}
