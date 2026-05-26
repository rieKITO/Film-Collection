package com.example.filmcollection.di

import com.example.filmcollection.data.FilmRepository
import com.example.filmcollection.data.RoomFilmRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFilmRepository(impl: RoomFilmRepository): FilmRepository
}
