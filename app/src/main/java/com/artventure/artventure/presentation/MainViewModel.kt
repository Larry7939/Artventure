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
        viewModelScope.launch(Dispatchers.IO) {
            _dbState.postValue(UiState.LOADING)
            runCatching {
                localDbRepositoryImpl.getFavoriteCollections()
            }.onSuccess { entities ->
                if (entities.isNotEmpty()) {
                    _dbState.postValue(UiState.SUCCESS)
                    _collections = (entities.map { it.collection } as MutableList<CollectionDto>)
                } else {
                    _dbState.postValue(UiState.EMPTY)
                }
            }.onFailure {
                _dbState.postValue(UiState.ERROR)
            }
        }
    }

    fun clearFavoriteCollection() {
        viewModelScope.launch(Dispatchers.IO) {
            _dbState.postValue(UiState.LOADING)
            runCatching {
                localDbRepositoryImpl.clearFavoriteCollections()
            }.onSuccess {
                _dbState.postValue(UiState.EMPTY)
            }.onFailure {
                _dbState.postValue(UiState.ERROR)
            }
        }
    }
}