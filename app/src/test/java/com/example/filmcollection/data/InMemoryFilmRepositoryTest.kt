package com.example.filmcollection.data

import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
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

        val films = repository.films.first()
        assertEquals(1, films.size)
        assertEquals("Interstellar", films.first().title)
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
        val existing = repository.films.first().first()

        repository.updateFilm(existing.copy(title = "New Title"))

        val updated = repository.films.first().first()
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
        val existing = repository.films.first().first()

        repository.deleteFilm(existing.id)

        val films = repository.films.first()
        assertTrue(films.isEmpty())
    }
}
