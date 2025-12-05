package org.devaxiom.safedocs.network

import org.devaxiom.safedocs.data.model.Document
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<ApiResponse<LoginData>>

    @GET("/api/documents")
    suspend fun getDocuments(@Query("type") type: String): Response<ApiResponse<PaginatedResponse<Document>>>
}
