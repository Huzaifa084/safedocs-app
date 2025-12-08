package org.devaxiom.safedocs.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class FamilySummary(
    @SerializedName("familyId") val id: String,
    @SerializedName("familyName") val familyName: String,
    @SerializedName("role") val role: String, // "HEAD" or "MEMBER"
    @SerializedName("memberCount") val memberCount: Int,
    @SerializedName("createdAt") val createdAt: Date? = null
)

data class FamilyProfile(
    @SerializedName("id") val id: String,
    @SerializedName("familyName") val familyName: String,
    @SerializedName("headUserId") val headUserId: String,
    @SerializedName("headName") val headName: String?,
    @SerializedName("members") val members: List<FamilyMember>
)

data class FamilyMember(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("role") val role: String,
    @SerializedName("active") val active: Boolean = true
)

data class FamilyInvitation(
    @SerializedName("inviteId") val inviteId: String,
    @SerializedName("familyId") val familyId: String,
    @SerializedName("invitedEmail") val invitedEmail: String,
    @SerializedName("status") val status: String, // PENDING, ACCEPTED, REJECTED
    @SerializedName("createdAt") val createdAt: Date? = null
)

data class CreateFamilyRequest(
    @SerializedName("familyName") val familyName: String
)

data class InviteFamilyRequest(
    @SerializedName("email") val email: String
)

data class RenameFamilyRequest(
    @SerializedName("familyName") val familyName: String
)
