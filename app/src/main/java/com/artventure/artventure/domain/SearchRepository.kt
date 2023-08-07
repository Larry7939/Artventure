package com.artventure.artventure.domain

import com.artventure.artventure.data.model.response.ResponseSearch

interface SearchRepository {
    suspend fun getCollections(startIdx: Int, endIdx: Int, searchWord: String): ResponseSearch
}