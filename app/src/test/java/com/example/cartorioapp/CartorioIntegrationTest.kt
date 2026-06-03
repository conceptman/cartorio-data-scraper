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
    fun `test end-to-end flow`() = runTest {
        // 1. Mock Data Sources
        val mockLocal = mockk<LocalDataSource>(relaxed = true)
        val remoteDataSource = RemoteDataSource()
        val repository = CartorioRepository(mockLocal, remoteDataSource)

        // 2. Mock FileManager with a temporary directory
        val tempDir = Files.createTempDirectory("CartorioData").toFile()
        val mockContext = mockk<android.content.Context>()
        val fileManager = object : FileManager(mockContext) {
            override fun getCartorioDataFolder(): File = tempDir
        }

        // 3. Sync Data
        repository.syncDataForState("SP") { }

        // 4. Capture data
        val data = remoteDataSource.getCartoriosForState("SP") { }
        assertEquals(20, data.size)

        // 5. JSON Export
        val fileName = "test_export.json"
        val exportedFile = fileManager.exportToJson(data, fileName)

        assertNotNull(exportedFile)
        assertEquals(true, exportedFile?.exists())

        // Cleanup
        tempDir.deleteRecursively()
    }
}
