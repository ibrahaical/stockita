package com.stockita.feature.dashboard.model

import com.stockita.core.database.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TimeRangeFilter(val code: String, val label: String) {
    ONE_DAY("1D", "Hari ini"),
    ONE_WEEK("1W", "1 minggu terakhir"),
    ONE_MONTH("1M", "1 bulan terakhir"),
    THREE_MONTHS("3M", "3 bulan terakhir"),
    YTD("YTD", "Year to date"),
    ONE_YEAR("1Y", "1 tahun terakhir"),
    THREE_YEARS("3Y", "3 tahun terakhir"),
    FIVE_YEARS("5Y", "5 tahun terakhir")
}

data class ChartPoint(
    val timestamp: Long,
    val formattedTime: String,
    val amount: Long
)

data class ChartDataSet(
    val totalAmount: Long,
    val periodLabel: String,
    val points: List<ChartPoint>
)

object ChartDataHelper {

    fun generateChartData(
        transactions: List<TransactionEntity>,
        filter: TimeRangeFilter,
        now: Long = System.currentTimeMillis()
    ): ChartDataSet {
        val cal = Calendar.getInstance().apply { timeInMillis = now }

        val (startOfPeriod, bucketTimestamps, timeFormatter) = when (filter) {
            TimeRangeFilter.ONE_DAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                val buckets = mutableListOf<Long>()
                // 8 interval buckets across 24h: 00:00, 03:00, 06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 23:59
                for (hour in listOf(0, 3, 6, 9, 12, 15, 18, 21)) {
                    val bCal = Calendar.getInstance().apply {
                        timeInMillis = start
                        set(Calendar.HOUR_OF_DAY, hour)
                    }
                    buckets.add(bCal.timeInMillis)
                }
                val endOfDay = Calendar.getInstance().apply {
                    timeInMillis = start
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                }
                buckets.add(endOfDay.timeInMillis)

                Triple(start, buckets, SimpleDateFormat("HH:mm", Locale.forLanguageTag("id-ID")))
            }

            TimeRangeFilter.ONE_WEEK -> {
                cal.add(Calendar.DAY_OF_YEAR, -6)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                val buckets = mutableListOf<Long>()
                val bCal = Calendar.getInstance().apply { timeInMillis = start }
                for (i in 0..6) {
                    bCal.set(Calendar.HOUR_OF_DAY, 23)
                    bCal.set(Calendar.MINUTE, 59)
                    buckets.add(bCal.timeInMillis)
                    bCal.add(Calendar.DAY_OF_YEAR, 1)
                }

                Triple(start, buckets, SimpleDateFormat("EEE, d MMM", Locale.forLanguageTag("id-ID")))
            }

            TimeRangeFilter.ONE_MONTH -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                val buckets = mutableListOf<Long>()
                val bCal = Calendar.getInstance().apply { timeInMillis = start }
                // 10 intervals (every 3 days)
                for (i in 0..10) {
                    buckets.add(bCal.timeInMillis)
                    bCal.add(Calendar.DAY_OF_YEAR, 3)
                }

                Triple(start, buckets, SimpleDateFormat("d MMM", Locale.forLanguageTag("id-ID")))
            }

            TimeRangeFilter.THREE_MONTHS -> {
                cal.add(Calendar.DAY_OF_YEAR, -90)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                val buckets = mutableListOf<Long>()
                val bCal = Calendar.getInstance().apply { timeInMillis = start }
                // 12 weekly intervals
                for (i in 0..12) {
                    buckets.add(bCal.timeInMillis)
                    bCal.add(Calendar.DAY_OF_YEAR, 7)
                }

                Triple(start, buckets, SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID")))
            }

            TimeRangeFilter.YTD -> {
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                val currentMonth = Calendar.getInstance().apply { timeInMillis = now }.get(Calendar.MONTH)
                val buckets = mutableListOf<Long>()
                val bCal = Calendar.getInstance().apply { timeInMillis = start }
                for (m in 0..currentMonth) {
                    bCal.set(Calendar.MONTH, m)
                    bCal.set(Calendar.DAY_OF_MONTH, bCal.getActualMaximum(Calendar.DAY_OF_MONTH))
                    bCal.set(Calendar.HOUR_OF_DAY, 23)
                    buckets.add(bCal.timeInMillis)
                }
                if (buckets.size < 2) {
                    // Ensure at least 2 points for a line
                    buckets.add(now)
                }

                Triple(start, buckets, SimpleDateFormat("MMM yyyy", Locale.forLanguageTag("id-ID")))
            }

            TimeRangeFilter.ONE_YEAR -> {
                cal.add(Calendar.MONTH, -11)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                val buckets = mutableListOf<Long>()
                val bCal = Calendar.getInstance().apply { timeInMillis = start }
                for (i in 0..11) {
                    bCal.set(Calendar.DAY_OF_MONTH, bCal.getActualMaximum(Calendar.DAY_OF_MONTH))
                    bCal.set(Calendar.HOUR_OF_DAY, 23)
                    buckets.add(bCal.timeInMillis)
                    bCal.add(Calendar.MONTH, 1)
                }

                Triple(start, buckets, SimpleDateFormat("MMM yyyy", Locale.forLanguageTag("id-ID")))
            }

            TimeRangeFilter.THREE_YEARS -> {
                cal.add(Calendar.YEAR, -3)
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis

                val buckets = mutableListOf<Long>()
                val bCal = Calendar.getInstance().apply { timeInMillis = start }
                // 12 quarters
                for (i in 0..12) {
                    buckets.add(bCal.timeInMillis)
                    bCal.add(Calendar.MONTH, 3)
                }

                Triple(start, buckets, SimpleDateFormat("MMM yyyy", Locale.forLanguageTag("id-ID")))
            }

            TimeRangeFilter.FIVE_YEARS -> {
                cal.add(Calendar.YEAR, -5)
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis

                val buckets = mutableListOf<Long>()
                val bCal = Calendar.getInstance().apply { timeInMillis = start }
                // 10 semi-annual points
                for (i in 0..10) {
                    buckets.add(bCal.timeInMillis)
                    bCal.add(Calendar.MONTH, 6)
                }

                Triple(start, buckets, SimpleDateFormat("yyyy", Locale.forLanguageTag("id-ID")))
            }
        }

        val validTransactions = transactions.filter { it.createdAt >= startOfPeriod }
        val totalAmount = validTransactions.sumOf { it.total }

        // Generate points with cumulative sums up to each bucket timestamp
        val points = bucketTimestamps.map { timestamp ->
            val cumulative = validTransactions
                .filter { it.createdAt <= timestamp }
                .sumOf { it.total }
            ChartPoint(
                timestamp = timestamp,
                formattedTime = timeFormatter.format(Date(timestamp)),
                amount = cumulative
            )
        }

        return ChartDataSet(
            totalAmount = totalAmount,
            periodLabel = filter.label,
            points = points
        )
    }
}
