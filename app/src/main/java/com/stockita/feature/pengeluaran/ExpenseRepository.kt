package com.stockita.feature.pengeluaran

import com.stockita.core.database.dao.ExpenseDao
import com.stockita.core.database.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }

    suspend fun insertExpense(title: String, amount: Long, category: String, note: String?) {
        val expense = ExpenseEntity(
            title = title,
            amount = amount,
            category = category,
            note = note
        )
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }
}
