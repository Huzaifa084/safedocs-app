package org.devaxiom.safedocs.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.devaxiom.safedocs.data.model.Document
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== Auth ========== //
    @POST("/api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<ApiResponse<LoginData>>

    // ========== Document List & Search ========== //
    @GET("/api/documents")
    suspend fun getDocuments(
        @Query("type") type: String,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("expiryFrom") expiryFrom: String? = null,
        @Query("expiryTo") expiryTo: String? = null
    ): Response<ApiResponse<PaginatedResponse<Document>>>

    @GET("/api/documents/shared/with-me")
    suspend fun getSharedWithMeDocuments(): Response<ApiResponse<PaginatedResponse<Document>>>

    // ========== Document CRUD ========== //
    @GET("/api/documents/{id}")
    suspend fun getDocumentDetails(@Path("id") documentId: String): Response<ApiResponse<Document>>

    @Multipart
    @POST("/api/documents")
    suspend fun uploadDocument(
        @Query("title") title: String,
        @Query("category") category: String,
        @Query("visibility") visibility: String,
        @Query("shareWith") shareWith: String?,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<UploadResponse>>

    @PUT("/api/documents/{id}")
    suspend fun updateDocument(@Path("id") documentId: String, @Body document: Document): Response<ApiResponse<Document>>

    @DELETE("/api/documents/{id}")
    suspend fun deleteDocument(@Path("id") documentId: String): Response<ApiResponse<Void>>

    // ========== Document Sharing ========== //
    @POST("/api/documents/{id}/share")
    suspend fun shareDocument(@Path("id") documentId: String, @Body shareRequest: ShareRequest): Response<ApiResponse<Void>>

    @DELETE("/api/documents/{id}/share/{shareId}")
    suspend fun unshareDocument(@Path("id") documentId: String, @Path("shareId") shareId: String): Response<ApiResponse<Void>>

    // ========== Downloading ========== //
    @GET("/api/documents/{id}/download")
    @Streaming // Important for large files
    suspend fun downloadDocument(@Path("id") documentId: String): Response<ResponseBody>
}

// Placeholder for a future share request data class
data class ShareRequest(val email: String)
