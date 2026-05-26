package com.example.filmcollection.di

import android.content.Context
import androidx.room.Room
import com.example.filmcollection.data.local.FilmDao
import com.example.filmcollection.data.local.FilmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFilmDatabase(
        @ApplicationContext context: Context,
    ): FilmDatabase = Room
        .databaseBuilder(context, FilmDatabase::class.java, FilmDatabase.DATABASE_NAME)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun provideFilmDao(database: FilmDatabase): FilmDao = database.filmDao()
}
