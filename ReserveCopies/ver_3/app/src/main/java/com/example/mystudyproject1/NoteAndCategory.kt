package com.example.mystudyproject1

import androidx.room.Embedded
import androidx.room.Relation

data class NoteAndCategory(
    @Embedded val category: NoteCategory,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val notes: List<Note>
)
