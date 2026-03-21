package com.example.Eliza.ui.about

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.Eliza.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Можно добавить информацию о версии, авторе, благодарности
        binding.tvVersion.text = "Версия 1.0.0"
        // В onViewCreated
        val descriptionText = """
    <p><b>Элиза</b> — твой личный островок спокойствия. Я создана, чтобы бережно заботиться о твоём настроении и здоровье. ✨</p>
    
    <p><b>Что мы будем делать вместе:</b></p>
    • <b>Дневник чувств:</b> записывай мысли и храни важные моменты в фото.<br>
    • <b>Лампа настроения:</b> делись событиями, и я подскажу, какой сегодня «цвет» твоего дня.<br>
    • <b>Календарь:</b> я помогу следить за циклом и предскажу важные даты.<br>
    • <b>Напутствия:</b> теплые слова от тех, кто тебя понимает.<br>
    • <b>Чат со мной:</b> я всегда рядом, чтобы поболтать, найти рецепт или просто выслушать. 🌸<br><br>
    
    <i>Все твои секреты остаются только на этом устройстве.</i><br><br>
    С любовью, сделано для тебя. ❤️
""".trimIndent()

// Чтобы теги <b> и <i> заработали, используем Html.fromHtml
        binding.tvDescription.text = android.text.Html.fromHtml(descriptionText, android.text.Html.FROM_HTML_MODE_LEGACY)
        binding.tvCopyright.text = "© 2026 Твой Bartman79"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}