package com.artventure.artventure.di

import com.artventure.artventure.data.service.SearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideSearchService(retrofit: Retrofit) =
        retrofit.create(SearchService::class.java)
}