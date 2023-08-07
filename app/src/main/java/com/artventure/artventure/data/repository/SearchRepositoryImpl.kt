package com.artventure.artventure.data.repository

import com.artventure.artventure.data.datasource.SearchDataSource
import com.artventure.artventure.data.model.response.ResponseSearch
import com.artventure.artventure.domain.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(private val searchDataSource: SearchDataSource) :
    SearchRepository {
    override suspend fun getCollections(
        startIdx: Int,
        endIdx: Int,
        searchWord: String
    ): ResponseSearch {
        return searchDataSource.getCollections(
            startIdx = startIdx,
            endIdx = endIdx,
            searchWord = searchWord
        )
    }
}