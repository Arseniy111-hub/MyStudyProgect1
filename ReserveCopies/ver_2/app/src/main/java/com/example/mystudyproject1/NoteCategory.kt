package com.example.mystudyproject1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteCategory(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int,
    val categoryName: String

)
