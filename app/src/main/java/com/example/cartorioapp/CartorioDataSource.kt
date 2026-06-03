package com.example.cartorioapp

interface CartorioDataSource {
    suspend fun getCartorios(): List<Cartorio>
}
