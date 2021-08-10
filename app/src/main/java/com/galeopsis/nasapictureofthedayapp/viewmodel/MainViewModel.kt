package com.galeopsis.nasapictureofthedayapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galeopsis.nasapictureofthedayapp.model.repository.NasaRepository
import com.galeopsis.nasapictureofthedayapp.utils.LoadingState
import kotlinx.coroutines.launch

class MainViewModel(
    private val nasaRepository: NasaRepository
) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    val data = nasaRepository.data

    fun fetchData(date: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = LoadingState.LOADING
                nasaRepository.refresh(date)
                _loadingState.value = LoadingState.LOADED
            } catch (e: Exception) {
                _loadingState.value = LoadingState.error(e.message)
            }
        }
    }
}