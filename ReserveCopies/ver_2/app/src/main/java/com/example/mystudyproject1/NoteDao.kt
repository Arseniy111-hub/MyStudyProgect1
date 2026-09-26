package com.example.mystudyproject1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Upsert
    suspend fun upsertTheNote(note: Note)
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: NoteCategory): Long
    @Delete
    suspend fun deleteCategory(category: NoteCategory)
    @Query("SELECT * FROM notecategory WHERE categoryName = :name")
    suspend fun getCategoryByName(name: String): NoteCategory?
    @Query("SELECT * FROM note WHERE categoryId = :categoryId")
    fun getNotesWithCategoryOnCategoryName(categoryId: Int): Flow<List<Note>>
    @Query("SELECT * FROM notecategory")
    fun getALlCategories(): Flow<List<NoteCategory>>
//    @Query("SELECT * FROM note WHERE categoryId = :categoryId")
//    suspend fun getCategoryOnNoteId(categoryId: Int): Note

}