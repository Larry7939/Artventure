package com.artventure.artventure.data.service

import com.artventure.artventure.BuildConfig
import com.artventure.artventure.data.model.response.ResponseSearch
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchService {
    @GET("/{KEY}/json/SemaPsgudInfoKorInfo/{START_INDEX}/{END_INDEX}/ / /{prdct_nm_korean}")
    suspend fun getCollections(
        @Path("KEY") key: String = BuildConfig.API_KEY,
        @Path("START_INDEX") startIdx: Int,
        @Path("END_INDEX") endIdx: Int,
        @Path("prdct_nm_korean") searchWord: String
    ): ResponseSearch

}