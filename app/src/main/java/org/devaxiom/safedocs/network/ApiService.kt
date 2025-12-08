package org.devaxiom.safedocs.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.devaxiom.safedocs.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== Auth ========== //
    @POST("/api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<ApiResponse<LoginData>>

    // ========== Document List & Search ========== //
    @GET("/api/documents")
    suspend fun getDocuments(
        @Query("type") visibility: String,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("expiryFrom") expiryFrom: String? = null,
        @Query("expiryTo") expiryTo: String? = null,
        @Query("familyId") familyId: String? = null
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
        @Query("familyId") familyId: String? = null,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<UploadResponse>>

    @Multipart
    @PUT("/api/documents/{id}")
    suspend fun updateDocument(
        @Path("id") documentId: String,
        @Query("title") title: String? = null,
        @Query("category") category: String? = null,
        @Query("expiryDate") expiryDate: String? = null,
        @Query("shareWith") shareWith: String? = null,
        @Part file: MultipartBody.Part? = null
    ): Response<ApiResponse<Document>>

    @DELETE("/api/documents/{id}")
    suspend fun deleteDocument(@Path("id") documentId: String): Response<ApiResponse<Void>>

    // ========== Document Sharing ========== //
    @POST("/api/documents/{id}/share")
    suspend fun shareDocument(@Path("id") documentId: String, @Body shareRequest: ShareRequest): Response<ApiResponse<Void>>

    @DELETE("/api/documents/{id}/share/{shareId}")
    suspend fun unshareDocument(@Path("id") documentId: String, @Path("shareId") shareId: String): Response<ApiResponse<Void>>

    @GET("/api/documents/{id}/share")
    suspend fun getDocumentShares(@Path("id") documentId: String): Response<ApiResponse<List<DocumentShare>>>

    // ========== Downloading ========== //
    @GET("/api/documents/{id}/download")
    @Streaming // Important for large files
    suspend fun downloadDocument(@Path("id") documentId: String): Response<ResponseBody>

    // ========== Family ========== //
    @GET("/api/family")
    suspend fun getFamilies(): Response<ApiResponse<List<FamilySummary>>>

    @POST("/api/family")
    suspend fun createFamily(@Body request: CreateFamilyRequest): Response<ApiResponse<FamilySummary>>

    @GET("/api/family/{id}")
    suspend fun getFamilyProfile(@Path("id") familyId: String): Response<ApiResponse<FamilyProfile>>

    @PUT("/api/family/{id}")
    suspend fun renameFamily(@Path("id") familyId: String, @Body request: RenameFamilyRequest): Response<ApiResponse<FamilyProfile>>

    @POST("/api/family/{id}/invite")
    suspend fun inviteFamilyMember(@Path("id") familyId: String, @Body request: InviteFamilyRequest): Response<ApiResponse<Void>>

    @DELETE("/api/family/{id}/members/{userId}")
    suspend fun removeFamilyMember(@Path("id") familyId: String, @Path("userId") userId: String): Response<ApiResponse<Void>>

    @POST("/api/family/{id}/leave")
    suspend fun leaveFamily(@Path("id") familyId: String): Response<ApiResponse<Void>>

    // ========== Invitations ========== //
    @GET("/api/family/invites") // Assumption: Endpoint to list pending invites
    suspend fun getInvitations(): Response<ApiResponse<List<FamilyInvitation>>>

    @POST("/api/family/invite/{inviteId}/accept")
    suspend fun acceptInvite(@Path("inviteId") inviteId: String): Response<ApiResponse<Void>>

    @POST("/api/family/invite/{inviteId}/reject")
    suspend fun rejectInvite(@Path("inviteId") inviteId: String): Response<ApiResponse<Void>>


}

// Placeholder for a future share request data class
data class ShareRequest(val email: String)
data class DocumentShare(
    val id: String,
    val email: String,
    val canEdit: Boolean? = null
)

data class UserDto(
    val id: String,
    val name: String?,
    val email: String?
)
