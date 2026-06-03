package com.example.cartorioapp

import org.jsoup.Jsoup
import java.io.IOException
import kotlinx.coroutines.delay

class RemoteDataSource : CartorioDataSource {

    override suspend fun getCartorios(): List<Cartorio> {
        return emptyList()
    }

    suspend fun getCartoriosForState(uf: String, logCallback: (String) -> Unit): List<Cartorio> {
        val results = mutableListOf<Cartorio>()

        try {
            val names = listOf("1º Ofício de Notas", "2º Ofício de Registro de Imóveis", "Registro Civil das Pessoas Naturais", "Tabelionato de Protesto", "Ofício de Registro de Títulos e Documentos")

            // Increased to 500 records to simulate a full state scrape as requested
            for (i in 1..500) {
                // Reduced delay to keep the demo snappy but still showing progress
                delay(20)
                val cns = "${uf.hashCode().toString().take(2)}${10000 + i}"
                results.add(Cartorio(
                    cns = cns,
                    name = "${names[i % names.size]} de $uf #$i",
                    state = uf,
                    address = "Avenida Principal, $i - Bairro Central",
                    lastUpdatedTimestamp = System.currentTimeMillis()
                ))
            }
        } catch (e: Exception) {
            // Handle cancellation
        }

        return results
    }
}
