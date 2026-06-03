package com.example.cartorioapp

import org.jsoup.Jsoup
import java.io.IOException

class RemoteDataSource : CartorioDataSource {

    override suspend fun getCartorios(): List<Cartorio> {
        val allResults = mutableListOf<Cartorio>()

        // Simulating scraping multiple states
        val states = listOf("SP", "RJ", "MG")

        for (state in states) {
            // Simulation of fetching and parsing for each state
            val html = simulateHtmlForState(state)
            allResults.addAll(parseHtml(html, state))
        }

        return allResults
    }

    private fun simulateHtmlForState(state: String): String {
        return when (state) {
            "SP" -> """
                <table>
                    <tr><td>11111</td><td>Cartório SP 1</td><td>$state</td><td>Rua SP, 1</td></tr>
                    <tr><td>11112</td><td>Cartório SP 2</td><td>$state</td><td>Rua SP, 2</td></tr>
                </table>
            """.trimIndent()
            "RJ" -> """
                <table>
                    <tr><td>22221</td><td>Cartório RJ 1</td><td>$state</td><td>Av RJ, 1</td></tr>
                </table>
            """.trimIndent()
            "MG" -> """
                <table>
                    <tr><td>33331</td><td>Cartório MG 1</td><td>$state</td><td>Praça MG, 1</td></tr>
                </table>
            """.trimIndent()
            else -> ""
        }
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
