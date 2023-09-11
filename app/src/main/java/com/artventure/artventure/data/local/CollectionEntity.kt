package com.artventure.artventure.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.artventure.artventure.data.model.dto.CollectionDto
import java.io.Serializable

@Entity(tableName = "local_favorite_collection")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var collection: CollectionDto
) : Serializable