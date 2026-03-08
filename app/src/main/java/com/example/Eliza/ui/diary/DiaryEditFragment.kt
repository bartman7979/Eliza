package com.example.Eliza.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.Eliza.data.local.database.AppDatabase
import com.example.Eliza.data.repository.DiaryRepository
import com.example.Eliza.databinding.FragmentDiaryEditBinding
import kotlinx.coroutines.launch

class DiaryEditFragment : Fragment() {

    private var _binding: FragmentDiaryEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DiaryEditViewModel
    private var entryId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Исправлено: получаем ID с дефолтным значением -1L
        entryId = arguments?.getLong("entryId", -1L) ?: -1L

        // ВАЖНО: Если ID пришел как 0, это часто значит, что ключ потерян в навигации
        Log.d("DiaryEdit", "Started with entryId: $entryId")

        val database = AppDatabase.getInstance(requireContext())
        val repository = DiaryRepository(database.diaryEntryDao(), database.photoDao())
        val factory = DiaryEditViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[DiaryEditViewModel::class.java]

        // 1. Слушаем сигнал об успехе сохранения/обновления
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveSuccess.collect {
                // Только когда БД реально отработала, закрываем экран
                findNavController().popBackStack()
            }
        }

        // 2. Если редактируем (ID > 0), загружаем текст
        if (entryId > 0) {
            lifecycleScope.launch {
                val entry = repository.getEntryById(entryId)
                binding.etContent.setText(entry?.content ?: "")
            }
        }

        // 3. Логика сохранения
        binding.btnSave.setOnClickListener {
            val content = binding.etContent.text.toString().trim()
            if (content.isNotEmpty()) {
                // Если ID не валидный ( -1 или 0), создаем новую запись
                if (entryId <= 0) {
                    Log.d("DiaryEdit", "Creating new entry")
                    viewModel.saveEntry(content)
                } else {
                    Log.d("DiaryEdit", "Updating entry $entryId")
                    viewModel.updateEntry(entryId, content)
                }
            } else {
                Toast.makeText(requireContext(), "Введите текст", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}