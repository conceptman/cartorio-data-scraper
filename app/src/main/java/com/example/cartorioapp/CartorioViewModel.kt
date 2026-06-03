package com.example.cartorioapp

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class CartorioViewModel(private val repository: CartorioRepository, private val fileManager: FileManager) : ViewModel() {

    private val _allCartorios = MutableLiveData<List<Cartorio>>()
    private val _filteredCartorios = MutableLiveData<List<Cartorio>>()
    val cartorios: LiveData<List<Cartorio>> = _filteredCartorios

    private val _isSyncing = MutableLiveData<Boolean>(false)
    val isSyncing: LiveData<Boolean> = _isSyncing

    private val _statusMessage = MutableLiveData<String>("Idle")
    val statusMessage: LiveData<String> = _statusMessage

    private val _exportedFile = MutableLiveData<File?>()
    val exportedFile: LiveData<File?> = _exportedFile

    private var scrapeJob: Job? = null

    fun loadData() {
        viewModelScope.launch {
            val data = repository.getAllCartorios()
            _allCartorios.value = data
            _filteredCartorios.value = data
        }
    }

    fun startScrape(uf: String) {
        scrapeJob = viewModelScope.launch {
            _isSyncing.value = true
            _statusMessage.value = "Scraping $uf..."
            try {
                repository.syncDataForState(uf) { /* No internal logs to UI as requested */ }
                _statusMessage.value = "Finished: $uf"
                val allData = repository.getAllCartorios()
                val file = fileManager.exportToJson(allData, "cartorio_data_$uf.json")
                _exportedFile.value = file
                loadData()
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun stopScrape() {
        scrapeJob?.cancel()
        _isSyncing.value = false
        _statusMessage.value = "Stopped"
    }

    fun clearData() {
        viewModelScope.launch {
            repository.clearAll()
            loadData()
        }
    }

    fun getLastExport(): File? = fileManager.getLastExportedFile()

    class Factory(private val repository: CartorioRepository, private val fileManager: FileManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartorioViewModel(repository, fileManager) as T
        }
    }
}
