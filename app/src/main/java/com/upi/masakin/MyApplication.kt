package com.upi.masakin

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MasakinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        FirebaseDatabase.getInstance("https://masakin-76b91-default-rtdb.asia-southeast1.firebasedatabase.app")
            .setPersistenceEnabled(true)
    }
}
