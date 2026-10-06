package com.stockita.feature.kasir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.ProductEntity
import com.stockita.feature.produk.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface KasirUiState {
    object Loading : KasirUiState
    data class Success(val products: List<ProductEntity>) : KasirUiState
    data class Error(val message: String) : KasirUiState
}

@HiltViewModel
class KasirViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val checkoutUseCase: CheckoutUseCase
) : ViewModel() {

    val uiState: StateFlow<KasirUiState> = productRepository.getAllProducts()
        .map { KasirUiState.Success(it) }
        .catch { KasirUiState.Error(it.message ?: "Terjadi kesalahan") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = KasirUiState.Loading
        )

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

    private val _checkoutResult = MutableStateFlow<String?>(null)
    val checkoutResult: StateFlow<String?> = _checkoutResult

    fun addToCart(product: ProductEntity) {
        val current = _cart.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == product.id }
        if (existingIndex != -1) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(qty = existing.qty + 1.0)
        } else {
            current.add(CartItem(product, 1.0))
        }
        _cart.value = current
    }

    fun updateCartQty(product: ProductEntity, qty: Double) {
        val current = _cart.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == product.id }
        if (existingIndex != -1) {
            if (qty <= 0.0) {
                current.removeAt(existingIndex)
            } else {
                current[existingIndex] = current[existingIndex].copy(qty = qty)
            }
        }
        _cart.value = current
    }

    fun checkout(paymentMethod: String) {
        viewModelScope.launch {
            try {
                checkoutUseCase(_cart.value, paymentMethod, null)
                _cart.value = emptyList() // clear cart
                _checkoutResult.value = "Transaksi Berhasil"
            } catch (e: Exception) {
                _checkoutResult.value = "Gagal: ${e.message}"
            }
        }
    }

    fun clearCheckoutResult() {
        _checkoutResult.value = null
    }
}
