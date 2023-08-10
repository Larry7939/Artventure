package com.artventure.artventure.data.service

import com.artventure.artventure.data.local.CollectionEntity

interface LocalDbRepository {
    suspend fun getFavoriteCollections(): List<CollectionEntity>
    suspend fun addFavoriteCollections(collectionDto: CollectionEntity)
    suspend fun clearFavoriteCollections()
}