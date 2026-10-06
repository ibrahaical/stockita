package com.stockita.feature.tugas.model

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TaskStatus(val code: String, val label: String) {
    TODO("TODO", "To Do"),
    IN_PROGRESS("IN_PROGRESS", "In Progress"),
    DONE("DONE", "Selesai");

    fun nextStatus(): TaskStatus = when (this) {
        TODO -> IN_PROGRESS
        IN_PROGRESS -> DONE
        DONE -> TODO
    }

    companion object {
        fun fromCode(code: String?): TaskStatus =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: TODO
    }
}

enum class TaskPriority(val level: Int, val label: String, val color: Color, val badgeBg: Color) {
    LOW(0, "Rendah", Color(0xFF4B5563), Color(0xFFF3F4F6)),
    MEDIUM(1, "Sedang", Color(0xFFD97706), Color(0xFFFEF3C7)),
    HIGH(2, "Tinggi", Color(0xFFDC2626), Color(0xFFFEE2E2));

    companion object {
        fun fromLevel(level: Int): TaskPriority =
            entries.find { it.level == level } ?: MEDIUM
    }
}

object TaskDateFormatter {
    fun formatDueDate(timestamp: Long?): String? {
        if (timestamp == null) return null
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val isTomorrow = tomorrow.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        return when {
            isToday -> "Hari ini"
            isTomorrow -> "Besok"
            else -> SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID")).format(Date(timestamp))
        }
    }

    fun isOverdue(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        return timestamp < now.timeInMillis
    }
}
