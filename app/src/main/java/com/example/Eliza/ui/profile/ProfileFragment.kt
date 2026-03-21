package com.example.Eliza.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.Eliza.R
import com.example.Eliza.databinding.FragmentProfileBinding
import com.example.Eliza.utils.PersonalityManager
import com.example.Eliza.utils.PersonalityType
import com.example.Eliza.utils.UserDataManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var personalityManager: PersonalityManager
    private lateinit var userDataManager: UserDataManager

    private lateinit var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        personalityManager = PersonalityManager(requireContext())
        userDataManager = UserDataManager(requireContext())

        // Загружаем имя и день рождения
        lifecycleScope.launch {
            userDataManager.userName.collect { name ->
                binding.etName.setText(name)
            }
        }
        lifecycleScope.launch {
            userDataManager.userBirthday.collect { birthday ->
                binding.etBirthday.setText(birthday)
            }
        }

        // Загружаем личность
        lifecycleScope.launch {
            personalityManager.currentPersonality.collect { type ->
                when (type) {
                    PersonalityType.FRIEND -> binding.radioFriend.isChecked = true
                    PersonalityType.SISTER -> binding.radioSister.isChecked = true
                    PersonalityType.COACH -> binding.radioCoach.isChecked = true
                }
            }
        }

        // Слушатель клавиатуры
        val rootView = binding.root
        val scrollView = binding.scrollView
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (_binding == null) return@OnGlobalLayoutListener
            val rect = android.graphics.Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                scrollView.setPadding(0, 0, 0, keypadHeight)
            } else {
                scrollView.setPadding(0, 0, 0, 0)
            }
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val birthday = binding.etBirthday.text.toString().trim()

            val selected = when (binding.radioGroupPersonality.checkedRadioButtonId) {
                R.id.radioFriend -> PersonalityType.FRIEND
                R.id.radioSister -> PersonalityType.SISTER
                R.id.radioCoach -> PersonalityType.COACH
                else -> PersonalityType.FRIEND
            }

            lifecycleScope.launch {
                personalityManager.setPersonality(selected)
                userDataManager.saveUserData(name, birthday)
                Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        if (_binding != null) {
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
        super.onDestroyView()
        _binding = null
    }
}