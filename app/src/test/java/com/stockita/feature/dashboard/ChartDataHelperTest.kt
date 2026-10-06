package com.stockita.feature.dashboard

import com.stockita.core.database.entity.TransactionEntity
import com.stockita.feature.dashboard.model.ChartDataHelper
import com.stockita.feature.dashboard.model.TimeRangeFilter
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class ChartDataHelperTest {

    @Test
    fun `generateChartData produces valid points for all 8 time filters with empty transactions`() {
        val now = 1700000000000L // fixed timestamp

        TimeRangeFilter.entries.forEach { filter ->
            val dataSet = ChartDataHelper.generateChartData(
                transactions = emptyList(),
                filter = filter,
                now = now
            )

            assertEquals(0L, dataSet.totalAmount)
            assertEquals(filter.label, dataSet.periodLabel)
            assertTrue("Filter ${filter.code} should produce at least 2 points", dataSet.points.size >= 2)
            dataSet.points.forEach { point ->
                assertEquals(0L, point.amount)
                assertTrue(point.formattedTime.isNotBlank())
            }
        }
    }

    @Test
    fun `generateChartData computes cumulative amounts correctly for ONE_DAY filter`() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 6, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = cal.timeInMillis
        val at10am = startOfToday + 10 * 3600_000L
        val at16pm = startOfToday + 16 * 3600_000L
        val now = startOfToday + 20 * 3600_000L

        val transactions = listOf(
            TransactionEntity(id = 1L, createdAt = at10am, subtotal = 50000L, discount = 0L, total = 50000L, paymentMethod = "CASH"),
            TransactionEntity(id = 2L, createdAt = at16pm, subtotal = 75000L, discount = 0L, total = 75000L, paymentMethod = "QRIS"),
            // Yesterday's transaction should be ignored
            TransactionEntity(id = 3L, createdAt = startOfToday - 5000L, subtotal = 100000L, discount = 0L, total = 100000L, paymentMethod = "CASH")
        )

        val result = ChartDataHelper.generateChartData(
            transactions = transactions,
            filter = TimeRangeFilter.ONE_DAY,
            now = now
        )

        // Total should be 50,000 + 75,000 = 125,000 (ignoring yesterday)
        assertEquals(125000L, result.totalAmount)

        // Initial points before 10am should have 0 amount
        val pointAt06 = result.points.first { it.formattedTime.startsWith("06") }
        assertEquals(0L, pointAt06.amount)

        // Point at 12:00 should have 50,000 (after 10am transaction)
        val pointAt12 = result.points.first { it.formattedTime.startsWith("12") }
        assertEquals(50000L, pointAt12.amount)

        // End of day point should have all 125,000
        val lastPoint = result.points.last()
        assertEquals(125000L, lastPoint.amount)
    }

    @Test
    fun `generateChartData for ONE_WEEK computes daily cumulative totals`() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 3600_000L

        val transactions = listOf(
            TransactionEntity(id = 1L, createdAt = now - 3 * oneDayMillis, subtotal = 30000L, discount = 0L, total = 30000L, paymentMethod = "CASH"),
            TransactionEntity(id = 2L, createdAt = now - 1 * oneDayMillis, subtotal = 40000L, discount = 0L, total = 40000L, paymentMethod = "CASH")
        )

        val result = ChartDataHelper.generateChartData(
            transactions = transactions,
            filter = TimeRangeFilter.ONE_WEEK,
            now = now
        )

        assertEquals(70000L, result.totalAmount)
        assertEquals(7, result.points.size)
        // Last point has full sum
        assertEquals(70000L, result.points.last().amount)
    }
}
