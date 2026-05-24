package com.huskymingle.app

import android.app.Application
import com.huskymingle.app.data.local.AuthDataStore
import com.huskymingle.app.data.local.CirclesStore
import com.huskymingle.app.data.local.CourseCatalogService
import com.huskymingle.app.data.local.StoriesStore
import com.huskymingle.app.data.local.UserPreferences
import com.huskymingle.app.data.network.RetrofitClient

class HuskyMingleApp : Application() {

    lateinit var authDataStore: AuthDataStore
        private set

    lateinit var userPreferences: UserPreferences
        private set

    lateinit var storiesStore: StoriesStore
        private set

    lateinit var circlesStore: CirclesStore
        private set

    lateinit var courseCatalog: CourseCatalogService
        private set

    override fun onCreate() {
        super.onCreate()
        authDataStore = AuthDataStore(this)
        userPreferences = UserPreferences(this)
        storiesStore = StoriesStore(this, userPreferences)
        circlesStore = CirclesStore(userPreferences)
        courseCatalog = CourseCatalogService(this)
        RetrofitClient.init(authDataStore)
    }
}
