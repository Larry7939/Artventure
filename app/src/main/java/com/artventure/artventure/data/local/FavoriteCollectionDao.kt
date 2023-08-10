package com.artventure.artventure.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteCollectionDao {
    /**즐겨찾기 추가*/
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavoriteCollection(collection: CollectionEntity)

    /**즐겨찾기 삭제*/
    @Delete
    fun deleteFavoriteCollection(collection: CollectionEntity)

    /**즐겨찾기 항목 전체 조회*/
    @Query("SELECT * FROM local_favorite_collection")
    fun getFavoriteCollections(): List<CollectionEntity>

    /**즐겨찾기 항목 전체 삭제*/
    @Query("DELETE FROM local_favorite_collection")
    fun clearFavoriteCollections()
}