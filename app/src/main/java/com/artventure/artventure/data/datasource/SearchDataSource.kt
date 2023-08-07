package com.artventure.artventure.data.datasource

import com.artventure.artventure.data.service.SearchService
import javax.inject.Inject

class SearchDataSource @Inject constructor(private val searchService: SearchService) {
    suspend fun getCollections(startIdx: Int, endIdx: Int, searchWord:String) =
        searchService.getCollections(
            startIdx = startIdx,
            endIdx = endIdx,
            searchWord = searchWord
        )

}