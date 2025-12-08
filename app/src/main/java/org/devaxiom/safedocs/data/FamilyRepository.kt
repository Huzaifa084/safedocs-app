package org.devaxiom.safedocs.data

import org.devaxiom.safedocs.data.model.CreateFamilyRequest
import org.devaxiom.safedocs.data.model.InviteFamilyRequest
import org.devaxiom.safedocs.data.model.RenameFamilyRequest
import org.devaxiom.safedocs.network.ApiClient

class FamilyRepository {

    suspend fun getFamilies() = ApiClient.instance.getFamilies()

    suspend fun createFamily(name: String) = 
        ApiClient.instance.createFamily(CreateFamilyRequest(name))

    suspend fun getFamilyProfile(familyId: String) = 
        ApiClient.instance.getFamilyProfile(familyId)

    suspend fun renameFamily(familyId: String, newName: String) = 
        ApiClient.instance.renameFamily(familyId, RenameFamilyRequest(newName))

    suspend fun inviteFamilyMember(familyId: String, email: String) = 
        ApiClient.instance.inviteFamilyMember(familyId, InviteFamilyRequest(email))

    suspend fun acceptFamilyInvite(inviteId: String) = 
        ApiClient.instance.acceptFamilyInvite(inviteId)

    suspend fun rejectFamilyInvite(inviteId: String) = 
        ApiClient.instance.rejectFamilyInvite(inviteId)

    suspend fun removeFamilyMember(familyId: String, userId: String) = 
        ApiClient.instance.removeFamilyMember(familyId, userId)

    suspend fun leaveFamily(familyId: String) = 
        ApiClient.instance.leaveFamily(familyId)
}
