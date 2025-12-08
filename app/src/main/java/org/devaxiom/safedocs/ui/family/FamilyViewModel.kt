package org.devaxiom.safedocs.ui.family

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.FamilyRepository
import org.devaxiom.safedocs.data.model.FamilyProfile
import org.devaxiom.safedocs.data.model.FamilySummary

class FamilyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FamilyRepository()

    private val _families = MutableLiveData<List<FamilySummary>>()
    val families: LiveData<List<FamilySummary>> = _families

    private val _familyProfile = MutableLiveData<FamilyProfile?>()
    val familyProfile: LiveData<FamilyProfile?> = _familyProfile

    private val _operationState = MutableLiveData<FamilyOperationState>()
    val operationState: LiveData<FamilyOperationState> = _operationState

    fun fetchFamilies() {
        _operationState.value = FamilyOperationState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getFamilies()
                if (response.isSuccessful && response.body()?.success == true) {
                    _families.postValue(response.body()?.data ?: emptyList())
                    _operationState.postValue(FamilyOperationState.Idle)
                } else {
                    _operationState.postValue(FamilyOperationState.Error(response.message()))
                }
            } catch (e: Exception) {
                _operationState.postValue(FamilyOperationState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun createFamily(name: String) {
        _operationState.value = FamilyOperationState.Loading
        viewModelScope.launch {
            try {
                val response = repository.createFamily(name)
                if (response.isSuccessful && response.body()?.success == true) {
                    fetchFamilies() // Refresh list
                    _operationState.postValue(FamilyOperationState.Success("Family created"))
                } else {
                     _operationState.postValue(FamilyOperationState.Error(response.body()?.message ?: "Failed to create family"))
                }
            } catch (e: Exception) {
                _operationState.postValue(FamilyOperationState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getFamilyProfile(id: String) {
        _operationState.value = FamilyOperationState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getFamilyProfile(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    _familyProfile.postValue(response.body()?.data)
                    _operationState.postValue(FamilyOperationState.Idle)
                } else {
                     _operationState.postValue(FamilyOperationState.Error(response.body()?.message ?: "Failed to load profile"))
                }
            } catch (e: Exception) {
                _operationState.postValue(FamilyOperationState.Error(e.message ?: "Unknown error"))
            }
        }
    }
    
    fun inviteMember(familyId: String, email: String) {
        viewModelScope.launch {
            try {
                val response = repository.inviteFamilyMember(familyId, email)
                if (response.isSuccessful && response.body()?.success == true) {
                     _operationState.postValue(FamilyOperationState.Success("Invite sent"))
                } else {
                     _operationState.postValue(FamilyOperationState.Error(response.body()?.message ?: "Invite failed"))
                }
            } catch (e: Exception) {
                 _operationState.postValue(FamilyOperationState.Error(e.message ?: "Unknown error"))
            }
        }
    }
    
    fun removeMember(familyId: String, userId: String) {
        viewModelScope.launch {
             try {
                val response = repository.removeFamilyMember(familyId, userId)
                if (response.isSuccessful && response.body()?.success == true) {
                     getFamilyProfile(familyId) // Refresh profile
                     _operationState.postValue(FamilyOperationState.Success("Member removed"))
                } else {
                     _operationState.postValue(FamilyOperationState.Error(response.body()?.message ?: "Remove failed"))
                }
            } catch (e: Exception) {
                 _operationState.postValue(FamilyOperationState.Error(e.message ?: "Unknown error"))
            }
        }
    }
    
    fun leaveFamily(familyId: String) {
        viewModelScope.launch {
            try {
                val response = repository.leaveFamily(familyId)
                if (response.isSuccessful && response.body()?.success == true) {
                     _operationState.postValue(FamilyOperationState.Success("Left family"))
                     // Navigation back logic usually handled in Fragment observing Success
                } else {
                     _operationState.postValue(FamilyOperationState.Error(response.body()?.message ?: "Leave failed"))
                }
            } catch (e: Exception) {
                 _operationState.postValue(FamilyOperationState.Error(e.message ?: "Unknown error"))
            }
        }
    }
    
    fun renameFamily(familyId: String, newName: String) {
        viewModelScope.launch {
            try {
                val response = repository.renameFamily(familyId, newName)
                if (response.isSuccessful && response.body()?.success == true) {
                     getFamilyProfile(familyId)
                     // Also refresh list if needed, or just update UI
                     fetchFamilies()
                     _operationState.postValue(FamilyOperationState.Success("Family renamed"))
                } else {
                     _operationState.postValue(FamilyOperationState.Error(response.body()?.message ?: "Rename failed"))
                }
            } catch (e: Exception) {
                 _operationState.postValue(FamilyOperationState.Error(e.message ?: "Unknown error"))
            }
        }
    }
}

sealed class FamilyOperationState {
    object Idle : FamilyOperationState()
    object Loading : FamilyOperationState()
    data class Success(val message: String) : FamilyOperationState()
    data class Error(val message: String) : FamilyOperationState()
}
