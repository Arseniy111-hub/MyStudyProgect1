package com.example.mystudyproject1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Upsert
    suspend fun upsert(note: Note)
    @Delete
    suspend fun delete(note: Note)
    @Query("SELECT * FROM Note")
    fun orderByAll(): Flow<List<Note>>
    @Query("SELECT * FROM Note ORDER BY text ASC ")
    fun orderByText(): Flow<List<Note>>
    @Query("SELECT * FROM Note ORDER BY contact ASC ")
    fun orderByContacts(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?
}