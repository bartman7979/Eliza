package com.example.Eliza.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.Eliza.R
import com.example.Eliza.data.local.database.AppDatabase
import com.example.Eliza.data.repository.DiaryRepository
import com.example.Eliza.databinding.FragmentDiaryDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController

class DiaryDetailFragment : Fragment() {

    private var _binding: FragmentDiaryDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DiaryDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Инициализация репозитория и ViewModel
        val database = AppDatabase.getInstance(requireContext())
        val repository = DiaryRepository(database.diaryEntryDao(), database.photoDao())

        val entryId = arguments?.getLong("entryId", -1L) ?: -1L

        // Создаем Factory. Передаем аргументы фрагмента как начальное состояние
        val factory = DiaryDetailViewModel.Factory(repository, SavedStateHandle(mapOf("entryId" to entryId)))
        viewModel = ViewModelProvider(this, factory)[DiaryDetailViewModel::class.java]

        // 2. ЕДИНЫЙ слушатель данных
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.entry.collect { entry ->
                    if (entry != null) {
                        // Данные загружены — отображаем
                        binding.tvDate.text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            .format(Date(entry.date))
                        binding.tvContent.text = entry.content
                        Log.d("DiaryDetail", "UI updated: id=${entry.id}")
                    } else {
                        // Пока null — просто ждем (или можно показать ProgressBar)
                        Log.d("DiaryDetail", "Waiting for data...")
                    }
                }
            }
        }

        // 3. Кнопки
        binding.btnEdit.setOnClickListener {
            val bundle = Bundle().apply { putLong("entryId", entryId) }
            it.findNavController().navigate(R.id.diaryEditFragment, bundle)
        }

        binding.btnDelete.setOnClickListener {
            // Передаем лямбду, чтобы закрыть экран ТОЛЬКО после удаления
            viewModel.deleteEntry {
                Toast.makeText(requireContext(), "Запись удалена", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}