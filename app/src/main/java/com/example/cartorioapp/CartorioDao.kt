package com.example.cartorioapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartorioDao {
    @Query("SELECT * FROM cartorios")
    suspend fun getAll(): List<Cartorio>

    @Query("SELECT * FROM cartorios WHERE cns = :cns")
    suspend fun getByCns(cns: String): Cartorio?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cartorios: List<Cartorio>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartorio: Cartorio)

    @Query("DELETE FROM cartorios")
    suspend fun deleteAll()
}
