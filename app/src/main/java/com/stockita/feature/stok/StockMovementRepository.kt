package com.stockita.feature.stok

import com.stockita.core.database.dao.StockDao
import com.stockita.core.database.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockMovementRepository @Inject constructor(
    private val stockDao: StockDao
) {
    fun getAllMovements(): Flow<List<StockMovementEntity>> = stockDao.getAllMovements()

    suspend fun adjustStock(
        targetType: String,
        targetId: Long,
        qty: Double,
        reason: String,
        refId: Long? = null
    ) {
        stockDao.adjustStock(
            targetType = targetType,
            targetId = targetId,
            qty = qty,
            reason = reason,
            refId = refId
        )
    }
}
