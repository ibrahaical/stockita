package com.stockita.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.dao.TransactionDao
import com.stockita.core.database.entity.MaterialEntity
import com.stockita.core.database.entity.TaskEntity
import com.stockita.core.database.entity.TransactionEntity
import com.stockita.feature.pengeluaran.ExpenseRepository
import com.stockita.feature.stok.MaterialRepository
import com.stockita.feature.tugas.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val omzetHariIni: Long = 0L,
    val labaHariIni: Long = 0L,
    val stokMenipis: List<MaterialEntity> = emptyList(),
    val tugasHariIni: List<TaskEntity> = emptyList(),
    val allTransactions: List<TransactionEntity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val expenseRepository: ExpenseRepository,
    private val materialRepository: MaterialRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        transactionDao.getAllTransactions(),
        transactionDao.getAllTransactionItems(),
        expenseRepository.getAllExpenses(),
        materialRepository.getAllMaterials(),
        taskRepository.getAllTasks()
    ) { transactions, txItems, expenses, materials, tasks ->
        // Start of today calculation
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis

        // Omzet Hari Ini
        val trxToday = transactions.filter { it.createdAt >= startOfToday }
        val omzetToday = trxToday.sumOf { it.total }

        // Laba = Omzet - Total HPP (for today's txs) - Total Pengeluaran (for today)
        val trxTodayIds = trxToday.map { it.id }.toSet()
        val hppToday = txItems
            .filter { it.transactionId in trxTodayIds }
            .sumOf { it.hppSnapshot * it.qty.toLong() }
            
        val expenseToday = expenses
            .filter { it.createdAt >= startOfToday }
            .sumOf { it.amount }
            
        val labaToday = omzetToday - hppToday - expenseToday

        // Stok Menipis
        val stokMenipisList = materials.filter { it.stock <= it.minStock }

        // Tugas Hari ini (or overdue, not done)
        val tasksToday = tasks.filter { !it.isDone }

        DashboardUiState(
            isLoading = false,
            omzetHariIni = omzetToday,
            labaHariIni = labaToday,
            stokMenipis = stokMenipisList,
            tugasHariIni = tasksToday,
            allTransactions = transactions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )
}
