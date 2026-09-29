package com.example.mystudyproject1


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Note::class, NoteCategory::class],
    version = 2,


)
@TypeConverters(Converters::class)
abstract class NoteDb: RoomDatabase() {
    abstract val dao: NoteDao
    companion object{
        val migrate1To2 = object: Migration(1,2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //New table- NoteCategory
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `NoteCategory` (
                    `categoryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `categoryName` TEXT NOT NULL
                    )
                """.trimIndent()
                )
                    //New property for relations in Note
                db.execSQL("ALTER TABLE `Note` ADD COLUMN `categoryId` INTEGER NOT NULL DEFAULT 0 ")
            }
        }
    }

}