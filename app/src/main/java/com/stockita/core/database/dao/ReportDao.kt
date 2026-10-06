package com.stockita.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.stockita.core.database.model.LabaRugiResult
import com.stockita.core.database.model.TopProductResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("""
        SELECT 
            (SELECT SUM(total) FROM transactions WHERE createdAt >= :startDate AND createdAt <= :endDate) as omzet,
            (SELECT SUM(hppSnapshot * qty) FROM transaction_items ti JOIN transactions tr ON ti.transactionId = tr.id WHERE tr.createdAt >= :startDate AND tr.createdAt <= :endDate) as hpp,
            (SELECT SUM(amount) FROM expenses WHERE createdAt >= :startDate AND createdAt <= :endDate) as expenses
    """)
    fun getLabaRugiSummary(startDate: Long, endDate: Long): Flow<LabaRugiResult>
    
    @Query("""
        SELECT 
            p.name as itemName, 
            SUM(ti.qty) as totalQty,
            SUM(ti.qty * ti.priceSnapshot) as totalRevenue
        FROM transaction_items ti
        JOIN transactions t ON ti.transactionId = t.id
        JOIN products p ON ti.productId = p.id
        WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate
        GROUP BY p.id
        ORDER BY totalQty DESC
    """)
    fun getTopProducts(startDate: Long, endDate: Long): Flow<List<TopProductResult>>
}
