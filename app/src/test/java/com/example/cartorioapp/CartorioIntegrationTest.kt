package com.example.cartorioapp

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

class CartorioIntegrationTest {

    @Test
    fun `test end-to-end flow from Remote Fetch to JSON Export`() = runTest {
        // 1. Mock Data Sources
        val mockLocal = mockk<LocalDataSource>(relaxed = true)
        val remoteDataSource = RemoteDataSource() // Use real one to test parsing
        val repository = CartorioRepository(mockLocal, remoteDataSource)

        // 2. Mock FileManager with a temporary directory
        val tempDir = Files.createTempDirectory("CartorioData").toFile()
        val mockContext = mockk<android.content.Context>()
        val fileManager = object : FileManager(mockContext) {
            override fun getCartorioDataFolder(): File = tempDir
        }

        // 3. Sync Data (Fetch -> Parsing -> Room Insert mock)
        repository.syncData()

        // Capture data for export (simulation of sync result)
        val data = remoteDataSource.getCartorios()
        assertEquals(3, data.size)

        // 4. JSON Export
        val fileName = "test_export.json"
        val exportedFile = fileManager.exportToJson(data, fileName)

        assertNotNull(exportedFile)
        assertEquals(true, exportedFile?.exists())

        // 5. Verify JSON content
        val content = exportedFile?.readText()
        assertNotNull(content)
        assertEquals(true, content?.contains("11223"))
        assertEquals(true, content?.contains("Cartório do 1º Ofício"))

        // Cleanup
        tempDir.deleteRecursively()
    }
}
