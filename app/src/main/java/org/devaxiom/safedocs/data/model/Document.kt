package org.devaxiom.safedocs.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Document(
    @SerializedName("id") val id: Long,
    @SerializedName("public_id") val publicId: String,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("expiry_date") val expiryDate: Date?,
    @SerializedName("visibility") val visibility: String,
    @SerializedName("status") val status: String,
    @SerializedName("storage_filename") val storageFilename: String?,
    @SerializedName("storage_mime_type") val storageMimeType: String?,
    @SerializedName("created_date") val createdDate: Date
)
