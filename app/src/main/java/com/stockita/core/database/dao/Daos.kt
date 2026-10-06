package com.stockita.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.stockita.core.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)
}

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAllMaterials(): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity): Long

    @Update
    suspend fun updateMaterial(material: MaterialEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transaction_items")
    fun getAllTransactionItems(): Flow<List<TransactionItemEntity>>
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isDone ASC, dueAt ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    @Update
    suspend fun updateTask(task: TaskEntity)
}

@Dao
abstract class CheckoutDao {
    @Insert
    abstract suspend fun insertTransaction(trx: TransactionEntity): Long

    @Insert
    abstract suspend fun insertItems(items: List<TransactionItemEntity>)

    @Insert
    abstract suspend fun insertMovements(movements: List<StockMovementEntity>)

    @Query("UPDATE products SET stock = stock + :qty WHERE id = :productId")
    abstract suspend fun adjustProductStock(productId: Long, qty: Double)

    @Query("UPDATE materials SET stock = stock + :qty WHERE id = :materialId")
    abstract suspend fun adjustMaterialStock(materialId: Long, qty: Double)

    @Transaction
    open suspend fun checkout(
        trx: TransactionEntity, 
        items: List<TransactionItemEntity>, 
        movements: List<StockMovementEntity>
    ) {
        val id = insertTransaction(trx)
        insertItems(items.map { it.copy(transactionId = id) })
        insertMovements(movements.map { it.copy(refId = id) })
        
        // Note: Realistically, we need to map movements to adjustProductStock or adjustMaterialStock here.
        // For Phase 1, keeping it simple. We will build the logic later.
        for (movement in movements) {
            if (movement.targetType == "PRODUCT") {
                adjustProductStock(movement.targetId, movement.qty) // qty is usually negative for sale
            } else if (movement.targetType == "MATERIAL") {
                adjustMaterialStock(movement.targetId, movement.qty)
            }
        }
    }
}

@Dao
abstract class StockDao {
    @Query("UPDATE materials SET stock = stock + :qty WHERE id = :materialId")
    abstract suspend fun updateMaterialStock(materialId: Long, qty: Double)

    @Query("UPDATE products SET stock = stock + :qty WHERE id = :productId")
    abstract suspend fun updateProductStock(productId: Long, qty: Double)

    @Insert
    abstract suspend fun insertStockMovement(movement: StockMovementEntity)

    @Query("SELECT * FROM stock_movements ORDER BY createdAt DESC")
    abstract fun getAllMovements(): Flow<List<StockMovementEntity>>

    @Transaction
    open suspend fun adjustStock(targetType: String, targetId: Long, qty: Double, reason: String, refId: Long? = null) {
        if (targetType == "MATERIAL") {
            updateMaterialStock(targetId, qty)
        } else if (targetType == "PRODUCT") {
            updateProductStock(targetId, qty)
        }
        
        insertStockMovement(StockMovementEntity(
            targetType = targetType,
            targetId = targetId,
            qty = qty,
            reason = reason,
            refId = refId
        ))
    }
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
}
