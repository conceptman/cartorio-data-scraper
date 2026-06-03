package com.example.cartorioapp

import org.jsoup.Jsoup
import java.io.IOException

class RemoteDataSource : CartorioDataSource {

    override suspend fun getCartorios(): List<Cartorio> {
        // Simulated HTML content for a Brazilian notary portal
        val simulatedHtml = """
            <html>
            <body>
                <h1>Portal de Cartórios</h1>
                <table>
                    <thead>
                        <tr><th>CNS</th><th>Nome</th><th>UF</th><th>Endereço</th></tr>
                    </thead>
                    <tbody>
                        <tr><td>11223</td><td>Cartório do 1º Ofício</td><td>SP</td><td>Rua Principal, 10</td></tr>
                        <tr><td>33445</td><td>Cartório de Notas e Protestos</td><td>RJ</td><td>Avenida Central, 500</td></tr>
                        <tr><td>55667</td><td>Registro Civil das Pessoas Naturais</td><td>MG</td><td>Praça da Matriz, s/n</td></tr>
                    </tbody>
                </table>
            </body>
            </html>
        """.trimIndent()

        // In a real implementation, we would fetch from a URL:
        // val doc = Jsoup.connect("https://some-brazilian-portal.gov.br").get()
        // val html = doc.html()

        return parseHtml(simulatedHtml)
    }

    fun parseHtml(html: String): List<Cartorio> {
        val list = mutableListOf<Cartorio>()
        val doc = Jsoup.parse(html)

        val rows = doc.select("table tbody tr")
        for (row in rows) {
            val cols = row.select("td")
            if (cols.size >= 4) {
                val cns = cols[0].text().trim()
                val name = cols[1].text().trim()
                val state = cols[2].text().trim()
                val address = cols[3].text().trim()

                if (cns.isNotEmpty() && cns.all { it.isDigit() }) {
                    list.add(Cartorio(
                        cns = cns,
                        name = name,
                        state = state,
                        address = address,
                        lastUpdatedTimestamp = System.currentTimeMillis()
                    ))
                }
            }
        }
        return list
    }
}
