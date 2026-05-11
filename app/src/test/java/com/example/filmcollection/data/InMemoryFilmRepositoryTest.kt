package com.example.filmcollection.data

import com.example.filmcollection.data.remote.FilmRemoteDataSource
import com.example.filmcollection.model.Film
import com.example.filmcollection.model.WatchStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryFilmRepositoryTest {

    @Test
    fun addFilm_addsItemToCollection() {
        val repository = InMemoryFilmRepository()

        repository.addFilm(
            title = "Interstellar",
            year = 2014,
            genre = "Sci-Fi",
            country = "USA",
            director = "Christopher Nolan",
        )

        val films = repository.films.value
        assertEquals(1, films.size)
        assertEquals("Interstellar", films.first().title)
        assertTrue(films.first().id.isNotBlank())
    }

    @Test
    fun updateFilm_updatesExistingItem() {
        val repository = InMemoryFilmRepository()
        repository.addFilm(
            title = "Old Title",
            year = 2000,
            genre = "Drama",
            country = "UK",
            director = "Director A",
        )
        val existing = repository.films.value.first()

        repository.updateFilm(existing.copy(title = "New Title"))

        val updated = repository.films.value.first()
        assertEquals("New Title", updated.title)
    }

    @Test
    fun deleteFilm_removesItemFromCollection() {
        val repository = InMemoryFilmRepository()
        repository.addFilm(
            title = "Film to Delete",
            year = 1999,
            genre = "Action",
            country = "France",
            director = "Director B",
        )
        val existing = repository.films.value.first()

        repository.deleteFilm(existing.id)

        val films = repository.films.value
        assertTrue(films.isEmpty())
    }

    @Test
    fun refresh_mergesRemoteWithLocal_preservingRatingAndStatus() = runTest {
        val remoteFilm = Film(
            id = "remote-1",
            title = "Remote Title",
            year = 2020,
            genre = "Drama",
            country = "USA",
            director = "Director X",
            isRemote = true,
        )
        val source = FakeRemoteDataSource(listOf(remoteFilm))
        val existingWithUserData = remoteFilm.copy(
            title = "Stale Title",
            rating = 5,
            watchStatus = WatchStatus.WATCHED,
        )
        val repository = InMemoryFilmRepository(
            initialFilms = listOf(existingWithUserData),
            remoteDataSource = source,
        )

        val result = repository.refresh()

        assertTrue(result.isSuccess)
        val films = repository.films.value
        assertEquals(1, films.size)
        val merged = films.first()
        assertEquals("Remote Title", merged.title)
        assertEquals(5, merged.rating)
        assertEquals(WatchStatus.WATCHED, merged.watchStatus)
    }

    @Test
    fun refresh_keepsUserAddedFilms() = runTest {
        val source = FakeRemoteDataSource(
            listOf(
                Film(
                    id = "remote-1",
                    title = "From Server",
                    year = 2020,
                    genre = "",
                    country = "",
                    director = "",
                    isRemote = true,
                ),
            ),
        )
        val repository = InMemoryFilmRepository(remoteDataSource = source)
        val userFilm = repository.addFilm(
            title = "My Film",
            year = 2021,
            genre = "Drama",
            country = "USA",
            director = "Me",
        )

        repository.refresh()

        val titles = repository.films.value.map { it.title }
        assertTrue(titles.contains("From Server"))
        assertTrue(titles.contains("My Film"))
        assertNotNull(repository.films.value.firstOrNull { it.id == userFilm.id })
    }

    @Test
    fun refresh_removesRemoteFilmsThatServerNoLongerReturns() = runTest {
        val initial = Film(
            id = "remote-1",
            title = "Remote",
            year = 2020,
            genre = "",
            country = "",
            director = "",
            isRemote = true,
        )
        val source = FakeRemoteDataSource(emptyList())
        val repository = InMemoryFilmRepository(
            initialFilms = listOf(initial),
            remoteDataSource = source,
        )

        repository.refresh()

        assertNull(repository.films.value.firstOrNull { it.id == "remote-1" })
    }

    @Test
    fun refresh_returnsFailureWhenRemoteThrows() = runTest {
        val source = object : FilmRemoteDataSource {
            override suspend fun fetchFilms(): List<Film> =
                throw RuntimeException("network error")
        }
        val repository = InMemoryFilmRepository(remoteDataSource = source)

        val result = repository.refresh()

        assertTrue(result.isFailure)
    }

    @Test
    fun refresh_isNoOpWhenRemoteDataSourceIsNull() = runTest {
        val repository = InMemoryFilmRepository()

        val result = repository.refresh()

        assertTrue(result.isSuccess)
    }

    private class FakeRemoteDataSource(
        private val films: List<Film>,
    ) : FilmRemoteDataSource {
        override suspend fun fetchFilms(): List<Film> = films
    }
}
