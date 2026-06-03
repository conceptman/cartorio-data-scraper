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

    fun loadData() {
        viewModelScope.launch {
            _cartorios.value = repository.getAllCartorios()
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _isSyncing.value = true
            _statusMessage.value = "Starting sync..."
            try {
                repository.syncData()
                _statusMessage.value = "Sync completed!"
                loadData()
            } catch (e: Exception) {
                _statusMessage.value = "Sync failed: ${e.message}"
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
