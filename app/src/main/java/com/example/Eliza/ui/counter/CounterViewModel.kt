package com.example.Eliza.ui.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.Eliza.data.repository.MoodRepository
import kotlinx.coroutines.launch

class CounterViewModel(private val moodRepository: MoodRepository) : ViewModel() {

    fun addPositiveEvent() {
        viewModelScope.launch {
            moodRepository.addPositiveEvent()
        }
    }

    fun addNegativeEvent() {
        viewModelScope.launch {
            moodRepository.addNegativeEvent()
        }
    }

    class Factory(private val moodRepository: MoodRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CounterViewModel(moodRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}