package com.artventure.artventure.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artventure.artventure.data.local.CollectionEntity
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.data.service.LocalDbRepository
import com.artventure.artventure.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(private val localDbRepositoryImpl: LocalDbRepository) :
    ViewModel() {

    private var _dbState = MutableLiveData<UiState>()
    val dbState: LiveData<UiState>
        get() = _dbState

    fun addFavoriteCollection(collectionDto: CollectionDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbState.postValue(UiState.LOADING)
            runCatching {
                localDbRepositoryImpl.addFavoriteCollections(
                    CollectionEntity(collection = collectionDto)
                )
            }.onSuccess {
                _dbState.postValue(UiState.SUCCESS)
            }.onFailure {
                Timber.e(it.cause)
                Timber.e(it.message)
                Timber.e(it.localizedMessage)
                _dbState.postValue(UiState.ERROR)
            }
        }
    }

}