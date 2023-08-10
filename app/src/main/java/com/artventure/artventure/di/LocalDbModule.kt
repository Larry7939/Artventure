package com.artventure.artventure.di

import android.content.Context
import com.artventure.artventure.data.local.FavoriteCollectionDao
import com.artventure.artventure.data.local.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDbModule {
    @Provides
    @Singleton
    fun provideFavoriteCollectionDao(@ApplicationContext context: Context): FavoriteCollectionDao {
        return LocalDatabase.getInstance(context)!!.FavoriteCollectionDao()
    }
}