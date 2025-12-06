package org.devaxiom.safedocs.ui.upload

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.DocumentRepository

class UploadViewModel(application: Application) : AndroidViewModel(application) {

    private val documentRepository = DocumentRepository(application)

    private val _uploadState = MutableLiveData<UploadState>()
    val uploadState: LiveData<UploadState> = _uploadState

    fun uploadDocument(
        title: String,
        category: String,
        visibility: String,
        shareWith: String?,
        fileUri: Uri
    ) {
        _uploadState.value = UploadState.Loading
        viewModelScope.launch {
            try {
                val response = documentRepository.uploadDocument(title, category, visibility, shareWith, fileUri)
                if (response.isSuccessful && response.body()?.success == true) {
                    _uploadState.postValue(UploadState.Success)
                } else {
                    val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Upload failed"
                    _uploadState.postValue(UploadState.Error(errorMessage))
                }
            } catch (e: Exception) {
                _uploadState.postValue(UploadState.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }
}

sealed class UploadState {
    object Loading : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}
