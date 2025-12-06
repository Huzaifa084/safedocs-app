package org.devaxiom.safedocs.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.devaxiom.safedocs.network.ApiClient
import org.devaxiom.safedocs.network.ShareRequest
import java.io.File

class DocumentRepository(private val context: Context) {

    // ========== Document List & Search ========== //
    suspend fun getDocuments(
        type: String,
        search: String? = null,
        category: String? = null,
        expiryFrom: String? = null,
        expiryTo: String? = null
    ) = ApiClient.instance.getDocuments(type, search, category, expiryFrom, expiryTo)

    suspend fun getSharedWithMeDocuments() = ApiClient.instance.getSharedWithMeDocuments()

    // ========== Document CRUD & Management ========== //
    suspend fun getDocumentDetails(documentId: String) = ApiClient.instance.getDocumentDetails(documentId)

    suspend fun deleteDocument(documentId: String) = ApiClient.instance.deleteDocument(documentId)

    suspend fun downloadDocument(documentId: String) = ApiClient.instance.downloadDocument(documentId)

    suspend fun uploadDocument(
        title: String,
        category: String,
        visibility: String,
        shareWith: String?,
        fileUri: Uri
    ) = ApiClient.instance.uploadDocument(
        title = title,
        category = category,
        visibility = visibility,
        shareWith = shareWith,
        file = prepareFilePart(fileUri)
    )

    suspend fun shareDocument(documentId: String, email: String) = ApiClient.instance.shareDocument(documentId, ShareRequest(email))

    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part {
        var fileName = "uploaded_file"
        context.contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }

        val tempFile = File(context.cacheDir, fileName).also {
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                it.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        val requestFile = tempFile.asRequestBody(
            context.contentResolver.getType(fileUri)?.toMediaTypeOrNull()
        )

        return MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
    }
}
