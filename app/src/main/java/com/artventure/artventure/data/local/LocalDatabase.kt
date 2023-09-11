package com.artventure.artventure.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CollectionEntity::class], version = 1, exportSchema = false)
@TypeConverters(LocalTypeConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun FavoriteCollectionDao(): FavoriteCollectionDao

    // Singleton 클래스 구현
    companion object {
        // DB 이름
        private const val dbName = "localDb"
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase? {
            if (INSTANCE == null) {
                synchronized(LocalDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java, dbName
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}