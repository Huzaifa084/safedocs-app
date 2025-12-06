package org.devaxiom.safedocs.network

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("visibility") val visibility: String,
    @SerializedName("storageKey") val storageKey: String,
    @SerializedName("storageFilename") val storageFilename: String,
    @SerializedName("storageSizeBytes") val storageSizeBytes: Long,
    @SerializedName("mimeType") val mimeType: String,
    @SerializedName("ownerName") val ownerName: String
)
