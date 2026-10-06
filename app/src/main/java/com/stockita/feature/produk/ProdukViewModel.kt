package com.stockita.feature.produk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProdukUiState {
    object Loading : ProdukUiState
    data class Success(val products: List<ProductEntity>) : ProdukUiState
    data class Error(val message: String) : ProdukUiState
}

@HiltViewModel
class ProdukViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    val uiState: StateFlow<ProdukUiState> = productRepository.getAllProducts()
        .map { ProdukUiState.Success(it) }
        .catch { ProdukUiState.Error(it.message ?: "Terjadi kesalahan") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProdukUiState.Loading
        )

    fun addProduct(name: String, price: Long, stockMode: String) {
        viewModelScope.launch {
            val newProduct = ProductEntity(
                name = name,
                categoryId = null,
                price = price,
                stockMode = stockMode,
                stock = 0.0
            )
            productRepository.insertProduct(newProduct)
        }
    }
}
