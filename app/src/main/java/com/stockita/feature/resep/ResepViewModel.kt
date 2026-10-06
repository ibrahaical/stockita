package com.stockita.feature.resep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.ProductEntity
import com.stockita.core.database.model.RecipeItemWithMaterial
import com.stockita.feature.produk.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResepUiState(
    val isLoading: Boolean = false,
    val products: List<ProductEntity> = emptyList(),
    val selectedProduct: ProductEntity? = null,
    val recipeItems: List<RecipeItemWithMaterial> = emptyList(),
    val totalHpp: Long = 0L,
    val error: String? = null
)

@HiltViewModel
class ResepViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
    private val hitungHppUseCase: HitungHppUseCase
) : ViewModel() {

    private val _selectedProductId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ResepUiState> = combine(
        productRepository.getAllProducts(),
        _selectedProductId.flatMapLatest { id ->
            if (id != null) recipeRepository.getRecipeForProduct(id) else flowOf(emptyList())
        },
        _selectedProductId
    ) { products, recipe, selectedId ->
        val selectedProduct = products.find { it.id == selectedId }
        val hpp = hitungHppUseCase(recipe)
        ResepUiState(
            isLoading = false,
            products = products.filter { it.stockMode == "RECIPE" },
            selectedProduct = selectedProduct,
            recipeItems = recipe,
            totalHpp = hpp
        )
    }.catch { 
        emit(ResepUiState(error = it.message)) 
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResepUiState(isLoading = true)
    )

    fun selectProduct(productId: Long) {
        _selectedProductId.value = productId
    }

    fun addRecipeItem(materialId: Long, qty: Double) {
        val productId = _selectedProductId.value ?: return
        viewModelScope.launch {
            recipeRepository.addRecipeItem(productId, materialId, qty)
        }
    }
}
