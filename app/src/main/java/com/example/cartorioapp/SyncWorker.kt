package com.example.cartorioapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = CartorioDatabase.getDatabase(applicationContext)
        val dao = database.cartorioDao()
        val repository = CartorioRepository(
            LocalDataSource(dao),
            RemoteDataSource()
        )
        val fileManager = FileManager(applicationContext)

        return try {
            repository.syncData()

            // Optional: Export to JSON after every sync
            val allData = repository.getAllCartorios()
            fileManager.exportToJson(allData, "cartorios_backup_${System.currentTimeMillis()}.json")

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
