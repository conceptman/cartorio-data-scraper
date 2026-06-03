package com.example.cartorioapp

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CartorioRepositoryTest {

    private val localDataSource = mockk<LocalDataSource>(relaxed = true)
    private val remoteDataSource = mockk<RemoteDataSource>()
    private val repository = CartorioRepository(localDataSource, remoteDataSource)

    @Test
    fun `syncData should fetch from remote and save to local`() = runTest {
        val remoteList = listOf(
            Cartorio("1", "Name 1", "State 1", "Addr 1", 100L)
        )
        coEvery { remoteDataSource.getCartorios() } returns remoteList

        repository.syncData()

        coVerify { remoteDataSource.getCartorios() }
        coVerify { localDataSource.saveCartorios(remoteList) }
    }

    @Test
    fun `getAllCartorios should return data from local`() = runTest {
        val localList = listOf(
            Cartorio("1", "Name 1", "State 1", "Addr 1", 100L)
        )
        coEvery { localDataSource.getCartorios() } returns localList

        val result = repository.getAllCartorios()

        assertEquals(localList, result)
        coVerify { localDataSource.getCartorios() }
    }
}
