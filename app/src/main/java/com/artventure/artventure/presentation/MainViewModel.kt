package com.artventure.artventure.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.data.service.LocalDbRepository
import com.artventure.artventure.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val localDbRepositoryImpl: LocalDbRepository) :
    ViewModel() {

    private var _dbState = MutableLiveData<UiState>()
    val dbState: LiveData<UiState>
        get() = _dbState

    private var _collections = mutableListOf<CollectionDto>()
    val collections: List<CollectionDto>
        get() = _collections

    private val _bottomNavVisibility: MutableLiveData<Int> = MutableLiveData<Int>(View.VISIBLE)
    val bottomNavVisibility: LiveData<Int>
        get() = _bottomNavVisibility

    fun setBottomNavVisibility(visibility: Int) {
        _bottomNavVisibility.value = visibility
    }


    fun getFavoriteCollection() {
        viewModelScope.launch {
            _dbState.value = UiState.LOADING
            runCatching {
                withContext(Dispatchers.IO) {
                    localDbRepositoryImpl.getFavoriteCollections()
                }
            }.onSuccess { entities ->
                if (entities.isNotEmpty()) {
                    _collections = (entities.map { it.collection } as MutableList<CollectionDto>)
                    _dbState.value = UiState.SUCCESS
                } else {
                    _dbState.value = UiState.EMPTY
                }
            }.onFailure {
                _dbState.value = UiState.ERROR
            }
        }
    }

    fun clearFavoriteCollection() {
        viewModelScope.launch {
            _dbState.value = UiState.LOADING
            runCatching {
                withContext(Dispatchers.IO) {
                    localDbRepositoryImpl.clearFavoriteCollections()
                }
            }.onSuccess {
                _dbState.value = UiState.EMPTY
            }.onFailure {
                _dbState.value = UiState.ERROR
            }
        }
    }
}