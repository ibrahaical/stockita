package com.stockita.feature.tugas.model

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TaskCategory(val label: String, val subtitle: String) {
    TODAY("Today", "Today's Tasks"),
    SCHEDULED("Scheduled", "Scheduled Tasks"),
    ALL("All", "All Tasks"),
    OVERDUE("Overdue", "Overdue Tasks")
}

enum class TaskStatus(val code: String, val label: String) {
    TODO("TODO", "To Do"),
    IN_PROGRESS("IN_PROGRESS", "In Progress"),
    DONE("DONE", "Done");

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
    fun formatCardDateTime(timestamp: Long?): String? {
        if (timestamp == null) return null
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val isTomorrow = tomorrow.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        val formattedTime = timeFormat.format(Date(timestamp))

        return when {
            isToday -> "Today, $formattedTime"
            isTomorrow -> "Tomorrow, $formattedTime"
            else -> {
                val dateFormat = SimpleDateFormat("d MMM, h:mm a", Locale.ENGLISH)
                dateFormat.format(Date(timestamp))
            }
        }
    }

    fun formatDateOnly(timestamp: Long?): String {
        if (timestamp == null) return "Pilih Tanggal"
        return SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID")).format(Date(timestamp))
    }

    fun formatTimeOnly(timestamp: Long?): String {
        if (timestamp == null) return "Pilih Waktu"
        return SimpleDateFormat("HH:mm", Locale.forLanguageTag("id-ID")).format(Date(timestamp))
    }

    fun formatDetailDateTime(timestamp: Long?): String {
        if (timestamp == null) return "Tidak ada tenggat waktu"
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy • HH:mm", Locale.forLanguageTag("id-ID"))
        return dateFormat.format(Date(timestamp))
    }

    fun isToday(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    fun isScheduled(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val startOfTomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return timestamp >= startOfTomorrow
    }

    fun isOverdue(timestamp: Long?, isDone: Boolean = false): Boolean {
        if (timestamp == null || isDone) return false
        return timestamp < System.currentTimeMillis()
    }
}

