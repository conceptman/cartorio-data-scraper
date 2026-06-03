package com.example.cartorioapp

import android.util.Log
import org.jsoup.Jsoup
import java.io.IOException

class RemoteDataSource : CartorioDataSource {

    private val TAG = "RemoteDataSource"

    override suspend fun getCartorios(): List<Cartorio> {
        return getCartoriosWithLogs { }
    }

    suspend fun getCartoriosWithLogs(logCallback: (String) -> Unit): List<Cartorio> {
        val allResults = mutableListOf<Cartorio>()
        val states = listOf("SP", "RJ", "MG", "RS", "BA")

        for (state in states) {
            logCallback("Scanning state: $state...")
            try {
                // Simulate network delay
                kotlinx.coroutines.delay(500)

                val html = simulateHtmlForState(state)
                val parsed = parseHtml(html, state)

                logCallback("Found ${parsed.size} records in $state.")
                allResults.addAll(parsed)
            } catch (e: Exception) {
                logCallback("FAILED to scan $state: ${e.message}")
            }
        }

        return allResults
    }

    private fun simulateHtmlForState(state: String): String {
        val sb = StringBuilder("<table>")
        for (i in 1..10) {
            val cns = "${state.hashCode().toString().take(2)}${1000 + i}"
            sb.append("<tr><td>$cns</td><td>Cartório $state $i</td><td>$state</td><td>Endereço de Teste $i, $state</td></tr>")
        }
        sb.append("</table>")
        return sb.toString()
    }

    fun parseHtml(html: String, state: String = "Unknown"): List<Cartorio> {
        val list = mutableListOf<Cartorio>()
        val doc = Jsoup.parse(html)

        val rows = doc.select("tr")
        for (row in rows) {
            val cols = row.select("td")
            if (cols.size >= 4) {
                val cns = cols[0].text().trim()
                val name = cols[1].text().trim()
                val parsedState = cols[2].text().trim()
                val address = cols[3].text().trim()

                if (cns.isNotEmpty() && cns.all { it.isDigit() }) {
                    list.add(Cartorio(
                        cns = cns,
                        name = name,
                        state = if (parsedState.isEmpty()) state else parsedState,
                        address = address,
                        lastUpdatedTimestamp = System.currentTimeMillis()
                    ))
                }
            }
        }
        return list
    }
}
