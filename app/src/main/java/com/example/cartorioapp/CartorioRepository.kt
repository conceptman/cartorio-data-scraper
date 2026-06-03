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

    suspend fun getAllCartorios(): List<Cartorio> = withContext(Dispatchers.IO) {
        localDataSource.getCartorios()
    }
}
