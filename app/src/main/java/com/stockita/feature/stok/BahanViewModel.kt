package com.stockita.feature.stok

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.MaterialEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BahanUiState {
    object Loading : BahanUiState
    data class Success(val materials: List<MaterialEntity>) : BahanUiState
    data class Error(val message: String) : BahanUiState
}

@HiltViewModel
class BahanViewModel @Inject constructor(
    private val materialRepository: MaterialRepository
) : ViewModel() {

    val uiState: StateFlow<BahanUiState> = materialRepository.getAllMaterials()
        .map { BahanUiState.Success(it) }
        .catch { BahanUiState.Error(it.message ?: "Terjadi kesalahan") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BahanUiState.Loading
        )

    fun addMaterial(name: String, unit: String, minStock: Double) {
        viewModelScope.launch {
            val newMaterial = MaterialEntity(
                name = name,
                unit = unit,
                stock = 0.0,
                minStock = minStock,
                lastCost = 0L,
                categoryId = null
            )
            materialRepository.insertMaterial(newMaterial)
        }
    }
}
