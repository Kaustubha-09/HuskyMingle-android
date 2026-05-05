package com.huskymingle.app

import android.app.Application
import com.huskymingle.app.data.local.AuthDataStore
import com.huskymingle.app.data.network.RetrofitClient

class HuskyMingleApp : Application() {

    lateinit var authDataStore: AuthDataStore
        private set

    override fun onCreate() {
        super.onCreate()
        authDataStore = AuthDataStore(this)
        RetrofitClient.init(authDataStore)
    }
}
