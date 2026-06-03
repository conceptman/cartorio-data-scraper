package com.example.cartorioapp

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class CartorioViewModel(private val repository: CartorioRepository) : ViewModel() {

    private val _cartorios = MutableLiveData<List<Cartorio>>()
    val cartorios: LiveData<List<Cartorio>> = _cartorios

    private val _isSyncing = MutableLiveData<Boolean>(false)
    val isSyncing: LiveData<Boolean> = _isSyncing

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _logs = MutableLiveData<String>("")
    val logs: LiveData<String> = _logs

    fun addLog(message: String) {
        val currentLogs = _logs.value ?: ""
        _logs.postValue("$currentLogs\n> $message")
    }

    fun loadData() {
        viewModelScope.launch {
            _cartorios.value = repository.getAllCartorios()
        }
    }

    fun scrapeState(uf: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            _statusMessage.value = "Scraping $uf..."
            addLog("Starting scraper for state: $uf")
            try {
                repository.syncDataForState(uf) { log -> addLog(log) }
                _statusMessage.value = "Scrape $uf completed!"
                addLog("Successfully completed scraping for $uf.")
                loadData()
            } catch (e: Exception) {
                _statusMessage.value = "Scrape $uf failed"
                addLog("ERROR: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    class Factory(private val repository: CartorioRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartorioViewModel(repository) as T
        }
    }
}
