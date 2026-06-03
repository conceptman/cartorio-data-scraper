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
        logCallback("Protocol initiated for $uf.")
        val remoteData = remoteDataSource.getCartoriosForState(uf, logCallback)

        if (remoteData.isNotEmpty()) {
            logCallback("Committing ${remoteData.size} records to Room DB...")
            localDataSource.saveCartorios(remoteData)
            logCallback("Transaction complete.")
        } else {
            logCallback("Result: Empty dataset.")
        }
    }

    suspend fun getAllCartorios(): List<Cartorio> = withContext(Dispatchers.IO) {
        localDataSource.getCartorios()
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        localDataSource.clearAll()
    }
}
