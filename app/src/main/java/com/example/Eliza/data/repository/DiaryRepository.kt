package com.example.Eliza.data.repository

import android.util.Log
import com.example.Eliza.data.local.dao.DiaryEntryDao
import com.example.Eliza.data.local.dao.PhotoDao
import com.example.Eliza.data.local.entity.DiaryEntry
import com.example.Eliza.data.local.entity.Photo
import kotlinx.coroutines.flow.Flow

class DiaryRepository(
    private val diaryEntryDao: DiaryEntryDao,
    private val photoDao: PhotoDao
) {
    // DiaryEntry operations
    fun getAllEntries(): Flow<List<DiaryEntry>> = diaryEntryDao.getAllEntries()

    suspend fun getEntryById(id: Long): DiaryEntry? {
        Log.d("DiaryRepo", "getEntryById: $id")
        val result = diaryEntryDao.getEntryById(id)
        Log.d("DiaryRepo", "getEntryById result: $result")
        return result
    }

    suspend fun insertEntry(entry: DiaryEntry) {
        Log.d("DiaryRepo", "insertEntry: $entry")
        diaryEntryDao.insert(entry)
        Log.d(
            "DiaryRepo",
            "insertEntry done, id = ${entry.id}"
        ) // но entry.id после вставки может обновиться, если autoGenerate
    }

        suspend fun updateEntry(entry: DiaryEntry) {
        diaryEntryDao.update(entry)
    }

    suspend fun deleteEntry(entry: DiaryEntry) {
        diaryEntryDao.delete(entry)
    }

    // Photo operations
    fun getPhotosForEntry(entryId: Long): Flow<List<Photo>> = photoDao.getPhotosForEntry(entryId)

    suspend fun insertPhoto(photo: Photo) {
        photoDao.insert(photo)
    }

    suspend fun deletePhoto(photo: Photo) {
        photoDao.delete(photo)
    }

    suspend fun deletePhotosForEntry(entryId: Long) {
        photoDao.deleteByEntryId(entryId)
    }
}