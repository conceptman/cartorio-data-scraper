package com.example.cartorioapp

import android.app.Application
import androidx.work.*
import java.util.concurrent.TimeUnit

class CartorioApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleSync()
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CartorioSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
