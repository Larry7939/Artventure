package com.artventure.artventure.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.domain.SearchRepository
import com.artventure.artventure.util.ListLiveData
import com.artventure.artventure.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepositoryImpl: SearchRepository) :
    ViewModel() {
    private var _isSearchEnable = MutableLiveData(false)
    val isSearchEnable:LiveData<Boolean>
        get() = _isSearchEnable
    val searchWord: MutableLiveData<String> = MutableLiveData<String>()

    private var _collections = ListLiveData<CollectionDto>()
    val collections: ListLiveData<CollectionDto>
        get() = _collections

    private var _searchState = MutableLiveData<UiState>()
    val searchState: LiveData<UiState>
        get() = _searchState

    private var _totalCount = 0
    val totalCount: Int
        get() = _totalCount

    var code = ""
    fun searchCollection() {
        viewModelScope.launch {
            _searchState.value = UiState.LOADING
            runCatching {
                searchRepositoryImpl.getCollections(
                    startIdx = 1,
                    endIdx = 100,
                    searchWord = searchWord.value.toString()
                )
            }.onSuccess { result ->
                if (result.searchCollectionInfo != null) {
                    _collections.value = result.searchCollectionInfo.infoList.map { it.toCollection() } as MutableList<CollectionDto>
                    _totalCount = result.searchCollectionInfo.totalCount
                    code = result.searchCollectionInfo.result.code
                    _searchState.value = UiState.SUCCESS
                } else {
                    Timber.d("Empty")
                    _searchState.value = UiState.EMPTY
                }
            }.onFailure {
                Timber.e("Search Failed ${it.message}")
                _searchState.value = UiState.ERROR
            }
        }
    }
}