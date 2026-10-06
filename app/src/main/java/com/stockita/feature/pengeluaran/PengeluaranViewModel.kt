package com.stockita.feature.pengeluaran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PengeluaranUiState {
    object Loading : PengeluaranUiState
    data class Success(val expenses: List<ExpenseEntity>) : PengeluaranUiState
    data class Error(val message: String) : PengeluaranUiState
}

@HiltViewModel
class PengeluaranViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    val uiState: StateFlow<PengeluaranUiState> = expenseRepository.getAllExpenses()
        .map { PengeluaranUiState.Success(it) }
        .catch { PengeluaranUiState.Error(it.message ?: "Terjadi kesalahan") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PengeluaranUiState.Loading
        )

    fun addExpense(title: String, amount: Long, category: String, note: String?) {
        viewModelScope.launch {
            expenseRepository.insertExpense(title, amount, category, note)
        }
    }
}
