package com.stockita.feature.stok

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.StockMovementEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MutasiUiState {
    object Loading : MutasiUiState
    data class Success(val movements: List<StockMovementEntity>) : MutasiUiState
    data class Error(val message: String) : MutasiUiState
}

@HiltViewModel
class MutasiViewModel @Inject constructor(
    private val stockMovementRepository: StockMovementRepository
) : ViewModel() {

    val uiState: StateFlow<MutasiUiState> = stockMovementRepository.getAllMovements()
        .map { MutasiUiState.Success(it) }
        .catch { MutasiUiState.Error(it.message ?: "Terjadi kesalahan") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MutasiUiState.Loading
        )

    fun adjustStock(
        targetType: String,
        targetId: Long,
        qty: Double,
        reason: String
    ) {
        viewModelScope.launch {
            stockMovementRepository.adjustStock(
                targetType = targetType,
                targetId = targetId,
                qty = qty,
                reason = reason
            )
        }
    }
}
