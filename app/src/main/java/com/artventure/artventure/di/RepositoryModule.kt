package com.artventure.artventure.di

import com.artventure.artventure.data.repository.SearchRepositoryImpl
import com.artventure.artventure.domain.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindSearchCollectionRepository(searchRepositoryImpl: SearchRepositoryImpl):SearchRepository
}