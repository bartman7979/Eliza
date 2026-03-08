package com.example.Eliza.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.Eliza.data.local.dao.DiaryEntryDao
import com.example.Eliza.data.local.dao.PhotoDao
import com.example.Eliza.data.local.dao.MoodEventDao
import com.example.Eliza.data.local.dao.CycleDayDao
import com.example.Eliza.data.local.entity.DiaryEntry
import com.example.Eliza.data.local.entity.Photo
import com.example.Eliza.data.local.entity.MoodEvent
import com.example.Eliza.data.local.entity.CycleDay

@Database(
    entities = [DiaryEntry::class, Photo::class, MoodEvent::class, CycleDay::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun photoDao(): PhotoDao
    abstract fun moodEventDao(): MoodEventDao
    abstract fun cycleDayDao(): CycleDayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eliza_database"
                )
                    .fallbackToDestructiveMigration() // для разработки, удалит данные при изменении схемы
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}