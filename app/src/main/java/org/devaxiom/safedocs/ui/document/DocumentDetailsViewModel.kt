package org.devaxiom.safedocs.ui.document

import android.app.Application
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.DocumentRepository
import org.devaxiom.safedocs.data.model.Document
import org.devaxiom.safedocs.network.DocumentShare
import java.io.File

class DocumentDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val documentRepository = DocumentRepository(application)

    // LiveData now holds a nullable Document to prevent crashes
    private val _document = MutableLiveData<Document?>()
    val document: LiveData<Document?> = _document

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _shares = MutableLiveData<List<DocumentShare>>()
    val shares: LiveData<List<DocumentShare>> = _shares

    private val _downloadState = MutableLiveData<DownloadState>()
    val downloadState: LiveData<DownloadState> = _downloadState

    fun fetchDocumentDetails(documentId: String) {
        viewModelScope.launch {
            try {
                val response = documentRepository.getDocumentDetails(documentId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _document.postValue(response.body()?.data)
                } else {
                    _error.postValue(response.body()?.message ?: "Error fetching document details")
                }
            } catch (e: Exception) {
                _error.postValue(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun fetchShares(documentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = documentRepository.getDocumentShares(documentId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _shares.postValue(response.body()?.data ?: emptyList())
                }
            } catch (_: Exception) { }
        }
    }

    fun addShare(documentId: String, email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = documentRepository.shareDocument(documentId, email)
                val ok = response.isSuccessful && (response.body()?.success == true)
                onResult(ok)
                if (ok) fetchShares(documentId)
            } catch (_: Exception) { onResult(false) }
        }
    }

    fun removeShare(documentId: String, shareId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = documentRepository.unshareDocument(documentId, shareId)
                val ok = response.isSuccessful && (response.body()?.success == true)
                onResult(ok)
                if (ok) fetchShares(documentId)
            } catch (_: Exception) { onResult(false) }
        }
    }

    fun downloadDocument(document: Document) {
        _downloadState.value = DownloadState.Loading
        viewModelScope.launch {
            try {
                val response = documentRepository.downloadDocument(document.id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        saveFileToDownloads(document, body.byteStream())
                        _downloadState.postValue(DownloadState.Success)
                    } else {
                        _downloadState.postValue(DownloadState.Error("File not found on server"))
                    }
                } else {
                    _downloadState.postValue(DownloadState.Error(response.message() ?: "Download failed"))
                }
            } catch (e: Exception) {
                _downloadState.postValue(DownloadState.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    fun deleteDocument(documentId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = documentRepository.deleteDocument(documentId)
                val ok = response.isSuccessful && (response.body()?.success == true)
                onResult(ok, response.body()?.message)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun updateDocument(updated: Document, expiryDateStr: String? = null, shareWith: String? = null, fileUri: android.net.Uri? = null, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = documentRepository.updateDocument(
                    documentId = updated.id,
                    title = updated.title,
                    category = updated.category,
                    expiryDate = expiryDateStr ?: updated.expiryDate?.let { java.text.SimpleDateFormat("yyyy-MM-dd").format(it) },
                    shareWith = shareWith,
                    fileUri = fileUri
                )
                val ok = response.isSuccessful && (response.body()?.success == true)
                if (ok) {
                    response.body()?.data?.let { _document.postValue(it) }
                }
                onResult(ok, response.body()?.message)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    private fun saveFileToDownloads(document: Document, inputStream: java.io.InputStream) {
        val fileName = document.storageFilename ?: "downloaded_file_${System.currentTimeMillis()}"
        val mimeType = document.storageMimeType
        val resolver = getApplication<Application>().contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/SafeDocs")
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } else {
            // Fallback for older Android versions (requires WRITE_EXTERNAL_STORAGE permission)
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val safeDocsDir = File(downloadsDir, "SafeDocs")
            if (!safeDocsDir.exists()) {
                safeDocsDir.mkdirs()
            }
            val file = File(safeDocsDir, fileName)
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}

sealed class DownloadState {
    object Loading : DownloadState()
    object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}
