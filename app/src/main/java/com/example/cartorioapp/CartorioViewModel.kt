package com.example.cartorioapp

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.io.File

class CartorioViewModel(private val repository: CartorioRepository, private val fileManager: FileManager) : ViewModel() {

    private val _allCartorios = MutableLiveData<List<Cartorio>>()

    private val _filteredCartorios = MutableLiveData<List<Cartorio>>()
    val cartorios: LiveData<List<Cartorio>> = _filteredCartorios

    private val _isSyncing = MutableLiveData<Boolean>(false)
    val isSyncing: LiveData<Boolean> = _isSyncing

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _logs = MutableLiveData<String>("")
    val logs: LiveData<String> = _logs

    private val _exportedFile = MutableLiveData<File?>()
    val exportedFile: LiveData<File?> = _exportedFile

    fun addLog(message: String) {
        val currentLogs = _logs.value ?: ""
        _logs.postValue("$currentLogs\n> $message")
    }

    fun loadData() {
        viewModelScope.launch {
            val data = repository.getAllCartorios()
            _allCartorios.value = data
            _filteredCartorios.value = data
        }
    }

    fun filter(query: String) {
        val all = _allCartorios.value ?: emptyList()
        if (query.isEmpty()) {
            _filteredCartorios.value = all
        } else {
            _filteredCartorios.value = all.filter {
                it.name.contains(query, ignoreCase = true) || it.cns.contains(query)
            }
        }
    }

    fun scrapeState(uf: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            _statusMessage.value = "Scraping $uf..."
            addLog("--- NEW SESSION ---")
            addLog("Targeting State: $uf")
            try {
                repository.syncDataForState(uf) { log -> addLog(log) }
                _statusMessage.value = "Success: $uf"
                addLog("Scrape finished. Exporting data...")
                val allData = repository.getAllCartorios()
                val file = fileManager.exportToJson(allData, "export_${uf}_${System.currentTimeMillis()}.json")
                _exportedFile.value = file
                addLog("File exported to: ${file?.name}")
                loadData()
            } catch (e: Exception) {
                _statusMessage.value = "Error: $uf"
                addLog("CRITICAL ERROR: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            repository.clearAll()
            addLog("Database cleared.")
            loadData()
        }
    }

    class Factory(private val repository: CartorioRepository, private val fileManager: FileManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartorioViewModel(repository, fileManager) as T
        }
    }
}
