package com.stockita.feature.kasir

import com.stockita.core.database.dao.CheckoutDao
import com.stockita.core.database.entity.StockMovementEntity
import com.stockita.core.database.entity.TransactionEntity
import com.stockita.core.database.entity.TransactionItemEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KasirRepository @Inject constructor(
    private val checkoutDao: CheckoutDao
) {
    suspend fun performCheckout(
        trx: TransactionEntity,
        items: List<TransactionItemEntity>,
        movements: List<StockMovementEntity>
    ) {
        checkoutDao.checkout(trx, items, movements)
    }
}
