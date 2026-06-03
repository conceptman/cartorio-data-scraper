package com.example.cartorioapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartorios")
data class Cartorio(
    @PrimaryKey val cns: String,
    val name: String,
    val state: String,
    val address: String,
    val lastUpdatedTimestamp: Long
)
