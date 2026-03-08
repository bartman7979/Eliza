package com.example.Eliza.ui.diary

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.Eliza.data.local.entity.DiaryEntry
import com.example.Eliza.data.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiaryDetailViewModel(
    private val repository: DiaryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Достаем ID из аргументов навигации (ключ должен совпадать с тем, что в nav_graph)
    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: -1L

    private val _entry = MutableStateFlow<DiaryEntry?>(null)
    val entry = _entry.asStateFlow()

    init {
        Log.d("DiaryDetailVM", "init, entryId from savedStateHandle = $entryId")
        loadEntry()
    }

    private fun loadEntry() {
        if (entryId == -1L) {
            Log.e("DiaryDetailVM", "No entryId provided!")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("DiaryDetailVM", "loading entry $entryId")
                val result = repository.getEntryById(entryId)
                Log.d("DiaryDetailVM", "entry loaded: $result")
                _entry.value = result
            } catch (e: Exception) {
                Log.e("DiaryDetailVM", "Error loading entry", e)
            }
        }
    }

    fun deleteEntry(onComplete: () -> Unit) {
        viewModelScope.launch {
            _entry.value?.let {
                Log.d("DiaryDetailVM", "deleting entry ${it.id}")
                repository.deleteEntry(it)
                onComplete() // Коллбэк, чтобы закрыть экран после удаления
            }
        }
    }

    class Factory(
        private val repository: DiaryRepository,
        private val savedStateHandle: SavedStateHandle
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiaryDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DiaryDetailViewModel(repository, savedStateHandle) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}