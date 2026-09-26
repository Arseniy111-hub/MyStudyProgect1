package com.example.mystudyproject1


import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey
    val id: Long,
    val text: String?,
    val contact: Contact?,
    val pictureUris: List<Uri> = emptyList()
    )