package com.artventure.artventure.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.domain.SearchRepository
import com.artventure.artventure.presentation.adapter.SectorFilteringDto
import com.artventure.artventure.util.ListLiveData
import com.artventure.artventure.util.type.SortingType
import com.artventure.artventure.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepositoryImpl: SearchRepository) :
    ViewModel() {
    private var _isSearchEnable = MutableLiveData(false)
    val isSearchEnable: LiveData<Boolean>
        get() = _isSearchEnable
    val onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        _isSearchEnable.value = hasFocus
    }
    private var _searchWord: MutableLiveData<String> = MutableLiveData<String>()
    val searchWord: LiveData<String>
        get() = _searchWord

    val searchWordInput: MutableLiveData<String> = MutableLiveData<String>()

    private var startIdx = START_INDEX
    private var endIdx = END_INDEX

    private var _collections = ListLiveData<CollectionDto>()
    val collections: ListLiveData<CollectionDto>
        get() = _collections

    private var _searchState = MutableLiveData<UiState>()
    val searchState: LiveData<UiState>
        get() = _searchState

    private var _pagingState = MutableLiveData<UiState>()
    val pagingState: LiveData<UiState>
        get() = _pagingState

    private var _totalCount = 0
    val totalCount: Int
        get() = _totalCount

    private var code = ""

    private var _sortingState = MutableLiveData(SortingType.MNFT_ASCENDING)
    val sortingState: LiveData<SortingType>
        get() = _sortingState
    private val defaultFilteringStates = listOf(
        SectorFilteringDto("전체", true),
        SectorFilteringDto("회화", true),
        SectorFilteringDto("한국화", true),
        SectorFilteringDto("드로잉&판화", true),
        SectorFilteringDto("조각", true),
        SectorFilteringDto("뉴미디어", true),
        SectorFilteringDto("사진", true),
        SectorFilteringDto("설치", true),
        SectorFilteringDto("디자인", true)
    )
    private var _filteringState = MutableLiveData(defaultFilteringStates)
    val filteringState: LiveData<List<SectorFilteringDto>>
        get() = _filteringState

    val selectedFilteringState: List<String>
        get() = filteringState.value!!.filter {
            it.isSelected
        }.map { it.sector }

    fun searchCollection() {
        viewModelScope.launch {
            _searchWord.value = searchWordInput.value
            _searchState.value = UiState.LOADING
            runCatching {
                searchRepositoryImpl.getCollections(
                    startIdx = START_INDEX,
                    endIdx = END_INDEX,
                    searchWord = searchWordInput.value.toString()
                )
            }.onSuccess { result ->
                if (result.searchCollectionInfo != null) {
                    _collections.value =
                        result.searchCollectionInfo.infoList.map { it.toCollection() } as MutableList<CollectionDto>
                    _totalCount = result.searchCollectionInfo.totalCount
                    code = result.searchCollectionInfo.result.code
                    _searchState.value = UiState.SUCCESS
                } else {
                    if (result.result?.code == "INFO-200") {
                        _collections.clear()
                        _searchState.value = UiState.EMPTY
                    }
                }
            }.onFailure {
                Timber.e("Search Failed ${it.message}")
                _searchState.value = UiState.ERROR
            }
        }
    }

    fun pagingCollection() {
        viewModelScope.launch {
            _pagingState.value = UiState.LOADING
            runCatching {
                searchRepositoryImpl.getCollections(
                    startIdx = startIdx,
                    endIdx = endIdx,
                    searchWord = _searchWord.value.toString()
                )
            }.onSuccess { result ->
                if (result.searchCollectionInfo != null) {
                    _collections.value?.addAll(result.searchCollectionInfo.infoList.map { it.toCollection() } as MutableList<CollectionDto>)
                    _pagingState.value = UiState.SUCCESS
                } else {
                    if (result.result?.code == "INFO-200") {
                        _pagingState.value = UiState.EMPTY
                    }
                }
            }.onFailure {
                Timber.e("Paging Failed ${it.message}")
                _pagingState.value = UiState.ERROR
            }
        }
    }

    fun setSortingState(sortingType: SortingType) {
        _sortingState.value = sortingType
    }

    fun setFilteringState(state: List<SectorFilteringDto>) {
        _filteringState.value = state
    }

    fun initSearchIndex() {
        startIdx = START_INDEX
        endIdx = END_INDEX
    }

    fun updateSearchIndex() {
        startIdx = endIdx + 1
        endIdx += PAGE_SIZE
    }

    fun checkIndexValidation(): Boolean = startIdx <= totalCount

    companion object {
        const val START_INDEX = 1
        const val END_INDEX = 100
        const val PAGE_SIZE = 100
    }
}