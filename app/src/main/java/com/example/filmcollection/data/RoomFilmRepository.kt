package com.example.filmcollection.data

import com.example.filmcollection.data.local.FilmDao
import com.example.filmcollection.data.local.toDomain
import com.example.filmcollection.data.local.toEntity
import com.example.filmcollection.data.remote.FilmRemoteDataSource
import com.example.filmcollection.model.Film
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomFilmRepository @Inject constructor(
    private val dao: FilmDao,
    private val remoteDataSource: FilmRemoteDataSource,
) : FilmRepository {

    override val films: Flow<List<Film>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addFilm(
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ): Film {
        val film = Film(
            id = UUID.randomUUID().toString(),
            title = title,
            year = year,
            genre = genre,
            country = country,
            director = director,
        )
        dao.upsert(film.toEntity())
        return film
    }

    override suspend fun updateFilm(film: Film) {
        dao.update(film.toEntity())
    }

    override suspend fun deleteFilm(id: String) {
        dao.deleteById(id)
    }

    override suspend fun refresh(): Result<Unit> = runCatching {
        val remoteFilms = remoteDataSource.fetchFilms()
        dao.applyRemote(remoteFilms.map { it.toEntity() })
    }
}
