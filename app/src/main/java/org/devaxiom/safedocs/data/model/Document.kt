package org.devaxiom.safedocs.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Document(
    @SerializedName("id") val id: String, // Corrected from Long to String
    @SerializedName("public_id") val publicId: String? = null, // Made nullable
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("expiry_date") val expiryDate: Date? = null,
    @SerializedName("visibility") val visibility: String,
    @SerializedName("status") val status: String? = null,
    @SerializedName("storage_filename") val storageFilename: String? = null,
    @SerializedName("storage_mime_type") val storageMimeType: String? = null,
    @SerializedName("created_date") val createdDate: Date? = null,
    @SerializedName("ownerName") val ownerName: String? = null // Added from API response
)
