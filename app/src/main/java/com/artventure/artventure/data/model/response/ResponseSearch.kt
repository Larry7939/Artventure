package com.artventure.artventure.data.model.response

import com.artventure.artventure.data.model.dto.CollectionDto
import com.google.gson.annotations.SerializedName

data class ResponseSearch(
    @SerializedName("SemaPsgudInfoKorInfo")
    val searchCollectionInfo: SearchCollectionInfo?,
    @SerializedName("RESULT")
    val result: Result?
)

data class Result(
    @SerializedName("CODE")
    val code: String,
    @SerializedName("MESSAGE")
    val message: String
)

data class Info(
    @SerializedName("main_image")
    val mainImg: String,
    @SerializedName("manage_no_year")
    val collectedYear: String,
    @SerializedName("matrl_technic")
    val matrlTechnic: String,
    @SerializedName("mnfct_year")
    val mnfctYear: String,
    @SerializedName("prdct_cl_nm")
    val sector: String,
    @SerializedName("prdct_detail")
    val detail: String,
    @SerializedName("prdct_nm_eng")
    val titleEng: String,
    @SerializedName("prdct_nm_korean")
    val titleKor: String,
    @SerializedName("prdct_stndrd")
    val standard: String,
    @SerializedName("thumb_image")
    val thumbImg: String,
    @SerializedName("writr_nm")
    val artist: String
) {
    fun toCollection(): CollectionDto {
        return CollectionDto(
            titleKor,
            titleEng,
            artist,
            mnfctYear,
            sector,
            standard,
            collectedYear,
            matrlTechnic,
            mainImg,
            thumbImg
        )
    }
}

data class SearchCollectionInfo(
    @SerializedName("RESULT")
    val result: Result,
    @SerializedName("list_total_count")
    val totalCount: Int,
    @SerializedName("row")
    val infoList: List<Info>
)