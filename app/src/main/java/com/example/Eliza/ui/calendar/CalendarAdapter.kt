package com.example.Eliza.ui.calendar

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.Eliza.R

class CalendarAdapter(
    private val onDayClick: (DayItem) -> Unit
) : ListAdapter<DayItem, CalendarAdapter.DayViewHolder>(DayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view, onDayClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DayViewHolder(
        itemView: View,
        private val onDayClick: (DayItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)

        fun bind(day: DayItem) {
            Log.d("CalendarAdapter", "Binding day: ${day.dayOfMonth}, isMenstruation=${day.isMenstruation}, isPredicted=${day.isPredicted}, isOvulation=${day.isOvulation}, isFertile=${day.isFertile}, isToday=${day.isToday}")
            tvDay.text = day.dayOfMonth.toString()
            tvDay.alpha = if (day.isCurrentMonth) 1.0f else 0.3f

            // Сбрасываем стили перед отрисовкой
            tvDay.setBackgroundResource(0)
            tvDay.setTextColor(Color.BLACK)

            when {
                // 1. Реальная менструация (Красный)
                day.isMenstruation -> {
                    tvDay.setBackgroundResource(R.drawable.bg_cycle_day_menstruation)
                    tvDay.setTextColor(Color.WHITE)
                }

                // 2. Прогноз менструации (Розовый)
                day.isPredicted -> {
                    tvDay.setBackgroundResource(R.drawable.bg_cycle_day_predicted)
                    tvDay.setTextColor(Color.parseColor("#F06292")) // Ярко-розовый текст
                }

                // 3. День овуляции (Голубой)
                day.isOvulation -> {
                    // Если нет drawable, можно временно закрасить цветом,
                    // но лучше создать bg_cycle_day_ovulation.xml
                    tvDay.setBackgroundResource(R.drawable.bg_cycle_day_ovulation)
                    tvDay.setTextColor(Color.parseColor("#1976D2")) // Насыщенный синий
                }

                // 4. Благоприятные дни (Зеленый)
                day.isFertile -> {
                    tvDay.setBackgroundResource(R.drawable.bg_cycle_day_fertile)
                    tvDay.setTextColor(Color.parseColor("#388E3C")) // Темно-зеленый
                }

                // 5. Сегодняшний день (если нет других событий)
                day.isToday -> {
                    tvDay.setBackgroundResource(R.drawable.bg_today)
                    tvDay.setTextColor(Color.BLACK)
                }

                // 6. Обычный день
                else -> {
                    tvDay.setTextColor(if (day.isCurrentMonth) Color.BLACK else Color.LTGRAY)
                }
            }

            itemView.setOnClickListener {
                if (day.isCurrentMonth) onDayClick(day)
            }
        }

    }

    class DayDiffCallback : DiffUtil.ItemCallback<DayItem>() {
        override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem) = oldItem.date == newItem.date
        override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem) = oldItem == newItem
    }
}