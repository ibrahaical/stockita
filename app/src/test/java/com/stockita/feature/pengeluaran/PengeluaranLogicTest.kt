package com.stockita.feature.pengeluaran

import com.stockita.core.database.entity.ExpenseEntity
import com.stockita.fakes.FakeExpenseDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PengeluaranLogicTest {

    private lateinit var fakeExpenseDao: FakeExpenseDao
    private lateinit var expenseRepository: ExpenseRepository

    @Before
    fun setUp() {
        fakeExpenseDao = FakeExpenseDao()
        expenseRepository = ExpenseRepository(fakeExpenseDao)
    }

    @Test
    fun `insertExpense saves expense record with correct parameters`() = runTest {
        expenseRepository.insertExpense(
            title = "Beli Gas LPG 3kg",
            amount = 22000L,
            category = "Operasional",
            note = "Warung Bu Siti"
        )

        val expenses = expenseRepository.getAllExpenses().first()
        assertEquals(1, expenses.size)
        assertEquals("Beli Gas LPG 3kg", expenses[0].title)
        assertEquals(22000L, expenses[0].amount)
        assertEquals("Operasional", expenses[0].category)
        assertEquals("Warung Bu Siti", expenses[0].note)
    }

    @Test
    fun `deleteExpense removes the specified expense from records`() = runTest {
        val expense = ExpenseEntity(
            id = 1L,
            title = "Biaya Parkir",
            amount = 3000L,
            category = "Transport",
            note = null
        )
        fakeExpenseDao.insertExpense(expense)

        var expenses = expenseRepository.getAllExpenses().first()
        assertEquals(1, expenses.size)

        expenseRepository.deleteExpense(expense)
        expenses = expenseRepository.getAllExpenses().first()
        assertTrue(expenses.isEmpty())
    }
}
