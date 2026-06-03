package com.example.cartorioapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Cartorio::class], version = 1, exportSchema = false)
abstract class CartorioDatabase : RoomDatabase() {
    abstract fun cartorioDao(): CartorioDao

    companion object {
        @Volatile
        private var INSTANCE: CartorioDatabase? = null

        fun getDatabase(context: Context): CartorioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartorioDatabase::class.java,
                    "cartorio_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
