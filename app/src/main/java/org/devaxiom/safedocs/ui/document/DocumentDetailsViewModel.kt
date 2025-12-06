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
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.DocumentRepository
import org.devaxiom.safedocs.data.model.Document
import java.io.File

class DocumentDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val documentRepository = DocumentRepository(application)

    // LiveData now holds a nullable Document to prevent crashes
    private val _document = MutableLiveData<Document?>()
    val document: LiveData<Document?> = _document

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

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
