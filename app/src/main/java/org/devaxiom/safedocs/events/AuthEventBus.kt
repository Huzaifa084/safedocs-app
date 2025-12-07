package org.devaxiom.safedocs.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object AuthEventBus {
    private val _unauthenticated = MutableLiveData<Unit>()
    val unauthenticated: LiveData<Unit> = _unauthenticated

    fun notifyUnauthenticated() {
        _unauthenticated.postValue(Unit)
    }
}
