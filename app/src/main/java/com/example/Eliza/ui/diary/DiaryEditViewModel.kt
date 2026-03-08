package com.example.Eliza.ui.diary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.Eliza.data.local.entity.DiaryEntry
import com.example.Eliza.data.repository.DiaryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DiaryEditViewModel(
    private val repository: DiaryRepository
) : ViewModel() {

    // Канал для уведомления фрагмента об успехе
    private val _saveSuccess = Channel<Unit>()
    val saveSuccess = _saveSuccess.receiveAsFlow()

    // Сохранение новой записи
    fun saveEntry(content: String) {
        viewModelScope.launch {
            try {
                val entry = DiaryEntry(content = content)
                repository.insertEntry(entry)
                _saveSuccess.send(Unit) // Сигналим об успехе
            } catch (e: Exception) {
                Log.e("DiaryEditVM", "Error inserting", e)
            }
        }
    }

    // Обновление существующей записи
    fun updateEntry(id: Long, newContent: String) {
        viewModelScope.launch {
            try {
                val entry = repository.getEntryById(id)
                if (entry != null) {
                    val updatedEntry = entry.copy(content = newContent)
                    repository.updateEntry(updatedEntry)
                    _saveSuccess.send(Unit) // Сигналим об успехе
                } else {
                    Log.e("DiaryEditVM", "Entry with id $id not found for update")
                }
            } catch (e: Exception) {
                Log.e("DiaryEditVM", "Error updating", e)
            }
        }
    }

    class Factory(private val repository: DiaryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiaryEditViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DiaryEditViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}