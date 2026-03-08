package com.example.Eliza.data.repository

import android.util.Log
import com.example.Eliza.data.local.dao.CycleDayDao
import com.example.Eliza.data.local.entity.CycleDay
import java.util.Calendar

class CycleRepository(private val cycleDayDao: CycleDayDao) {

    private val DAY_IN_MS = 24 * 60 * 60 * 1000L

    suspend fun markMenstruationDay(date: Long) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val cleanDate = cal.timeInMillis
        Log.d("CycleRepo", "markMenstruationDay: original=$date, clean=$cleanDate")
        val existing = cycleDayDao.getDay(cleanDate)
        if (existing == null) {
            cycleDayDao.insert(CycleDay(date = cleanDate, type = 1))
        } else {
            cycleDayDao.delete(existing)
        }
    }

    suspend fun getDaysForMonth(start: Long, end: Long): List<CycleDay> {
        val result = cycleDayDao.getDaysBetween(start, end)
        Log.d("CycleRepo", "getDaysForMonth: start=$start, end=$end, result=${result.map { it.date }}")
        return result
    }

    data class PredictionData(
        val menstruation: Set<Long> = emptySet(),
        val ovulation: Set<Long> = emptySet(),
        val fertile: Set<Long> = emptySet()
    )

    private suspend fun getCycleStarts(): List<Long> {
        val allDays = cycleDayDao.getAllMenstruationDays().sortedBy { it.date }
        if (allDays.isEmpty()) return emptyList()

        val starts = mutableListOf<Long>()
        var lastTracked: Long = -1
        for (day in allDays) {
            if (lastTracked == -1L || day.date - lastTracked > 7 * DAY_IN_MS) {
                starts.add(day.date)
            }
            lastTracked = day.date
        }
        return starts
    }

    private fun calculateAvg(starts: List<Long>): Long {
        if (starts.size < 2) return 28 * DAY_IN_MS
        val lengths = mutableListOf<Long>()
        for (i in 1 until starts.size) {
            lengths.add(starts[i] - starts[i - 1])
        }
        return lengths.average().toLong()
    }

    suspend fun predictNextPeriod(): Long? {
        val starts = getCycleStarts()
        if (starts.isEmpty()) return null
        val avg = calculateAvg(starts)
        return starts.last() + avg
    }

    suspend fun calculatePredictions(viewStart: Long, viewEnd: Long): PredictionData {
        val starts = getCycleStarts()
        if (starts.isEmpty()) return PredictionData()

        val avgCycle = calculateAvg(starts)
        val predMenstruation = mutableSetOf<Long>()
        val predOvulation = mutableSetOf<Long>()
        val predFertile = mutableSetOf<Long>()

        var nextCycleStart = starts.last()

        repeat(12) {
            nextCycleStart += avgCycle

            // "Чистим" дату после прыжка, чтобы время всегда было 00:00
            val cal = Calendar.getInstance().apply {
                timeInMillis = nextCycleStart
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            nextCycleStart = cal.timeInMillis

            // 1. Менструация (5 дней)
            for (i in 0..4) {
                predMenstruation.add(nextCycleStart + (i * DAY_IN_MS))
            }

            // 2. Овуляция (за 14 дней до следующего начала)
            val ovulationDay = nextCycleStart - (14 * DAY_IN_MS)
            predOvulation.add(ovulationDay)

            // 3. Фертильное окно
            for (i in -3..1) {
                predFertile.add(ovulationDay + (i * DAY_IN_MS))
            }
        }

        return PredictionData(predMenstruation, predOvulation, predFertile)
    }
}