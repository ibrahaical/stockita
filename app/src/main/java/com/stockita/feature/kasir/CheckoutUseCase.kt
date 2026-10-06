package com.stockita.feature.kasir

import com.stockita.core.database.entity.ProductEntity
import com.stockita.core.database.entity.StockMovementEntity
import com.stockita.core.database.entity.TransactionEntity
import com.stockita.core.database.entity.TransactionItemEntity
import com.stockita.feature.resep.RecipeRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

data class CartItem(
    val product: ProductEntity,
    val qty: Double
)

@Singleton
class CheckoutUseCase @Inject constructor(
    private val kasirRepository: KasirRepository,
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(cartItems: List<CartItem>, paymentMethod: String, note: String?) {
        if (cartItems.isEmpty()) return

        var subtotal = 0L
        val transactionItems = mutableListOf<TransactionItemEntity>()
        val stockMovements = mutableListOf<StockMovementEntity>()

        for (item in cartItems) {
            val product = item.product
            val itemTotal = (product.price * item.qty).toLong()
            subtotal += itemTotal

            // Calculate HPP based on recipe if stockMode is RECIPE, else assume 0 or from product
            var hppSnapshot = 0L
            if (product.stockMode == "RECIPE") {
                val recipeItems = recipeRepository.getRecipeForProduct(product.id).firstOrNull() ?: emptyList()
                var productHpp = 0.0
                for (rItem in recipeItems) {
                    productHpp += (rItem.qty * rItem.lastCost)
                    // Stock movement for material deduction
                    stockMovements.add(
                        StockMovementEntity(
                            targetType = "MATERIAL",
                            targetId = rItem.materialId,
                            qty = -(rItem.qty * item.qty),
                            reason = "SALE",
                            refId = null // Will be updated in DAO
                        )
                    )
                }
                hppSnapshot = productHpp.toLong()
            } else if (product.stockMode == "DIRECT") {
                // Stock movement for product deduction
                stockMovements.add(
                    StockMovementEntity(
                        targetType = "PRODUCT",
                        targetId = product.id,
                        qty = -item.qty,
                        reason = "SALE",
                        refId = null
                    )
                )
            }

            transactionItems.add(
                TransactionItemEntity(
                    transactionId = 0L, // Will be updated in DAO
                    productId = product.id,
                    nameSnapshot = product.name,
                    priceSnapshot = product.price,
                    hppSnapshot = hppSnapshot,
                    qty = item.qty
                )
            )
        }

        val trx = TransactionEntity(
            subtotal = subtotal,
            discount = 0L, // Keep simple for MVP
            total = subtotal,
            paymentMethod = paymentMethod,
            note = note
        )

        kasirRepository.performCheckout(trx, transactionItems, stockMovements)
    }
}
