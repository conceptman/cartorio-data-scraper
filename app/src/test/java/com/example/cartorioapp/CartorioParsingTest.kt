package com.example.cartorioapp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CartorioParsingTest {

    @Test
    fun `test parseHtml correctly extracts cartorio data`() {
        val html = """
            <html>
            <body>
                <table>
                    <tr>
                        <td>12345</td>
                        <td>Cartorio Silva</td>
                        <td>SP</td>
                        <td>Rua A, 100</td>
                    </tr>
                    <tr>
                        <td>67890</td>
                        <td>Cartorio Santos</td>
                        <td>RJ</td>
                        <td>Av B, 200</td>
                    </tr>
                    <tr>
                        <td>invalid</td>
                        <td>Bad Data</td>
                        <td>XX</td>
                        <td>None</td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()

        val remoteDataSource = RemoteDataSource()
        val result = remoteDataSource.parseHtml(html)

        assertEquals(2, result.size)
        assertEquals("12345", result[0].cns)
        assertEquals("Cartorio Silva", result[0].name)
        assertEquals("SP", result[0].state)
        assertEquals("67890", result[1].cns)
        assertEquals("Cartorio Santos", result[1].name)
        assertEquals("RJ", result[1].state)
    }

    @Test
    fun `test parseHtml with empty html`() {
        val remoteDataSource = RemoteDataSource()
        val result = remoteDataSource.parseHtml("<html><body></body></html>")
        assertTrue(result.isEmpty())
    }
}
