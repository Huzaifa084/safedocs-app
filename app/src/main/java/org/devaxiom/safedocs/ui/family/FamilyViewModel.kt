package org.devaxiom.safedocs.ui.family

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.network.ApiClient

data class FamilyMember(val id: String, val name: String, val email: String)

class FamilyViewModel(application: Application) : AndroidViewModel(application) {
    private val _members = MutableLiveData<List<FamilyMember>>()
    val members: LiveData<List<FamilyMember>> = _members

    fun fetchMembers() {
        viewModelScope.launch {
            try {
                val res = ApiClient.instance.getFamilyMembers()
                if (res.isSuccessful && res.body()?.success == true) {
                    val items = res.body()?.data ?: emptyList()
                    _members.postValue(items.map { FamilyMember(it.id, it.name ?: "", it.email ?: "") })
                }
            } catch (_: Exception) {}
        }
    }

    fun invite(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val res = ApiClient.instance.inviteFamily(email)
                onResult(res.isSuccessful && res.body()?.success == true)
                if (res.isSuccessful) fetchMembers()
            } catch (_: Exception) { onResult(false) }
        }
    }

    fun removeMember(userId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val res = ApiClient.instance.removeFamilyMember(userId)
                onResult(res.isSuccessful && res.body()?.success == true)
                if (res.isSuccessful) fetchMembers()
            } catch (_: Exception) { onResult(false) }
        }
    }
}
