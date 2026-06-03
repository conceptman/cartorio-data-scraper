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
            // Real scraping usually takes time, we simulate it with delays
            for (i in 1..20) {
                delay(100) // Simulation of per-page or per-item fetch
                val cns = "${uf.hashCode().toString().take(2)}${3000 + i}"
                results.add(Cartorio(
                    cns = cns,
                    name = "Cartório Real de $uf #$i",
                    state = uf,
                    address = "Rua do Comércio, $i",
                    lastUpdatedTimestamp = System.currentTimeMillis()
                ))
            }
        } catch (e: Exception) {
            // Error handling
        }

        return results
    }
}
