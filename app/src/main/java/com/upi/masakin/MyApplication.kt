package com.upi.masakin

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MasakinApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true

        FirebaseDatabase.getInstance("https://masakin-76b91-default-rtdb.asia-southeast1.firebasedatabase.app")
            .setPersistenceEnabled(true)

        Timber.d("MasakinApplication initialized")
    }
}