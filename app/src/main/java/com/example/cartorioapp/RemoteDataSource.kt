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

            // Increased to 1000 records per state as requested
            for (i in 1..1000) {
                // Minor delay to show progress in UI
                delay(10)
                val cns = "${uf.hashCode().toString().take(2)}${20000 + i}"
                results.add(Cartorio(
                    cns = cns,
                    name = "${names[i % names.size]} de $uf #$i",
                    state = uf,
                    address = "Avenida Principal, $i - Setor Administrativo",
                    lastUpdatedTimestamp = System.currentTimeMillis()
                ))
            }
        } catch (e: Exception) {
            // Handle cancellation
        }

        return results
    }
}
