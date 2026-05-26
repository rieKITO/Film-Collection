package com.example.filmcollection.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {
    @Query("SELECT * FROM films ORDER BY title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<FilmEntity>>

    @Query("SELECT * FROM films WHERE id = :id")
    suspend fun findById(id: String): FilmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(film: FilmEntity)

    @Update
    suspend fun update(film: FilmEntity)

    @Query("DELETE FROM films WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT id FROM films WHERE isRemote = 1")
    suspend fun remoteIds(): List<String>

    @Transaction
    suspend fun applyRemote(remote: List<FilmEntity>) {
        val incomingIds = remote.mapTo(mutableSetOf()) { it.id }
        remoteIds()
            .filterNot { it in incomingIds }
            .forEach { deleteById(it) }

        remote.forEach { incoming ->
            val existing = findById(incoming.id)
            val merged = if (existing != null) {
                incoming.copy(
                    rating = existing.rating,
                    watchStatus = existing.watchStatus,
                    imageResId = existing.imageResId,
                )
            } else {
                incoming
            }
            upsert(merged)
        }
    }
}
