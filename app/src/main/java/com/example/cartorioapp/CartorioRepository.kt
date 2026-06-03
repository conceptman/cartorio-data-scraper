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

    suspend fun syncDataForState(uf: String, logCallback: (String) -> Unit) = withContext(Dispatchers.IO) {
        logCallback("Connecting to portal for $uf...")
        val remoteData = remoteDataSource.getCartoriosForState(uf, logCallback)

        if (remoteData.isNotEmpty()) {
            logCallback("Storing ${remoteData.size} records in Room...")
            localDataSource.saveCartorios(remoteData)
            logCallback("Data saved successfully.")
        } else {
            logCallback("Warning: No data returned from scraper.")
        }
    }

    suspend fun getAllCartorios(): List<Cartorio> = withContext(Dispatchers.IO) {
        localDataSource.getCartorios()
    }
}
