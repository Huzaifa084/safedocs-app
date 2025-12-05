package org.devaxiom.safedocs

import android.app.Application
import org.devaxiom.safedocs.network.ApiClient

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
    }
}
