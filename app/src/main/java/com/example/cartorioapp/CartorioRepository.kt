package com.example.cartorioapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartorioRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun syncData() = withContext(Dispatchers.IO) {
        val remoteData = remoteDataSource.getCartorios()
        if (remoteData.isNotEmpty()) {
            localDataSource.saveCartorios(remoteData)
        }
    }

    suspend fun syncDataWithLogs(logCallback: (String) -> Unit) = withContext(Dispatchers.IO) {
        logCallback("Initializing remote fetch...")
        val remoteData = remoteDataSource.getCartoriosWithLogs(logCallback)
        logCallback("Fetched ${remoteData.size} total records.")

        if (remoteData.isNotEmpty()) {
            logCallback("Saving to local database...")
            localDataSource.saveCartorios(remoteData)
            logCallback("Database update complete.")
        } else {
            logCallback("No new data found to save.")
        }
    }

    suspend fun getAllCartorios(): List<Cartorio> = withContext(Dispatchers.IO) {
        localDataSource.getCartorios()
    }
}
