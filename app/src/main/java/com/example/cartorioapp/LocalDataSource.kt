package com.example.cartorioapp

class LocalDataSource(private val cartorioDao: CartorioDao) : CartorioDataSource {
    override suspend fun getCartorios(): List<Cartorio> {
        return cartorioDao.getAll()
    }

    suspend fun saveCartorios(cartorios: List<Cartorio>) {
        cartorioDao.insertAll(cartorios)
    }

    suspend fun clearAll() {
        cartorioDao.deleteAll()
    }
}
