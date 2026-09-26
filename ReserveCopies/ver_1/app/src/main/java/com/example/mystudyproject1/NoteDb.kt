package com.example.mystudyproject1


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Note::class, Contact::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NoteDb: RoomDatabase() {
    abstract val dao: NoteDao
}