package com.example.mystudyproject1

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact (
    @PrimaryKey
    val id: Long,
    val name: String,
    val uri: Uri
)
