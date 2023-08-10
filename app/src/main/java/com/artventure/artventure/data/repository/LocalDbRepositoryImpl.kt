package com.artventure.artventure.data.repository

import com.artventure.artventure.data.local.CollectionEntity
import com.artventure.artventure.data.local.FavoriteCollectionDao
import com.artventure.artventure.data.service.LocalDbRepository
import javax.inject.Inject

class LocalDbRepositoryImpl @Inject constructor(private val dao:FavoriteCollectionDao): LocalDbRepository {
    override suspend fun getFavoriteCollections():List<CollectionEntity>  = dao.getFavoriteCollections()


    override suspend fun addFavoriteCollections(collectionDto: CollectionEntity) {
        dao.insertFavoriteCollection(collectionDto)
    }

    override suspend fun clearFavoriteCollections() {
        dao.clearFavoriteCollections()
    }
}