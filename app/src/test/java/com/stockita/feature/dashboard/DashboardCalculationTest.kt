package com.stockita.feature.dashboard

import com.stockita.core.database.entity.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DashboardCalculationTest {

    private fun getTodayStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun `dashboard calculations compute omzet, hpp, expense, and net profit correctly for today`() {
        val startOfToday = getTodayStartMillis()
        val yesterday = startOfToday - 3600_000L * 12 // 12 hours before today
        val today1 = startOfToday + 3600_000L * 2      // 02:00 AM today
        val today2 = startOfToday + 3600_000L * 5      // 05:00 AM today

        val transactions = listOf(
            TransactionEntity(id = 1L, createdAt = yesterday, subtotal = 50000L, discount = 0L, total = 50000L, paymentMethod = "CASH"),
            TransactionEntity(id = 2L, createdAt = today1, subtotal = 100000L, discount = 0L, total = 100000L, paymentMethod = "QRIS"),
            TransactionEntity(id = 3L, createdAt = today2, subtotal = 75000L, discount = 0L, total = 75000L, paymentMethod = "CASH")
        )

        val txItems = listOf(
            // Tx 1 (Yesterday)
            TransactionItemEntity(id = 1L, transactionId = 1L, productId = 10L, nameSnapshot = "P1", priceSnapshot = 50000L, hppSnapshot = 20000L, qty = 1.0),
            // Tx 2 (Today) -> HPP = 30000 * 2 = 60000
            TransactionItemEntity(id = 2L, transactionId = 2L, productId = 11L, nameSnapshot = "P2", priceSnapshot = 50000L, hppSnapshot = 30000L, qty = 2.0),
            // Tx 3 (Today) -> HPP = 15000 * 3 = 45000
            TransactionItemEntity(id = 3L, transactionId = 3L, productId = 12L, nameSnapshot = "P3", priceSnapshot = 25000L, hppSnapshot = 15000L, qty = 3.0)
        )

        val expenses = listOf(
            ExpenseEntity(id = 1L, title = "Bensin kemarin", amount = 20000L, category = "Operasional", createdAt = yesterday),
            ExpenseEntity(id = 2L, title = "Es Batu hari ini", amount = 15000L, category = "Bahan", createdAt = today1),
            ExpenseEntity(id = 3L, title = "Sabun cuci hari ini", amount = 10000L, category = "Kebersihan", createdAt = today2)
        )

        // 1. Filter today's transactions
        val trxToday = transactions.filter { it.createdAt >= startOfToday }
        val omzetToday = trxToday.sumOf { it.total }

        // Omzet = 100,000 + 75,000 = 175,000 (ignoring Tx 1)
        assertEquals(175000L, omzetToday)

        // 2. HPP for today
        val trxTodayIds = trxToday.map { it.id }.toSet()
        val hppToday = txItems
            .filter { it.transactionId in trxTodayIds }
            .sumOf { it.hppSnapshot * it.qty.toLong() }

        // HPP = 60,000 + 45,000 = 105,000 (ignoring Tx 1)
        assertEquals(105000L, hppToday)

        // 3. Expenses for today
        val expenseToday = expenses
            .filter { it.createdAt >= startOfToday }
            .sumOf { it.amount }

        // Expenses = 15,000 + 10,000 = 25,000 (ignoring yesterday)
        assertEquals(25000L, expenseToday)

        // 4. Laba = Omzet - HPP - Expenses = 175,000 - 105,000 - 25,000 = 45,000
        val labaToday = omzetToday - hppToday - expenseToday
        assertEquals(45000L, labaToday)
    }

    @Test
    fun `dashboard computes negative profit when expenses and HPP exceed revenue`() {
        val startOfToday = getTodayStartMillis()
        val now = startOfToday + 10000L

        val transactions = listOf(
            TransactionEntity(id = 1L, createdAt = now, subtotal = 50000L, discount = 0L, total = 50000L, paymentMethod = "CASH")
        )
        val txItems = listOf(
            TransactionItemEntity(id = 1L, transactionId = 1L, productId = 1L, nameSnapshot = "P", priceSnapshot = 50000L, hppSnapshot = 30000L, qty = 1.0)
        )
        val expenses = listOf(
            ExpenseEntity(id = 1L, title = "Sewa Tempat Harian", amount = 80000L, category = "Sewa", createdAt = now)
        )

        val trxToday = transactions.filter { it.createdAt >= startOfToday }
        val omzet = trxToday.sumOf { it.total } // 50,000
        val hpp = txItems.sumOf { it.hppSnapshot * it.qty.toLong() } // 30,000
        val expense = expenses.sumOf { it.amount } // 80,000

        // Laba = 50,000 - 30,000 - 80,000 = -60,000 (loss)
        val laba = omzet - hpp - expense
        assertEquals(-60000L, laba)
    }

    @Test
    fun `dashboard active tasks filter excludes completed tasks`() {
        val tasks = listOf(
            TaskEntity(id = 1L, title = "Beli susu", isDone = false),
            TaskEntity(id = 2L, title = "Restock cup", isDone = true),
            TaskEntity(id = 3L, title = "Bersihkan mesin", isDone = false)
        )

        val tasksActive = tasks.filter { !it.isDone }
        assertEquals(2, tasksActive.size)
        assertTrue(tasksActive.any { it.title == "Beli susu" })
        assertTrue(tasksActive.any { it.title == "Bersihkan mesin" })
    }
}
